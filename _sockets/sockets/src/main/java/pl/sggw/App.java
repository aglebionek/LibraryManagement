package pl.sggw;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class App {
    private static final String REQUEST_METHOD_HEADER_KEY = "Method";
    private static final String REQUEST_URL_HEADER_KEY = "Url";
    private static final String RESPONSE_HEADERS = "HTTP/1.1 200 OK\nConnection: close\nContent-Type: text/html\n\n";
    private static final String RESPONSE_CODE_200 = "200 OK";
    private static final String RESPONSE_CODE_404 = "404 Not Found";

    private static Integer PORT = 8080;
    private static String responseView = "";

    private static ConcurrentHashMap<Integer, Book> booksDictionary = new ConcurrentHashMap<>();
    private static int highestBookId = 0;

    public static void main(String[] args) throws IOException {
        //read env port for future heroku deployment
        try {
            PORT = Integer.valueOf(System.getenv("PORT"));
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }

        ServerSocket serverSocket = new ServerSocket(PORT);
        booksDictionary = ReadingDatabase.readBooksDictionary();
        System.out.println(booksDictionary);

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
        //get output stream for sending responses
        PrintWriter out = new PrintWriter(client.getOutputStream());
        
        HashMap<String, String> requestHeaders = getRequestHeaders(client.getInputStream());

        String method = requestHeaders.get(REQUEST_METHOD_HEADER_KEY);
        String route = requestHeaders.get(REQUEST_URL_HEADER_KEY);

        responseView = RouteHandler.returnViewForRoute(method, route, booksDictionary);

        //send response headers and body
        out.print(RESPONSE_HEADERS);
        out.println(responseView);
        out.flush();
        out.close();
    }

    private static HashMap<String, String> getRequestHeaders(InputStream inputStream) throws IOException {
        HashMap<String, String> result = new HashMap<String, String>();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String line = in.readLine();
        String[] methodAndUrl = line.split(" ");
        result.put("Method", methodAndUrl[0]);
        result.put("Url", methodAndUrl[1].substring(1, methodAndUrl[1].length()));
        while ((line = in.readLine()).length() > 0) {
            String[] keyValuePair = line.split(":", 2);
            result.put(keyValuePair[0].trim(), keyValuePair[1].trim());
        }
        return result;
    }

    private static void printHashMapToConsole(HashMap<String, String> hashMap) {
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}