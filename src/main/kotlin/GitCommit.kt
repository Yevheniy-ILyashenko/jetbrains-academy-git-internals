package gitinternals

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class GitCommit(gitPath: String, val hash: String) {

    data class Action(val name: String, val time: ZonedDateTime?)

    private val content = GitObject(gitPath, hash)
    val tree: String
    val parents: List<String>
    val author: Action?
    val committer: Action?
    val message: String

    init {
        var readTree = ""
        val readParents = mutableListOf<String>()
        var authorRead: Action? = null
        var committerRead: Action? = null
        val commitMessageLines = mutableListOf<String>()
        var readMessage = false

        if (content.type == GitObject.Type.COMMIT) {
            content.readContent().forEach { line ->
                val split = line.split(" ")
                val prefix = split.getOrNull(0)
                when {
                    readMessage -> commitMessageLines.add(line)
                    prefix == "tree" -> readTree = split.getOrNull(1) ?: ""
                    prefix == "parent" -> split.getOrNull(1)?.let { readParents.add(it) }
                    prefix == "author" -> authorRead = action(line)
                    prefix == "committer" -> committerRead = action(line)
                    line == "" -> readMessage = true
                }
            }
        }

        tree = readTree
        parents = readParents
        author = authorRead
        committer = committerRead
        message = commitMessageLines.joinToString("\n")
    }

    private fun action(line: String): Action {
        val split = line.split(" ")
        val time = split.getOrNull(split.lastIndex - 1)?.toLongOrNull()?.let { Instant.ofEpochSecond(it) }
        return Action(
            name = split.drop(1).take(split.size - 3).joinToString(" ").replace("<", "").replace(">", ""),
            time = time?.atZone(ZoneOffset.of(split.getOrNull(split.lastIndex) ?: "+0000"))
        )
    }
}