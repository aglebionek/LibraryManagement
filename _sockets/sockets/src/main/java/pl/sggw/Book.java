package pl.sggw;

import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String authorName;
    private String authorSurname;
    private int id;

    public Book(String _title, String _authorName, String _authorSurname, int _id) {
        title = _title;
        authorName = _authorName;
        authorSurname = _authorSurname;
        id = _id;
    }

    public String getTitle() {
        return title;
    } 

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorSurname() {
        return authorSurname;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return String.format("[%d] ", id) + getTitle() + ", " + getAuthorName() + ", " + getAuthorSurname();
    }

}