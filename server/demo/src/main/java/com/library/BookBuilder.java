package com.library;

public class BookBuilder {
    private String _title;
    private String _author = "UnknownAuthor";
    private String _publisher = "UnknownPublisher";
    private BookStatus status = BookStatus.AVAILABLE;
    private int _id;

    public BookBuilder() {}

    public Book buildBook() {
        return new Book(_title, _author, _publisher, _id, BookStatus.AVAILABLE);
    }

    public BookBuilder title(String _title) {
        this._title = _title;
        return this;
    }

    public BookBuilder author(String _author) {
        this._author = _author;
        return this;
    }

    public BookBuilder publisher(String _publisher) {
        this._publisher = _publisher;
        return this;
    }

    public BookBuilder id(int _id) {
        this._id = _id;
        return this;
    }
}
