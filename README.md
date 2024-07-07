
## About

Widely used in industry and education, Git is probably the most popular and convenient source control system today. 
You only need to know a few Git CLI commands for this project, or you can use Git with the GUI wrapper. 
You don’t need to know what happens under the hood of Git when everything is working as it should,
but if something goes wrong it is very difficult to find a solution without knowing the underlying logic.


### Work on project. Stage 1/7:What is a Git object
What is a Git object
Description
Let's start with watching a comprehensive introductory video from Gitlab. You can also check out a written introduction to Git.

Here’s a recap of some key points you learned about Git from the introduction:
Git objects are stored in the .git/objects subdirectory of your project
Git objects are compressed with zlib
The file path contains a SHA-1 hash of the object contents
Let’s start with reading the simplest type of object, the blob object.

Note: you can use the git cat-file -p <get object hash> command to view the Git object contents.

Objectives
Write a program that asks the user for the path to the Git blob object, read the object file, decompress (inflate) it with zlib.
Print out the content of the file.
Pay attention that the file is using null terminated strings.
Java module java.util.zip contains the zlib inflator and deflator.
For a convenient method of inflating the compressed data, check out the Geeks for geeks article on the subject.
Note: null-terminated strings are a common data structure. Strings in C and C++ are stored in memory as sequences of characters followed by the character \x00 also known as NULL. No additional length information is stored. For example, "Hello World!\x00".

Example
The greater-than symbol followed by a space > represents the user input. Note that it's not part of the input.

        Enter git object location:
        > task/test/gitone/objects/61/8383db6d7ee3bd2e97b871205f113b6a3ba854
        blob 14
        Hello world!



### Work on project. Stage 2/7:Git object types
Git object types
Description
Git has three types of objects:

Blob stores file contents
Tree stores directory structure with filenames and subdirectories
Commit represents the snapshots of your project
Any Git object file starts with a header. The header is a null-terminated string of text containing the object type and size. (You should already be familiar with null-terminated strings from the previous stage.)

Objectives
Write a program that asks the user for the .git directory location and the Git object hash.
Find the file, the path to the file is as follows: first the git directory path, followed by objects folder, followed by a folder with the first two digits of the object hash and inside is located the object file that has the remaining digits of the hash as name. In summary: [git directory]/objects/[first two digits of hash]/[remaining digits of hash]
Output only the object header data, which contains the object type and size, using the format "type:[type] length:[length]".
Examples
Note that we will not use the .git folder in this project as it is impossible to store under Git. The .git folder contents needed for stage testing will be stored in the “test” folder.

The greater-than symbol followed by a space > represents the user input. Note that it's not part of the input.

Example 1

        Enter .git directory location:
        > /home/my_project/.git
        Enter git object hash:
        > 0eee6a98471a350b2c2316313114185ecaf82f0e
        type:commit length:216

Example 2

        Enter .git directory location:
        > task/test/gitone
        Enter git object hash:
        > 490f96725348e92770d3c6bab9ec532564b7ebe0
        type:blob length:85

Example 3

        Enter .git directory location:
        > task/test/gitone
        Enter git object hash:
        > a7b882bbf2db5d90287e9affc7e6f3b3c740b327
        type:tree length:35



### Work on project. Stage 3/7:Commits
Commits
Description
A commit is a snapshot of your project. A commit object contains the following information:

Filesystem tree
A list of parent commits
The author's name and email
Date and time when the commit was originally created
Committer's name and email (committer is the person who applied the commit)
Date and time when the commit was applied
Commit message

The commit object name, also known as "commit ID", is a SHA-1 hash of the commit object’s contents.
A commit may have no parent if it is the initial commit.
A commit may have two parents if it is a merge commit. In this case, the first parent is the preceding commit in the current branch, and the second parent is the preceding commit from the branch being merged.
The author and committer differ if the commit was cherry-picked or changed during a merge, rebase, or another operation.
The commit file structure is straightforward: the git object header is followed by plain text lines in the same order as in the description above.

