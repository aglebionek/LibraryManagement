package com.library;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ServerHttp {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8000;
    private static final int BACKLOG = 1;

    private static final String HEADER_ALLOW = "Allow";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final int STATUS_OK = 200;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    private static final int NO_RESPONSE_LENGTH = -1;
    
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String ALLOWED_METHODS = METHOD_GET + "," + METHOD_OPTIONS + "," + METHOD_POST;

    private static String commandsFilename = "commands";
    private static String commands = "";

    private static final DatabaseType DATABASE_TYPE = DatabaseType.json;
    private static HashMap<Integer, Book> booksDictionary = new HashMap<>();
    private static int highestBookId = 0;

    private static HashMap<String, HashMap<Integer, Book>> user = new HashMap<>();


    public static void main(String[] args) throws Exception {
        booksDictionary = ReadingDatabase.readBooksDictionary(DATABASE_TYPE);
        highestBookId = Collections.max(booksDictionary.keySet());
        commands = readCommandsFromFile();
        HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); 
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(final HttpExchange httpExchange) throws IOException {
            final String requestMethod = httpExchange.getRequestMethod().toUpperCase();
            final OutputStream out = httpExchange.getResponseBody();
            final InputStream in = httpExchange.getRequestBody();

            byte[] response = {};


            switch (requestMethod) {
                case METHOD_POST:
                    DataInputStream dataInputStream = new DataInputStream(in);
                    byte[] bytes = dataInputStream.readAllBytes();
                    String str = new String(bytes);
                    System.out.println(str);
                    response = "Post recieved succesfully.".getBytes();
                    break;

                case METHOD_GET:
                    final Map<String, List<String>> requestParameters = getRequestParameters(httpExchange.getRequestURI());
                    String command = "";
                    try {
                        command = requestParameters.get("command").get(0);
                    } catch (NullPointerException e) {
                        response = "No command specified".getBytes();
                        httpExchange.sendResponseHeaders(STATUS_OK, response.length);
                        out.write(response);
                        out.close();
                    }

                    
                        if (command.startsWith("help")) {
                            response = commands.getBytes();
                        } else if (command.startsWith("all")) {
                            response = setObjectAsResponse(booksDictionary);
                        } else if (command.startsWith("find")) {
                            command = command.substring(4);
                            String[] arguments = command.replaceAll(",", "").split(" ");
                            //iterate over book database and if book matches the criteria add it to be printed
                            HashMap<Integer, Book> booksToPrint = new HashMap<>();
                            boolean writeEntry = true;
                            for (Map.Entry<Integer, Book> entry : booksDictionary.entrySet()) {
                                for(String argument : arguments) {
                                    if (!Pattern.matches(".*" + argument.toUpperCase() + ".*", entry.getValue().toString())) {
                                        writeEntry = false;
                                        break;
                                    }
                                }
                                if (writeEntry) booksToPrint.put(entry.getKey(), entry.getValue());
                                writeEntry = true;
                            }
                            System.out.println(booksToPrint);
                            response = setObjectAsResponse(booksToPrint);
                        } else if (command.startsWith("add")) {
                            command = command.substring(4);
                            String[] arguments = command.split(",");
                            highestBookId += 1;
                            Book book = new Book(arguments[0].trim(), arguments[1].trim(), arguments[2].trim(), highestBookId, BookStatus.AVAILABLE);
                            booksDictionary.put(highestBookId, book);
                            UpdatingDatabase.updateDatabaseFile(booksDictionary, DATABASE_TYPE);
                            response = setObjectAsResponse(booksDictionary);
                        } else if (command.startsWith("remove")) {
                            command = command.substring(7);
                            boolean success = true;
                            try {
                                Book book = booksDictionary.get(Integer.parseInt(command));
                                booksDictionary.remove(Integer.parseInt(command));
                                System.out.println("Book [" + book.getId() + "] removed successfully.");
                                UpdatingDatabase.updateDatabaseFile(booksDictionary, DATABASE_TYPE);
                            } catch (Exception e) {
                                response = new String("No book with id " + command + " was found.").getBytes();
                                success = false;
                            }
                            if (success) response = setObjectAsResponse(booksDictionary);
                        } else if (command.startsWith("return")) {
                            command = command.substring(7);
                            boolean success = true;
                            try {
                                Book book = booksDictionary.get(Integer.parseInt(command));
                                if (book.status == BookStatus.AVAILABLE) {
                                    System.out.println("WARNING: Book is already returned.");
                                }
                                book.status = BookStatus.AVAILABLE;
                                UpdatingDatabase.updateDatabaseFile(booksDictionary, DATABASE_TYPE);
                            } catch (Exception e) {
                                response = new String("No book with id " + command + " was found.").getBytes();
                                success = false;
                            }
                            if (success) response = setObjectAsResponse(booksDictionary);
                        } else if (command.startsWith("borrow")) {
                            command = command.substring(7);
                            boolean success = true;
                            try {
                                Book book = booksDictionary.get(Integer.parseInt(command));
                                if (book.status == BookStatus.UNAVAILABLE) {
                                    System.out.println("WARNING: Book is already borrowed.");
                                }
                                book.status = BookStatus.UNAVAILABLE;
                                UpdatingDatabase.updateDatabaseFile(booksDictionary, DATABASE_TYPE);
                            } catch (Exception e) {
                                response = new String("No book with id " + command + " was found.").getBytes();
                                success = false;
                            }
                            if (success) response = setObjectAsResponse(booksDictionary);
                        }
            }
            httpExchange.sendResponseHeaders(STATUS_OK, response.length);
            out.write(response);
            out.close();
        }
    }

    private static byte[] setObjectAsResponse(Object o) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream so = new ObjectOutputStream(bo);
        so.writeObject(o);
        so.flush();
        return bo.toByteArray();
    }

    private static String readCommandsFromFile() {
        String commands = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(commandsFilename));
            String line = br.readLine();
            while(line != null) {
                commands += line + "\n";
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            commands = "Error reading commands file\n";
        }
        return commands;
    }

    private static Map<String, List<String>> getRequestParameters(final URI requestUri) {

        final Map<String, List<String>> requestParameters = new LinkedHashMap<>();
        final String requestQuery = requestUri.getRawQuery();
        
        if (requestQuery != null) {
            final String[] rawRequestParameters = requestQuery.split("[&;]", -1);
            for (final String rawRequestParameter : rawRequestParameters) {
                final String[] requestParameter = rawRequestParameter.split("=", 2);
                final String requestParameterName = decodeUrlComponent(requestParameter[0]);
                requestParameters.putIfAbsent(requestParameterName, new ArrayList<>());
                final String requestParameterValue = requestParameter.length > 1 ? decodeUrlComponent(requestParameter[1]) : null;
                requestParameters.get(requestParameterName).add(requestParameterValue);
            }
        }
        return requestParameters;
    }

    private static String decodeUrlComponent(final String urlComponent) {
        try {
            return URLDecoder.decode(urlComponent, CHARSET.name());
        } catch (final UnsupportedEncodingException ex) {
            throw new InternalError(ex);
        }
    }
}