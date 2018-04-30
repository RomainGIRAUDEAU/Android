package ihm.si3.polytech.projetnote;

/**
 * Created by user on 08/04/2018.
 */

public class Mishap {
    private int id;
    private String title;
    private String text;
    private String author;
    private String date;
    private State state;
    private Priority priority;
    private String dateStart;
    private String dateEnd;
    private String place;

    public Mishap(int id, String title, String text, String author, String date
            , State state, Priority priority, String dateStart, String dateEnd) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.author = author;
        this.state = state;
        this.priority = priority;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;

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

}
