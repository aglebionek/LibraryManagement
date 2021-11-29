package com.library;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ServerSockets {
    static HashMap<Integer, Book> booksDictionary = new HashMap<>();
    static int highestBookId = 0;
    static DatabaseType databaseType = DatabaseType.csv;
    static String commandsFilename = "commands";
    static String commands = "";

        
    public static void main(String argv[]) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println(serverSocket.getLocalSocketAddress());
        System.out.println(serverSocket.getInetAddress());

        booksDictionary = ReadingDatabase.readBooksDictionary(databaseType);
        System.out.println(booksDictionary);
        try {
            highestBookId = Collections.max(booksDictionary.keySet());
        } catch (Exception e) {
            //TODO: handle exception
        }
        commands = readCommandsFromFile();    
    
        System.out.println("Server waiting for client on port " + serverSocket.getLocalPort());
        do {
            Socket connected = serverSocket.accept();
            new ServerSockets.clientThread(connected).start();
        } while (true);
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

    private static void sendStringToClient(PrintWriter out, String response) {
        for (String line : response.split("\n")) out.println(line);
        out.println("");
        out.flush();
    }

    private static void sendBooksDictionaryToClient(ObjectOutputStream objOut, HashMap<Integer, Book> booksDictionary) throws IOException {
        objOut.writeObject(booksDictionary);
        objOut.reset();
    }
    
    static class clientThread extends Thread {
        Socket client = null;
        BufferedReader in = null;
        PrintWriter out = null;
        ObjectOutputStream objOut = null;
        String command = "";
        String response = "";
    
        public clientThread(Socket clientSocket) {
            client = clientSocket;
        }
        
        @Override
        public void run() {
            System.out.println("New connection accepted " + client.getInetAddress() + ": " + client.getPort());

            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream());
                objOut = new ObjectOutputStream(client.getOutputStream());
                objOut.writeObject(booksDictionary);
                objOut.reset();
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }
    
            //while client is connected
            while (!this.client.isClosed()) {
    
                //wait for command
                try {
                    command = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
    
    
                if (command.equals("exit")) {
                    out.close();
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                } else {
                    if (command.startsWith("help")) {
                        sendStringToClient(out, commands);
                    } else if (command.startsWith("add")) {
                        command = command.substring(4);
                        String[] arguments = command.split(",");
                        highestBookId += 1;
                        Book book = new Book(arguments[0].trim(), arguments[1].trim(), arguments[2].trim(), highestBookId, BookStatus.AVAILABLE);
                        booksDictionary.put(highestBookId, book);
                        try {
                            UpdatingDatabase.updateDatabaseFile(booksDictionary, databaseType);
                            sendBooksDictionaryToClient(objOut, booksDictionary);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        response = book.toString() + "\n";
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
                        try {
                            sendBooksDictionaryToClient(objOut, booksToPrint);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if (command.startsWith("remove")) {
                        command = command.substring(7);
                        try {
                            Book book = booksDictionary.get(Integer.parseInt(command));
                            booksDictionary.remove(Integer.parseInt(command));
                            System.out.println("Book [" + book.getId() + "] removed successfully.");
                            UpdatingDatabase.updateDatabaseFile(booksDictionary, databaseType);
                        } catch (Exception e) {
                            System.out.println("No book with id " + command + " was found.");
                        }
                        try {
                            sendBooksDictionaryToClient(objOut, booksDictionary);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if (command.startsWith("return")) {
                        command = command.substring(7);
                        try {
                            Book book = booksDictionary.get(Integer.parseInt(command));
                            if (book.status == BookStatus.AVAILABLE) {
                                System.out.println("WARNING: Book is already returned.");
                            }
                            book.status = BookStatus.AVAILABLE;
                            UpdatingDatabase.updateDatabaseFile(booksDictionary, databaseType);
                        } catch (Exception e) {
                            System.out.println("No book with id " + command + " was found.");
                        }
                        try {
                            sendBooksDictionaryToClient(objOut, booksDictionary);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if (command.startsWith("borrow")) {
                        command = command.substring(7);
                        try {
                            Book book = booksDictionary.get(Integer.parseInt(command));
                            if (book.status == BookStatus.UNAVAILABLE) {
                                System.out.println("WARNING: Book is already borrowed.");
                            }
                            book.status = BookStatus.UNAVAILABLE;
                            UpdatingDatabase.updateDatabaseFile(booksDictionary, databaseType);
                        } catch (Exception e) {
                            System.out.println("No book with id " + command + " was found.");
                        }
                        try {
                            sendBooksDictionaryToClient(objOut, booksDictionary);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if (command.startsWith("all")) {
                        try {
                            sendBooksDictionaryToClient(objOut, booksDictionary);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                
            }
        }
    }
}