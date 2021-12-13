package pl.sggw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ViewsReader {
    private static final String DEFAULT_VIEW_FILE = "default.html";
    public static String fromFile(String path) throws FileNotFoundException {
        String htmlString = "";
        File websiteFile = new File(path);
        Scanner websiteScanner = new Scanner(websiteFile);
        while (websiteScanner.hasNextLine()) {
            htmlString += websiteScanner.nextLine();
        }
        websiteScanner.close();
        return htmlString;
    }

    public static String defaultView() throws FileNotFoundException {
        return fromFile(DEFAULT_VIEW_FILE);
    }
}
