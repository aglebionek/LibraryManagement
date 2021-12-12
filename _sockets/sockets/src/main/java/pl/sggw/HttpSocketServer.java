package pl.sggw;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import pl.sggw.book.Book;
import pl.sggw.database.Deserialize;
import pl.sggw.database.Serialize;

public class HttpSocketServer {
    private static final String REQUEST_METHOD_HEADER_KEY = "Method";
    private static final String REQUEST_URL_HEADER_KEY = "Url";
    private static final String REQUEST_CONTENT_LENGTH_HEADER_KEY = "Content-Length";
    private static final String REQUEST_BODY_KEY = "Body";

    private static final String RESPONSE_HEADERS = "HTTP/1.1 200 OK\nConnection: close\nContent-Type: text/html\n\n";
    private static final String RESPONSE_CODE_200 = "200 OK";
    private static final String RESPONSE_CODE_404 = "404 Not Found";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static Integer PORT = 8080;
    private static String responseView = "";

    private static ConcurrentHashMap<Integer, Book> booksDictionary = new ConcurrentHashMap<>();
    private static int highestBookId = 0;

    public static void main(String[] args) throws IOException, IllegalArgumentException, IllegalAccessException {
        // read env port for future heroku deployment
        try {
            PORT = Integer.valueOf(System.getenv("PORT"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServerSocket serverSocket = new ServerSocket(PORT);
        booksDictionary = Deserialize.readDatabaseFromFile();
        highestBookId = Collections.max(booksDictionary.keySet());

        System.out.println("Server waiting for connections on http://localhost:" + PORT);

        do {
            // accept connections and send them to a new Thread
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
        PrintWriter out = new PrintWriter(client.getOutputStream());
        HashMap<String, String> requestHeaders = RequestHandler.Parser.getRequestHeaders(client.getInputStream());

        String method = requestHeaders.get(REQUEST_METHOD_HEADER_KEY);
        String route = requestHeaders.get(REQUEST_URL_HEADER_KEY);

        if (method.toUpperCase().equals("POST")) {
            HashMap<String, String> requestBody = RequestHandler.Parser.getRequestBody(requestHeaders.get(REQUEST_BODY_KEY));
            Debugging.printHashMapToConsole(requestBody);
            responseView = RequestHandler.Post.returnViewForRoute(route, requestBody, booksDictionary);
        } else if (method.toUpperCase().equals("GET")) {
            responseView = RequestHandler.Get.returnViewForRoute(route, booksDictionary);
        }

        // send response headers and body
        out.print(RESPONSE_HEADERS);
        out.println(responseView);
        out.flush();
        out.close();
    }

    
}