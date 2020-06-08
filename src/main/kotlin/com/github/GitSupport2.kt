package com.github

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.FileTreeIterator
import org.eclipse.jgit.treewalk.WorkingTreeOptions
import org.eclipse.jgit.treewalk.filter.TreeFilter
import java.io.File
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        throw IllegalArgumentException("Missed repository path")
    }
    val repository: Repository = FileRepositoryBuilder().setGitDir(
            File(args[0]).resolve(".git")
    ).build()
    Git(repository).use { git ->

        DiffFormatter(System.out).apply {
            setRepository(repository)
            if (repository.config[WorkingTreeOptions.KEY].autoCRLF !== CoreConfig.AutoCRLF.FALSE) {
                setDiffComparator(AutoCRLFComparator())
            }
            pathFilter = TreeFilter.ALL
            scan(
                    prepareTreeParser(git.repository, Constants.HEAD),
                    FileTreeIterator(repository)
            ).forEach {
                format(it)
            }
        }
    }
}

fun prepareTreeParser(repository: Repository, ref: String): AbstractTreeIterator {
    val head: Ref = repository.exactRef(ref)
    RevWalk(repository).use { walk ->
        val commit: RevCommit = walk.parseCommit(head.objectId)
        val tree: RevTree = walk.parseTree(commit.tree.id)
        val treeParser = CanonicalTreeParser()
        repository.newObjectReader().use {
            reader -> treeParser.reset(reader, tree.id)
        }
        walk.dispose()
        return treeParser
    }
}
