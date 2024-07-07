package gitinternals

import java.io.File
import java.time.format.DateTimeFormatter

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx")

fun getHead(gitPath: String): String {
    return File("$gitPath/HEAD").readText().split("/").let {
        it.getOrNull(it.lastIndex)?.trim()
    } ?: throw IllegalArgumentException("Head not found")
}

fun getLastCommit(gitPath: String, branch: String): String {
    return File("$gitPath/refs/heads/$branch").readText().let {
        "[0-9a-fA-F]{40}".toRegex().find(it)?.value
    } ?: throw IllegalArgumentException("Last commit not found")
}

private fun printGitObject(gitPath: String, objectHash: String) {
    val gitObject = GitObject(gitPath, objectHash)
    println("*${gitObject.type}*")
    when (gitObject.type) {
        GitObject.Type.TREE -> {
            val tree = GitTree(gitPath, objectHash)
            tree.files.forEach {
                println("${it.permissions} ${it.hash} ${it.name}")
            }
        }

        GitObject.Type.BLOB -> {
            println(gitObject.readContent().joinToString("\n"))
        }

        GitObject.Type.COMMIT -> {
            val commit = GitCommit(gitPath, objectHash)
            println("tree: ${commit.tree}")
            if (commit.parents.isNotEmpty())
                println("parents: ${commit.parents.joinToString(" | ")}")
            println("author: ${commit.author?.name} original timestamp: ${commit.author?.time?.format(timeFormatter)}")
            println("committer: ${commit.committer?.name} commit timestamp: ${commit.committer?.time?.format(timeFormatter)}")
            println("commit message:\n${commit.message}")
        }

        GitObject.Type.UNKNOWN -> {}
    }
}

fun printBranches(gitPath: String) {
    val headBranch = getHead(gitPath)
    val directory = File("$gitPath/refs/heads")
    val files = directory.listFiles()
    files?.map { it.name }?.sorted()?.forEach { branchName ->
        val prefix = if (branchName == headBranch) "*" else " "
        println("$prefix $branchName")
    }
}

fun printCommits(gitPath: String, branch: String) {
    var commitHash: String? = getLastCommit(gitPath, branch)
    while (commitHash != null) {
        val commit = GitCommit(gitPath, commitHash)
        printCommit(commit)
        if (commit.parents.size > 1) {
            printCommit(GitCommit(gitPath, commit.parents[1]), merged = true)
        }
        commitHash = commit.parents.getOrNull(0)
    }
}

fun printCommit(commit: GitCommit, merged: Boolean = false) {
    println("Commit: ${commit.hash}${if (merged) " (merged)" else ""}")
    println("${commit.committer?.name} commit timestamp: ${commit.committer?.time?.format(timeFormatter)}")
    println(commit.message)
}

fun printCommitTree(gitPath: String, commitHash: String) {
    val commit = GitCommit(gitPath, commitHash)
    printTree("", GitObject(gitPath, commit.tree))
}

fun printTree(name: String, gitObject: GitObject) {
    if (gitObject.type == GitObject.Type.BLOB)
        println(name)
    if (gitObject.type == GitObject.Type.TREE) {
        GitTree(gitObject).files.forEach {
            printTree(if (name.isBlank()) it.name else "$name/${it.name}", GitObject(gitObject.gitPath, it.hash))
        }
    }
}


fun main() {
    println("Enter .git directory location:")
    val gitPath = readln()
    println("Enter command:")
    val gitCommand = readln()

    when (gitCommand) {
        "list-branches" -> printBranches(gitPath)

        "log" -> {
            println("Enter branch name:")
            val branch = readln()
            printCommits(gitPath, branch)
        }

        "cat-file" -> {
            println("Enter git object hash:")
            val objectHash = readln()
            printGitObject(gitPath, objectHash)
        }

        "commit-tree" -> {
            println("Enter commit-hash:")
            val commitHash = readln()
            printCommitTree(gitPath, commitHash)
        }
    }
}
