package com.library;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.google.gson.Gson;

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
        } 
    }  
}
