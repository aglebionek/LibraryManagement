package pl.sggw.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import pl.sggw.book.Book;

public class Deserialize {
    public static ConcurrentHashMap<Integer, Book> readDatabaseFromFile() throws FileNotFoundException {
        File file = new File("database.json");
        Scanner fileReader = new Scanner(file);
        String json = "";
        while (fileReader.hasNextLine()) {
            json += fileReader.nextLine();
        }
        fileReader.close();

        return parseJsonToMap(json);
    }



    private static ConcurrentHashMap<Integer, Book> parseJsonToMap(String json) {
        ConcurrentHashMap<Integer, Book> booksDictionary = new ConcurrentHashMap<>();
        json = json.substring(1, json.length() - 1);
        String[] entries = json.split("},");
        for (int i = 0; i < entries.length - 1; i++) {
            entries[i] += "}";
        }
        for (String entry : entries) {
            String[] keyValuePair = entry.trim().split(":", 2);
            int id = Integer.parseInt(keyValuePair[0].substring(1, keyValuePair[0].length() - 1));
            String bookData = keyValuePair[1].trim().substring(1, keyValuePair[1].length() - 2);
            String[] bookProperties = bookData.trim().split(",");
            int i = 0;
            String[] bookValues = new String[3];
            for (String string : bookProperties) {
                String value = string.split(":")[1];
                bookValues[i] = value.trim().substring(1, value.length() - 2);
                i++;
            }
            Book book = new Book(bookValues[0], bookValues[1], bookValues[2]);
            booksDictionary.put(id, book);
        }
        return booksDictionary;
    }

    
}
