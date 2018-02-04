package com.scapista;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import java.io.ByteArrayOutputStream;

import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;
import static java.lang.System.out;
import static org.eclipse.jgit.lib.Constants.HEAD;



/*public class App {
    public static void main(String[] args) throws java.io.IOException, GitAPIException {
        File repoDir = new File("/Users/scapista/Documents/Source/java-OOO/Course2/MOOCTextEditor/.git");

        // now open the resulting repository with a FileRepositoryBuilder
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try (Repository repository = builder
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build())
        {
            // The {tree} will return the underlying tree-id instead of the commit-id itself!
            // For a description of what the carets do see e.g. http://www.paulboxley.com/blog/2011/06/git-caret-and-tilde
            // This means we are selecting the parent of the parent of the parent of the parent of current HEAD and
            // take the tree-ish of it
            ObjectId oldHead = repository.resolve("HEAD^^{tree}");
            ObjectId head = repository.resolve("HEAD^{tree}");
            ObjectId oldCommitString = null;
            ObjectId newCommitString = null;

            PlotWalk revWalk = new PlotWalk(repository);
            ObjectId rootId = repository.resolve(HEAD);
            RevCommit root = revWalk.parseCommit(rootId);
            revWalk.markStart(root);
            PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<PlotLane>();
            plotCommitList.source(revWalk);
            plotCommitList.fillTo(Integer.MAX_VALUE);

            for(PlotCommit str: plotCommitList){
                //System.out.println(str.getName());
                if(oldCommitString == null) oldCommitString = str.toObjectId();
                else oldCommitString = newCommitString;
                newCommitString = str.toObjectId();
            }

            System.out.println("Printing diff between tree: " + oldHead + " and " + head);

            // prepare the two iterators to compute the diff between
            try (ObjectReader reader = repository.newObjectReader()) {
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, oldHead);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, head);

                // finally get the list of changed files
                try (Git git = new Git(repository)) {
                    List<DiffEntry> diffs = git.diff()
                            .setNewTree(newTreeIter)
                            .setOldTree(oldTreeIter)
                            .call();
                    for (DiffEntry entry : diffs) {
                        System.out.println(entry.toString());
                    }
                    System.out.println(git.getRepository());
                }
                try (Git git = new Git(repository)) {
                    newTreeIter.reset(reader, newCommitString);
                    oldTreeIter.reset(reader, oldCommitString);

                    List<DiffEntry> diff = git.diff().
                            setOldTree(oldTreeIter).
                            setNewTree(newTreeIter).
                            //setPathFilter(PathFilter.create("README.md")).
                            // to filter on Suffix use the following instead
                                    //setPathFilter(PathSuffixFilter.create(".java")).
                                    call();
                    for (DiffEntry entry : diff) {
                        System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
                        try (DiffFormatter formatter = new DiffFormatter(System.out)) {
                            formatter.setRepository(repository);
                            formatter.format(entry);
                        }
                    }
                }
            }
        }
    }
    private static AbstractTreeIterator prepareTreeParser(ObjectId objectId, Repository repository)
            throws IOException {
       Git git = new Git(repository);
        try( RevWalk walk = new RevWalk( git.getRepository() ) ) {
            RevCommit commit = walk.parseCommit( objectId );
            ObjectId treeId = commit.getTree().getId();
            try( ObjectReader reader = git.getRepository().newObjectReader() ) {
                return new CanonicalTreeParser( null, reader, treeId );
            }
        }
    }
*/

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

    import java.io.IOException;
    import java.util.List;
    import org.eclipse.jgit.api.Git;
    import org.eclipse.jgit.api.errors.GitAPIException;
    import org.eclipse.jgit.diff.DiffEntry;
    import org.eclipse.jgit.diff.DiffFormatter;
    import org.eclipse.jgit.lib.ObjectId;
    import org.eclipse.jgit.lib.ObjectReader;
    import org.eclipse.jgit.lib.Repository;
    import org.eclipse.jgit.revwalk.RevCommit;
    import org.eclipse.jgit.revwalk.RevTree;
    import org.eclipse.jgit.revwalk.RevWalk;
    import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
    import org.eclipse.jgit.treewalk.AbstractTreeIterator;
    import org.eclipse.jgit.treewalk.CanonicalTreeParser;
    import org.eclipse.jgit.treewalk.filter.PathFilter;


    /**
     * Simple snippet which shows how to show diffs between branches
     *
     * @author dominik.stadler at gmx.at
     */
    public class App {

        public static void main(String[] args) throws IOException, GitAPIException {
            String oldCommitString = null;
            String newCommitString = null;

            try (Repository repository = openJGitCookbookRepository()) {
                PlotWalk revWalk = new PlotWalk(repository);
                ObjectId rootId = repository.resolve(HEAD);
                RevCommit root = revWalk.parseCommit(rootId);
                revWalk.markStart(root);
                PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<PlotLane>();
                plotCommitList.source(revWalk);
                plotCommitList.fillTo(Integer.MAX_VALUE);

                for(PlotCommit str: plotCommitList){
                    //System.out.println(str.getName());
                    if(oldCommitString == null) oldCommitString = str.getName();
                    else oldCommitString = newCommitString;
                    newCommitString = str.getName();
                }

                // the diff works on TreeIterators, we prepare two for the two branches
                AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, oldCommitString);
                AbstractTreeIterator newTreeParser = prepareTreeParser(repository, newCommitString);

                // then the porcelain diff-command returns a list of diff entries
                try (Git git = new Git(repository)) {
                    List<DiffEntry> diff = git.diff().
                            setOldTree(oldTreeParser).
                            setNewTree(newTreeParser).
                            //setPathFilter(PathFilter.create("README.md")).
                            // to filter on Suffix use the following instead
                            //setPathFilter(PathSuffixFilter.create(".java"))
                            call();
                    ArrayList<String> diffList = new ArrayList<>();
                    for (DiffEntry entry : diff) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        //System.out.println("Entry: " + entry.getNewPath() + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
                        try (DiffFormatter formatter = new DiffFormatter(out)) {
                            formatter.setRepository(repository);
                            formatter.format(entry);
                            String diffTxt = out.toString("UTF-8");
                            diffList.add("-->" + entry.getNewPath());
                            diffList.add(diffTxt);
                            formatter.flush();
                        }

                    }

                    //System.out.println(entry.getNewPath() + " " + entry.getChangeType());
                    for (String str : diffList){
                        for (String innStr : str.split("\n")) {
                            if (innStr.startsWith("-->"))
                                out.println(innStr);
                            else if (innStr.contains("@@"))
                                out.println(innStr);
                        }
                    }
                }
            }
        }
        private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
            // from the commit we can build the tree which allows us to construct the TreeParser
            //noinspection Duplicates
            try (RevWalk walk = new RevWalk(repository)) {
                RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
                RevTree tree = walk.parseTree(commit.getTree().getId());

                CanonicalTreeParser treeParser = new CanonicalTreeParser();
                try (ObjectReader reader = repository.newObjectReader()) {
                    treeParser.reset(reader, tree.getId());
                }

                walk.dispose();

                return treeParser;
            }
        }
        public static Repository openJGitCookbookRepository() throws IOException {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            return builder
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();
        }
}