Objectives
Your program now should detect the object type for the specified file, and print out the object’s type and contents.
The content should be reformatted as in the example. In this stage, you should support blobs and commits.
If the commit has no parent skip the parents line on output
If the commit has two parents join their hashes separated by " | ", keeping the same order that was in the file
You will also have to translate a Unix epoch timestamp to human readable date and time. You can construct a java.time.Instant object of the timestamp and use java.time.format.DateTimeFormatter to print it out.
It is guaranteed that a name (author or committer) does not contain white spaces in middle of the name
Commit messages may have multiple lines

Please keep in mind that commit messages usually have an empty line at the end.

Examples
The greater-than symbol followed by a space > represents the user input. Note that it's not part of the input.

Example 1

        Enter .git directory location:
        > task/test/gitone
        Enter git object hash:
        
        >490f96725348e92770d3c6bab9ec532564b7ebe0
        *BLOB*
        fun main() {
            while(true) {
                println("Hello Hyperskill student!")
            }
        }

Example 2

        Enter .git directory location:
        > task/test/gitone
        Enter git object hash:
        > 0eee6a98471a350b2c2316313114185ecaf82f0e
        *COMMIT*
        tree: 79401ddb0e2c0fe0472c813754dd4a8873b66a84
        parents: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0
        author: Smith mr.smith@matrix original timestamp: 2020-03-29 17:18:20 +03:00
        committer: Cypher cypher@matrix commit timestamp: 2020-03-29 17:25:52 +03:00
        commit message:
        get docs from feature1

Example 3

        Enter .git directory location:
        > task/test/gittwo
        Enter git object hash:
        > 31cddcbd00e715688cd127ad20c2846f9ed98223
        *COMMIT*
        tree: aaa96ced2d9a1c8e72c56b253a0e2fe78393feb7
        author: Kalinka Kali.k4@email.com original timestamp: 2021-12-11 22:31:36 -03:00
        committer: Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:31:36 -03:00
        commit message:
        simple hello

Example 4

        Enter .git directory location:
        > task/test/gittwo
        Enter git object hash:
        > dcec4e51e2ce4a46a6206d0d4ab33fa99d8b1ab5
        *COMMIT*
        tree: d128f76a96c56ac4373717d3fbba4fa5875ca68f
        parents: 5ad3239e54ba7c533d9f215a13ac82d14197cd8f | d2c5bedbb2c46945fd84f2ad209a7d4ee047f7f9
        author: Kalinka Kali.k4@email.com original timestamp: 2021-12-11 22:49:02 -03:00
        committer: Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:49:02 -03:00
        commit message:
        awsome hello

### Work on project. Stage 4/7:Trees
Trees
Description
Tree objects store the file name, and the SHA-1 hash of the file content which is the same as the blob file name for this file, or another tree object if it is a subdirectory. Tree objects can hold a group of files and directories.
Tree object file structure is a bit tricky to read. Just like any other Git object, a file tree object starts with a null-terminated header. The header is followed by one or more items consisting of: a permission metadata number, a whitespace, a filename, a null char and a 20-byte long binary SHA-1. Pay attention that there is no whitespace nor null char between the SHA-1 and the next item if there is one.

Objectives
Add support for reading tree objects to your current program
Convert the 20 byte long SHA-1 binary to hexadecimal lowercase string, which should be 40 digits long after convertion
While making the conversion zeropad the hex representation of a byte if it results in only one digit (ex: a byte with value 10 converts to "0a", 0 converts to "00" and 200 converts to "c8")

Example
The greater-than symbol followed by a space > represents the user input. Note that it's not part of the input.

        Enter .git directory location:
        > task/test/gitone
        Enter git object hash:
        > 109e8050b41bd10b81be0a51a5e67327f5609551
        *TREE*
        100644 2b26c15c04375d90203783fb4c2a45ff04b571a6 main.kt
        100644 f674b5d3a4c6cef5815b4e72ef2ea1bbe46b786b readme.txt
        40000 74198c849dbbcd51d060c59253a4757eedb9bd12 some-folder



### Work on project. Stage 5/7:Branches
Branches
Description
Lightweight branches are known as one of the best features of Git. In Git, a branch is just one commit object like the one you parsed in stage 3! Branches do not contain any duplicates of other branches.

