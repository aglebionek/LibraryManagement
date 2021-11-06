package com.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
        }
        return booksDictionary;
    }
}
