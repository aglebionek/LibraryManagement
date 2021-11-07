package com.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.json.XML;

public class ReadingDatabase {
    public static HashMap<Integer, Book> readBooksDictionary(DatabaseType databaseType) {
        HashMap<Integer, Book> booksDictionary = new HashMap<>();
        if (databaseType == DatabaseType.textfile) {
            try {
                FileInputStream fis = new FileInputStream("db");
                ObjectInputStream ois = new ObjectInputStream(fis);
                booksDictionary = (HashMap<Integer, Book>) ois.readObject();
                ois.close();
            } catch (Exception e) {
                System.out.println("WARNING: Cannot read the books database. Starting system with empty database.");
            }
        } else if (databaseType == DatabaseType.json) {
            try {
                Gson gson = new Gson();
                File myObj = new File("db.json");
                Scanner myReader = new Scanner(myObj);
                String json = "";
                while (myReader.hasNextLine()) {
                    json += myReader.nextLine();
                }
                myReader.close();
                booksDictionary = gson.fromJson(json, new TypeToken<HashMap<Integer, Book>>() {}.getType());
            } catch (Exception e) {
                System.out.println("WARNING: Cannot read the books database. Starting system with empty database.");
            }
        } else if (databaseType == DatabaseType.xml) {
            try {
                Gson gson = new Gson(); 
                File myObj = new File("db.xml");
                Scanner myReader = new Scanner(myObj);
                String xml = "";
                while (myReader.hasNextLine()) {
                    xml += myReader.nextLine();
                }
                myReader.close();
                JSONObject jsonObject = XML.toJSONObject(xml);
                String jsonString = jsonObject.toString();
                booksDictionary = gson.fromJson(jsonString, new TypeToken<HashMap<Integer, Book>>() {}.getType());
            } catch (Exception e) {
                System.out.println("WARNING: Cannot read the books database. Starting system with empty database.");
            }
        } else if (databaseType == DatabaseType.csv) {
            try {
                File myObj = new File("db.csv");
                Scanner myReader = new Scanner(myObj);
                String bookCSV = "";
                String[] bookCSVParams;
                int id;
                while (myReader.hasNextLine()) {
                    bookCSV = myReader.nextLine();
                    bookCSVParams = bookCSV.split(",");
                    id = Integer.parseInt(bookCSVParams[0]);
                    booksDictionary.put(
                        id,
                        new BookBuilder()
                        .id(id)
                        .title(bookCSVParams[1])
                        .author(bookCSVParams[2])
                        .publisher(bookCSVParams[3])
                        .buildBook()
                    );
                }
                myReader.close();
            } catch (Exception e) {
                System.out.println("WARNING: Cannot read the books database. Starting system with empty database.");
            }
            
        }
        return booksDictionary;
    }
}
