package com.scapista;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ParseException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
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
import java.io.ByteArrayOutputStream;
import com.scapista.MethodParser.*;

import static java.lang.System.out;
import static org.eclipse.jgit.lib.Constants.HEAD;



public class App {

        public static void main(String[] args) throws IOException, GitAPIException,ParseException {
            try (Repository repository = openJGitCookbookRepository()) {
                String oldCommitString = null;
                String newCommitString = null;
                MethodParser mp = new MethodParser();

                for(PlotCommit str: getCommits(repository)){
                    if(oldCommitString == null) oldCommitString = str.getName();
                    else oldCommitString = newCommitString;
                    newCommitString = str.getName();
                }

                for (String str : formatGitChanages(repository,
                        prepareTreeParser(repository, oldCommitString),
                        prepareTreeParser(repository, newCommitString))
                        ){
                    for (String innStr : str.split("\n")) {
                        if (innStr.startsWith("-->")){
                            String tmpDir = repository.getDirectory().toString();
                            mp.setDirName(tmpDir.substring(0,tmpDir.indexOf(".git")));
                            System.out.println(innStr.substring(3));
                            mp.setFileName(innStr.substring(3));
                        }
                        else if (innStr.startsWith("@@")){
                            //out.println(innStr);
                            mp.printMethod(Integer.parseInt(innStr.substring(4,innStr.indexOf(','))));
                        }
                    }
                }
            }
        }
        private static PlotCommitList<PlotLane>  getCommits(Repository repository)throws IOException, GitAPIException{
            PlotWalk revWalk = new PlotWalk(repository);
            ObjectId rootId = repository.resolve(HEAD);
            RevCommit root = revWalk.parseCommit(rootId);
            revWalk.markStart(root);
            PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<PlotLane>();
            plotCommitList.source(revWalk);
            plotCommitList.fillTo(Integer.MAX_VALUE);
            return plotCommitList;
        }

        private static ArrayList<String> formatGitChanages(Repository repository,
                                 AbstractTreeIterator oldTreeParser,
                                 AbstractTreeIterator newTreeParser) throws IOException, GitAPIException{
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
                return diffList;
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
