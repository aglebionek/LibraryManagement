package pl.sggw;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RouteHandler {
    public static String returnViewForRoute(String method, String route, ConcurrentHashMap<Integer, Book> booksDictionary) throws FileNotFoundException {
        String view = ReadingViews.defaultView();
        if(route.isEmpty()) {
            return view;
        }
        else {
            try {
                view = ReadingViews.fromFile(route);
            } catch (Exception e) {
                System.out.println("No such view, returning default view.");
                return view;
            }

            if (route.equals("books.html")) {
                String booksAsHtml = PrintingDatabase.databaseToHtml(booksDictionary);
                view = String.format(view, booksAsHtml);
            }
        }
        return view;
    }
}
