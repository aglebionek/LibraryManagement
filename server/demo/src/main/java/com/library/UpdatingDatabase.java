package com.library;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.XML;

public class UpdatingDatabase {
    public static void updateDatabaseFile(HashMap<Integer, Book> books, DatabaseType databaseType) throws IOException {
        if (databaseType == DatabaseType.textfile) {
            File yourFile = new File("db");
            yourFile.createNewFile();
            FileOutputStream fos = new FileOutputStream("db");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(books);
            oos.close();
        } else if (databaseType == DatabaseType.json) {
            Gson gson = new Gson(); 
            String json = gson.toJson(books);
            FileWriter myWriter = new FileWriter("db.json");
            myWriter.write(json);
            myWriter.close();
        } else if (databaseType == DatabaseType.xml) {
            Gson gson = new Gson(); 
            String json = gson.toJson(books);
            FileWriter myWriter = new FileWriter("db.xml");
            JSONObject jsonObject = new JSONObject(json);
            String xml = XML.toString(jsonObject);
            myWriter.write(xml);
            myWriter.close();
        } else if (databaseType == DatabaseType.csv) {
            String eol = System.getProperty("line.separator");

            try (FileWriter writer = new FileWriter("db.csv")) {
                for (Map.Entry<Integer, Book> entry : books.entrySet()) {
                    writer.append(entry.getKey().toString())
                        .append(',')
                        .append(entry.getValue().getTitle())
                        .append(',')
                        .append(entry.getValue().getAuthor())
                        .append(',')
                        .append(entry.getValue().getPublisher())
                        .append(',')
                        .append(entry.getValue().status.toString())
                        .append(eol);
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }  
}
