package pl.sggw.database;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pl.sggw.book.Book;

public class Serialize {
    public static String parseMapToJson(ConcurrentHashMap<Integer, Book> booksDictionary) throws IllegalArgumentException, IllegalAccessException, IOException {
        String json = "{";
        for (Map.Entry<Integer, Book> entry : booksDictionary.entrySet()) {
            int id = entry.getKey();
            Book book = entry.getValue();
            json+=addBook(id);
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            int i = 0;
            for (; i < fields.length-1; i++) {
                fields[i].setAccessible(true);
                json = addExpandableKVPair(json, fields[i].getName(), (String) fields[i].get(book));
                fields[i].setAccessible(false);

            }
            fields[i].setAccessible(true);
            json = addFinalKVPair(json, fields[i].getName(), (String) fields[i].get(book));
            fields[i].setAccessible(false);
        }
        json = json.substring(0, json.length()-1);
        json+="}";
        return json;
    }

    private static String addExpandableKVPair(String json, String key, String value) {
        return String.format(json, "\""+key+"\": \""+value+"\", %s");
    }

    private static String addFinalKVPair(String json, String key, String value) {
        return String.format(json, "\""+key+"\": \""+value+"\"");
    }

    private static String addBook(int keyId) {
        return "\""+keyId+"\": {%s},";
    }   

    public static void saveDatabaseToFile(ConcurrentHashMap<Integer, Book> booksDictionary) throws IOException, IllegalArgumentException, IllegalAccessException {
        FileWriter myWriter = new FileWriter("database.json");
        myWriter.write(parseMapToJson(booksDictionary));
        myWriter.close();
    }
}
