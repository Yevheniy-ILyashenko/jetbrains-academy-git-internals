package gitinternals

import java.io.FileInputStream
import java.util.zip.InflaterInputStream

class GitObject(val gitPath: String, hash: String) {

    enum class Type { UNKNOWN, TREE, BLOB, COMMIT }

    private val path: String = "$gitPath/objects/${hash.take(2)}/${hash.drop(2)}"
    val type: Type = readType()

    private fun read(readBlock: (readByte: Int) -> Boolean) {
        runCatching {
            FileInputStream(path).use { file ->
                InflaterInputStream(file).use { inflatedStream ->
                    while (inflatedStream.available() > 0) {
                        val b = inflatedStream.read()
                        if (b == -1 || !readBlock(b)) break
                    }
                }
            }
        }
    }

    private fun readType(): Type {
        var typeRead: Type? = null
        var read = ""
        read { byte ->
            when (byte) {
                0 -> {
                    typeRead = when {
                        read.startsWith("tree") -> Type.TREE
                        read.startsWith("blob") -> Type.BLOB
                        read.startsWith("commit") -> Type.COMMIT
                        else -> Type.UNKNOWN
                    }
                }

                else -> read += byte.toChar()
            }
            typeRead == null
        }
        return typeRead ?: Type.UNKNOWN
    }

    fun readContent(): List<String> {
        var typePassed = false
        val contentRead = StringBuilder()
        read { byte ->
            if (typePassed)
                contentRead.append(byte.toChar())
            else if (byte == 0)
                typePassed = true
            true
        }
        return contentRead.lines()
    }

    fun readContent(readBlock: (readByte: Int) -> Boolean) {
        var typePassed = false
        read { byte ->
            if (typePassed)
                readBlock(byte)
            else if (byte == 0) {
                typePassed = true
                true
            } else
                true
        }
    }
}