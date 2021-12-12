package pl.sggw.book;

import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String authorName;
    private String authorSurname;

    public Book(String _title, String _authorName, String _authorSurname) {
        title = _title;
        authorName = _authorName;
        authorSurname = _authorSurname;
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

    public String toString() {
        return getTitle() + ", " + getAuthorName() + ", " + getAuthorSurname();
    }

}