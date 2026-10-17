package texthack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * MODULE 1 / CO1 - the data layer of EventFlow.
 *
 * Corpus reads every .txt file inside src/datasets (recursively) and stores it
 * as an Article inside the repository list. Every algorithm in the project runs
 * on the text stored here.
 *
 * Only plain file-IO classes are used (File, Files, IOException, ArrayList).
 * No algorithm logic lives in this class.
 */
public class Corpus {

    public static ArrayList<Article> repository = new ArrayList<>();
    static int articleID = 1;

    /** Corpus can also be run on its own to check that the dataset loads. */
    public static void main(String[] args) {

        String[] potentialPaths = {
            "src/datasets",
            "datasets",
            "bin/datasets",
            "EventFlow/src/datasets",
            "../src/datasets",
            "../datasets"
        };

        File folder = null;
        for (String path : potentialPaths) {
            File f = new File(path);
            if (f.exists() && f.isDirectory()) {
                folder = f;
                break;
            }
        }

        if (folder == null) {
            System.out.println("Folder not found!");
            System.out.println("Checked paths: src/datasets, datasets, bin/datasets");
            return;
        }

        loadFiles(folder);

        System.out.println("\nCorpus Loaded Successfully");
        System.out.println("Total Articles : " + repository.size());
        System.out.println();

        for (Article article : repository) {

            System.out.println("Article ID : " + article.id);
            System.out.println("File Name : " + article.fileName);
            System.out.println("Characters : " + article.content.length());
            System.out.println("------------------------------------");
        }
    }

    /**
     * Recursively walks the dataset folder and loads every .txt file.
     *
     * Time  : O(total number of characters in all files)
     * Space : O(total number of characters in all files)
     */
    public static void loadFiles(File folder) {

        File[] files = folder.listFiles();

        if (files == null)
            return;

        for (File file : files) {

            if (file.isDirectory()) {

                loadFiles(file);

            } else if (file.getName().endsWith(".txt")) {

                try {

                    String text = Files.readString(file.toPath());

                    repository.add(
                            new Article(
                                    articleID++,
                                    file.getName(),
                                    text));

                } catch (IOException e) {

                    System.out.println(
                            "Cannot read: " + file.getAbsolutePath());
                }
            }
        }
    }

    /** Returns the article with the given id, or null if it does not exist. */
    public static Article getArticle(int id) {

        for (Article article : repository) {
            if (article.id == id) {
                return article;
            }
        }
        return null;
    }

    /** Joins every article into one long text (used for corpus-wide search). */
    public static String combinedText() {

        StringBuilder builder = new StringBuilder();

        for (Article article : repository) {
            builder.append(article.content);
            builder.append('\n');
        }
        return builder.toString();
    }
}
