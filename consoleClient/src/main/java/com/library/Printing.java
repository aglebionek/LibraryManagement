package com.library;

import java.util.HashMap;

public class Printing {
    private static int idColumnLength = 5;
    private static int stringColumnLength = 30;
    private static int statusColumnLength = 11;

    private static String idColumnName = "[ID]";
    private static String titleColumnName = "[TITLE]";
    private static String authorColumnName = "[AUTHOR]";
    private static String publisherColumnName = "[PUBLISHER]";
    private static String statusColumnName = "[STATUS]";

    public static void printAllBooks(HashMap<Integer, Book> books, String command) {
        HashMap<Integer, Book> sorted = books;
        if (command.length() > 4) {
            command = command.substring(4);
            String[] arguments = command.split(",");
            for (String argument : arguments) {
                argument = argument.strip().toLowerCase();
                System.out.println(argument);;
                if (argument.equals("author")) {
                    sorted = Sorting.sortBy(sorted, new SortByAuthor());
                } else if (argument.equals("title")) {
                    sorted = Sorting.sortBy(sorted, new SortByTitle());
                } else if (argument.equals("publisher")) {
                    sorted = Sorting.sortBy(sorted, new SortByPublisher());
                } else if (argument.equals("status")) {
                    sorted = Sorting.sortBy(sorted, new SortByStatus());
                } else {
                    System.out.println("WARNING: Given sortBy argument is incorrect, sorting by id.");
                }
            }
            
        }
        printBooks(sorted);
   }

    public static void printBooks(HashMap<Integer, Book> books) {
        printLine(idColumnName, titleColumnName, authorColumnName, publisherColumnName, statusColumnName);
        for (Book book : books.values()) printBook(book);

    }

    public static void printBook(Book book) {
        printLine(String.valueOf(book.getId()), book.getTitle(), book.getAuthor(), book.getPublisher(), book.status.toString());
    }

    private static void printLine(String id, String title, String author, String publisher, String status) {
        printStringOfLength(id, idColumnLength);
        System.out.print("|");
        printStringOfLength(title, stringColumnLength);
        System.out.print("|");
        printStringOfLength(author, stringColumnLength);
        System.out.print("|");
        printStringOfLength(publisher, stringColumnLength);
        System.out.print("|");
        printStringOfLength(status, statusColumnLength);
        System.out.print("|\n");
    }

    private static void printStringOfLength(String string, int length) {
        int i = 0;
        for (char c : string.toCharArray()) {
            System.out.print(c);
            i++;
        }
        for(; i<length; i++) System.out.print(" ");
    }
}