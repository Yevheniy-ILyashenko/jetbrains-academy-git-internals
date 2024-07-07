package gitinternals

class GitTree(gitObject: GitObject) {

    constructor(gitPath: String, hash: String): this(GitObject(gitPath, hash))

    data class File(val permissions: String, val name: String, val hash: String)

    val files: List<File>

    private val permissionsReadStage = 0
    private val fileNameReadStage = 1
    private val binarySHAReadStage = 2

    init {
        val filesRead = mutableListOf<File>()
        var stage = permissionsReadStage
        val contentRead = StringBuilder()
        var permissionsRead = ""
        var fileNameRead = ""
        val shaRead = IntArray(20)
        var shaReadArrayIndex = 0

        gitObject.readContent { byte ->
            contentRead.append(byte.toChar())
            val continueReading = when (stage) {
                permissionsReadStage -> {
                    if (contentRead.last() == ' ') {
                        permissionsRead = contentRead.toString().trim()
                        contentRead.clear()
                        stage = fileNameReadStage
                    }
                    true
                }

                fileNameReadStage -> {
                    if (byte == 0) {
                        contentRead.deleteAt(contentRead.lastIndex)
                        fileNameRead = contentRead.toString()
                        contentRead.clear()
                        stage = binarySHAReadStage
                    }
                    true
                }

                binarySHAReadStage -> {
                    shaRead[shaReadArrayIndex] = byte
                    if (shaReadArrayIndex < shaRead.lastIndex) {
                        shaReadArrayIndex++
                    } else {
                        val hex = shaRead.joinToString("") { "%02x".format(it) }
                        filesRead.add(File(permissionsRead, fileNameRead, hex))
                        shaReadArrayIndex = 0
                        contentRead.clear()
                        stage = permissionsReadStage
                    }
                    true
                }

                else -> false
            }

            continueReading
        }
        files = filesRead
    }
}