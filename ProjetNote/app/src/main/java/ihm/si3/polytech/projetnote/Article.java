package ihm.si3.polytech.projetnote;

/**
 * Created by user on 08/04/2018.
 */

public class Article {
    private int id;
    private String title;
    private String text;
    private String author;
    private String date;
    private CategoryPer categoryPer;
    private Media media;
    private String url;

    public Article(int id, String title, String text, String author, String date
            , CategoryPer categoryPer, Media media, String url){
        this.id = id;
        this.title = title;
        this.text = text;
        this.author = author;
        this.date =date;
        this.categoryPer = categoryPer;
        this.media = media;
        this.url = url;

    }

    @Override
    public String toString() {
        return title + " (" + author + ", " + date + ")";
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Media getMedia() {
        return media;
    }
}
