import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class gitHelper {

    /**
     * @param file is the path to the directory, in witch to search for git
     */
    static public Git createRepository(File file) throws GitAPIException {
        return Git.init().setDirectory(file).call();
    }

    static public Git searchForRepository(File file) throws IOException {
        return Git.open(file);
    }

    static public Git findOrCreate(File file) throws RuntimeException {
        try (Git git = searchForRepository(file)) {
            System.out.println("old Repository found");
            return git;
        } catch (Exception ex) {
            System.out.println("No old Repository found");
        }
        try (Git git = createRepository(file)) {
            System.out.println("created Repository");
            return git;
        } catch (Exception ex) {
            System.out.println("Could not create nor find Repository");
        }
        throw new RuntimeException();
    }

    static public void commitAll(Git git) throws GitAPIException {
        // Stage all files in the repo including new files, excluding deleted files
        git.add().addFilepattern(".").call();
        // Stage all changed files, including deleted files, excluding new files
        git.add().addFilepattern(".").setUpdate(true).call();
        // and then commit the changes.
        git.commit()
                .setMessage(String.valueOf(System.currentTimeMillis()))
                .call();
    }

    static public LinkedList<String> getAllCommits(Git git) {
        LinkedList<String> commitsList = new LinkedList<>();
        try {
            Iterable<RevCommit> commits = iterateAllCommits(git);
            for (RevCommit commit : commits) {
                commitsList.add("Date: " + commit.getAuthorIdent().getWhen());
            }
        } catch (Exception ex) {
            System.out.println("no Commits found");
        }
        return commitsList;
    }

    private static Iterable<RevCommit> iterateAllCommits(Git git) throws GitAPIException, IOException {
        Repository repo = git.getRepository();
        LogCommand log = git.log();
        ObjectId head = repo.resolve("HEAD");
        return log.add(head).call();
    }

    public class gitHelperInstance {


    }
}
