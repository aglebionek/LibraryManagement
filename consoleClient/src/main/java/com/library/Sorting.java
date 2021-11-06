package com.library;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class SortByAuthor implements Comparator<Map.Entry<Integer, Book>> {
    public int compare(Map.Entry<Integer, Book> entry1, Map.Entry<Integer, Book> entry2)
    {
        return entry1.getValue().getAuthor().compareTo(entry2.getValue().getAuthor());
    }
}

class SortByTitle implements Comparator<Map.Entry<Integer, Book>> {
    public int compare(Map.Entry<Integer, Book> entry1, Map.Entry<Integer, Book> entry2)
    {
        return entry1.getValue().getTitle().compareTo(entry2.getValue().getTitle());
    }
}

class SortByPublisher implements Comparator<Map.Entry<Integer, Book>> {
    public int compare(Map.Entry<Integer, Book> entry1, Map.Entry<Integer, Book> entry2)
    {
        return entry1.getValue().getPublisher().compareTo(entry2.getValue().getPublisher());
    }
}

class SortByStatus implements Comparator<Map.Entry<Integer, Book>> {
    public int compare(Map.Entry<Integer, Book> entry1, Map.Entry<Integer, Book> entry2)
    {
        return entry1.getValue().status.toString().compareTo(entry2.getValue().status.toString());
    }
}

class Sorting {
    public static HashMap<Integer, Book> sortBy(HashMap<Integer, Book> books, Comparator<Map.Entry<Integer, Book>> cmp) {
        List<Map.Entry<Integer, Book>> list = new LinkedList<Map.Entry<Integer, Book>>(books.entrySet());
        Collections.sort(list, cmp);
        HashMap<Integer, Book> sorted = new LinkedHashMap<Integer, Book>();
        for(Map.Entry<Integer, Book> book : list) sorted.put(book.getKey(), book.getValue());
        return sorted;
    }
}