The list of your local branches is typically stored in the .git/refs/heads directory. The file names in this folder are equal to branch names. The content in these files is equal to the commit ID of the head of the corresponding branch.
The current HEAD is stored in the .git/HEAD file.
ORIG_HEAD contains the last HEAD you worked on if you are currently in a “detached head” state.
The list of available branches can be accessed with the git branch -l command.

Objectives
Extend your program with command names. Use the cat-file command name for git-object file printing.

Add the list-branches command. This new command should print out local branch names accessible in the /refs/heads directory of the specified .git location. The branch list should be sorted in alphabetical order. Branch names should be preceded with * followed by one space for the current branch and two spaces for other branches.

Examples
The greater-than symbol followed by a space > represents the user input. Note that it's not part of the input.

Example 1

        Enter .git directory location:
        > task/test/gitone
        Enter command:
        > list-branches
        feature1
        feature2
        * master

Example 2

        Enter .git directory location:
        > task/test/gitone
        Enter command:
        > cat-file
        Enter git object hash:
        > 490f96725348e92770d3c6bab9ec532564b7ebe0
        *BLOB*
        fun main() {
            while(true) {
                println("Hello Hyperskill student!")
            }
        }


### Work on project. Stage 6/7:Git log
Git log
Description
What happens when you ask Git for the log using the git log command? Git iterates through the commits using parent links until it reaches a commit with no parents. This orphan commit is the initial commit for your repo.

Objectives
Extend your program with the log command.
It should iterate commits and print out a log for the specified branch.
Last commit should appear first on the log, and the initial commit should be the last.
If a commit has two parents print first the merged commit and add " (merged)" after the hash number (merged commit is the one that is coming from another branch).
Use the output format shown in the example.
Example
The greater-than symbol followed by a space > represents the user input. Note that it's not part of the input.

Example 1

        Enter .git directory location:
        > task/test/gitone
        Enter command:
        > log
        Enter branch name:
        feature2
        Commit: 97e638cc1c7135580c3ff93162e727148e1bad05
        Cypher cypher@matrix commit timestamp: 2020-03-29 17:27:35 +03:00
        break our software
        
        Commit: 0eee6a98471a350b2c2316313114185ecaf82f0e
        Cypher cypher@matrix commit timestamp: 2020-03-29 17:25:52 +03:00
        get docs from feature1
        
        Commit: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0
        Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:12:52 +03:00
        start kotlin project
        
        Commit: 73324685d9dbd1fdda87f3c5c6f77d79c1b769c2
        Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:10:52 +03:00
        initial commit

Example 2

        Enter .git directory location:
        > task/test/gittwo
        Enter command:
        > log
        Enter branch name:
        main
        Commit: dcec4e51e2ce4a46a6206d0d4ab33fa99d8b1ab5
        Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:49:02 -03:00
        awsome hello
        
        Commit: d2c5bedbb2c46945fd84f2ad209a7d4ee047f7f9 (merged)
        Ivan Petrovich@moon.org commit timestamp: 2021-12-11 22:43:54 -03:00
        hello of the champions
        
        Commit: 5ad3239e54ba7c533d9f215a13ac82d14197cd8f
        Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:46:28 -03:00
        maybe hello
        
        Commit: 31cddcbd00e715688cd127ad20c2846f9ed98223
        Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:31:36 -03:00
        simple hello



### Work on project. Stage 7/7:Full tree
Full tree
Description
Finally, let's try to get the full filesystem tree of the project for the specified commit.

As you remember from stage 4, a tree object contains the file and the subdirectory list for one folder. To get the full project tree, you should recursively iterate through tree objects.

Objectives
Extend your program with the commit-tree command.
Ask the user to specify the commit.
Print out the full file tree.

Example
The greater-than symbol followed by a space > represents the user input. Note that it's not part of the input.

        Enter .git directory location:
        > task/test/gitone
        Enter command:
        > commit-tree
        Enter commit-hash:
        > fd362f3f305819d17b4359444aa83e17e7d6924a
        main.kt
        readme.txt
        some-folder/qq.txt

