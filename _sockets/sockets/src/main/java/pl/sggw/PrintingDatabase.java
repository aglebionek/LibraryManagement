package pl.sggw;

import java.util.concurrent.ConcurrentHashMap;

public class PrintingDatabase {
    private static int idColumnLength = 5;
    private static int stringColumnLength = 30;

    private static String idColumnName = "[ID]";
    private static String titleColumnName = "[TITLE]";
    private static String authorNameColumnName = "[AUTHOR]";
    private static String authorSurnameColumnName = "[PUBLISHER]";

    public static String printBook(Book book) {
        return "<p>" + printLine(String.valueOf(book.getId()), book.getTitle(), book.getAuthorName(), book.getAuthorSurname()) + "</p>";
    }

    private static String printLine(String id, String title, String authorName, String authorSurname) {
        return 
            String.format(
            "%1$-" + idColumnLength + "s|%2$-" + stringColumnLength + "s|%3$-" + stringColumnLength +
            "s|%4$-" + stringColumnLength + "s|", id, title, authorName, authorSurname
            );

    }

    public static String databaseToHtml(ConcurrentHashMap<Integer, Book> books) {
        String result = printLine(idColumnName, titleColumnName, authorNameColumnName, authorSurnameColumnName);
        for (Book book : books.values()) result += printBook(book);
        return result;
    }
}
