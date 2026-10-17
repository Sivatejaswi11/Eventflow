package texthack;

/**
 * A single event document loaded from src/datasets.
 * Each Article is one .txt file of the EventFlow corpus.
 */
public class Article {

    public int id;
    public String fileName;
    public String content;

    public Article(int id, String fileName, String content) {
        this.id = id;
        this.fileName = fileName;
        this.content = content;
    }
}
