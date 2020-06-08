package com.github

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.dircache.DirCacheBuildIterator
import org.eclipse.jgit.dircache.DirCacheIterator
import org.eclipse.jgit.dircache.DirCacheTree
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.FileTreeIterator
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
