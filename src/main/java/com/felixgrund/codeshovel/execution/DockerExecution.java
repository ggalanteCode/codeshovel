package com.felixgrund.codeshovel.execution;

import com.felixgrund.codeshovel.execution.ShovelExecution;
import com.felixgrund.codeshovel.services.RepositoryService;
import com.felixgrund.codeshovel.services.impl.CachingRepositoryService;
import com.felixgrund.codeshovel.util.Utl;
import com.felixgrund.codeshovel.wrappers.Commit;
import com.felixgrund.codeshovel.wrappers.StartEnvironment;
import org.apache.commons.cli.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

import java.nio.file.Paths;
import java.util.Date;

import static com.felixgrund.codeshovel.execution.MainCli.newOption;

/**
 * Intended to be executed from the Dockerfile.
 *
 * The program should be started in the repository directory. Requires the relative path to the file to analyze and the
 * commit to start running at.
 *
 * cd /path/to/repo
 * java codeshovel relative/path/to/file.java sd8i3ksd823r4sdfkls83r48
 */
public class DockerExecution {

    private static String TARGET_FILE_PATH;
    private static String REPO_PATH;
    private static String START_COMMIT;
    private static String REPO_NAME;
    private static String METHOD_NAME;
    private static int METHOD_START_LINE;
    private static String OUTFILE;

    public static void main(String[] args) throws Exception {
            TARGET_FILE_PATH = args[2];
            START_COMMIT = args[1];
            REPO_PATH = args[0] + "/.git";
            REPO_NAME = Paths.get(REPO_PATH).getParent().getFileName().toString();
            METHOD_NAME = args[3];
            METHOD_START_LINE = Integer.parseInt(args[4]);


            System.out.println("Starting CodeShovel with REPO_NAME=" + REPO_NAME + " REPO_PATH=" + REPO_PATH +
                    " TARGET_FILE_PATH=" + TARGET_FILE_PATH + " START_COMMIT=" + START_COMMIT);

            long start = new Date().getTime();
            execute();
            long end = new Date().getTime();
            System.out.println("Time taken: " + (end - start) / 1000 + "sec");

    }

    private static void execute() throws Exception {
        String repositoryPath = REPO_PATH; //REPO_DIR + "/" + REPO + "/.git";
        OUTFILE = System.getProperty("user.dir") + "/" + REPO_NAME + "-" + METHOD_NAME + "-" + METHOD_START_LINE + ".json";
        Repository repository = Utl.createRepository(repositoryPath);
        Git git = new Git(repository);

        RepositoryService repositoryService = new CachingRepositoryService(git, repository, REPO_NAME, repositoryPath);

        Commit startCommit = repositoryService.findCommitByName(START_COMMIT);

        StartEnvironment env = new StartEnvironment(repositoryService);
        env.setFilePath(TARGET_FILE_PATH);
        env.setFileName(Utl.getFileName(TARGET_FILE_PATH));
        env.setStartCommitName(START_COMMIT);
        env.setStartCommit(startCommit);
        env.setOutputFilePath(OUTFILE);
        env.setFunctionName(METHOD_NAME);
        env.setFunctionStartLine(METHOD_START_LINE);
        ShovelExecution.runSingle(env, env.getFilePath(), false);
    }

    private static void usage() {
        System.out.println("Usage: <repopath>\n<startcommit>\n<targetfilepath>\n<methodname>\n<methodstartline>");
    }

}
