package pl.sggw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ReadingDatabase {
    // TODO rewrite for json with reflection
    public static ConcurrentHashMap<Integer, Book> readBooksDictionary() throws FileNotFoundException {
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
        for (int i = 0; i < entries.length-1; i++) {
            entries[i] += "}";
        }
        //printArray(entries);
        String[] keyValuePair;
        int id;
        String bookData;
        String[] bookProperties;
        String[] bookValues = new String[3];
        Book book;
        for (String entry : entries) {
            keyValuePair = entry.trim().split(":", 2);
            //printArray(keyValuePair);
            id = Integer.parseInt(keyValuePair[0].substring(1, keyValuePair[0].length() - 1));
            bookData = keyValuePair[1].trim().substring(1, keyValuePair[1].length() - 2);
            //System.out.println(bookData);
            bookProperties = bookData.trim().split(",");
            //System.out.println("properties");
            //printArray(bookProperties);
            int i = 0;
            for (String string : bookProperties) {
                String value = string.split(":")[1];
                bookValues[i] = value.trim().substring(1, value.length() - 2);
                i++;
            }
            //System.out.println("values" + bookValues);
            
            book = new Book(bookValues[0], bookValues[1], bookValues[2], id);
            booksDictionary.put(id, book);
        }
         
        
        return booksDictionary;
    }

    private static void printArray(String[] array) {
        String toPrint = "";
        for (String string : array) {
            toPrint += string + "\n";
        }
        System.out.println(toPrint);
    }
}
