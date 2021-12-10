package pl.sggw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadingViews {
    private static final String DEFAULT_VIEW_FILE = "default.html";
    public static String fromFile(String path) throws FileNotFoundException {
        String websiteString = "";
        File websiteFile = new File(path);
        Scanner websiteScanner = new Scanner(websiteFile);
        while (websiteScanner.hasNextLine()) {
            websiteString += websiteScanner.nextLine();
        }
        websiteScanner.close();
        return websiteString;
    }

    public static String defaultView() throws FileNotFoundException {
        return fromFile(DEFAULT_VIEW_FILE);
    }
}
