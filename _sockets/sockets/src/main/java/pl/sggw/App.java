package pl.sggw;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class App {
    private static Integer PORT = 8080;
    private static String websiteString = "";

    static ConcurrentHashMap<Integer, Book> booksDictionary = new ConcurrentHashMap<>();
    static int highestBookId = 0;

    public static void main(String[] args) throws IOException {
        //read env port for future heroku deployment
        try {
            PORT = Integer.valueOf(System.getenv("PORT"));
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }

        ServerSocket serverSocket = new ServerSocket(PORT);

        //read example website from an html file to a String
        File websiteFile = new File("example.html");
        Scanner websiteScanner = new Scanner(websiteFile);
        while (websiteScanner.hasNextLine()) {
            websiteString += websiteScanner.nextLine();
        }
        websiteScanner.close();

        System.out.println("Server waiting for connections on http://localhost:" + PORT);

        do {
            //accept connections and send them to a new Thread
            final Socket connected = serverSocket.accept();
            Thread thread = new Thread(
                    new Runnable() {

                        @Override
                        public void run() {
                            try {
                                newClientThread(connected);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });
            thread.start();
        } while (true);
    }

    private static void newClientThread(Socket client) throws IOException {
        //get input/output streams for reading/sending requests/responses
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());

        //display request headers
        String line = "";
        while ((line = in.readLine()).length() > 0) {
            System.out.println("Wczytane: " + line);
        }

        //send response headers and body
        out.println("HTTP/1.1 200 OK");
        out.println("Connection: close");
        out.println("Content-Type: text/html");
        out.println("");
        out.println(websiteString);
        out.flush();
        out.close();
    }
}
