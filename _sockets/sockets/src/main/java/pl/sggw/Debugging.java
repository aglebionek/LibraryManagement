package pl.sggw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Debugging {
    public static void printArray(String[] array) {
        String toPrint = "";
        for (String string : array) {
            toPrint += string + "\n";
        }
        System.out.println(toPrint);
    }

    public static void printHashMapToConsole(HashMap<String, String> hashMap) {
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static void printInputStreamToConsole(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = in.readLine()).length() > 0) {
            System.out.println(line);
        }
    }
}
