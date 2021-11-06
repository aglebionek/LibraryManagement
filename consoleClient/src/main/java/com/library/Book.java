package com.library;

import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String author;
    private String publisher;
    private int id;
    public BookStatus status;

    public Book(String _title, String _author, String _publisher, int _id, BookStatus _status) {
        title = _title.toUpperCase();
        author = _author.toUpperCase();
        publisher = _publisher.toUpperCase();
        id = _id;
        status = _status;
    }

    public String getTitle() {
        return title;
    } 

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return String.format("[%d] ", id) + getTitle() + ", " + getAuthor() + ", " + getPublisher() + ", " + this.status;
    }

}