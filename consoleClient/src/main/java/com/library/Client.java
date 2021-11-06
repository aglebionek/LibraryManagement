package com.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public final class Client {
    
    // method for getting input from the user
    private static String getCommand(BufferedReader input, String prompt) {
        System.out.print(prompt + " ");
        try {
            return input.readLine();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    //CLIENT
    
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Socket server = null;
        PrintWriter out = null;
        ObjectInputStream objIn = null;
        HashMap<Integer, Book> booksDictionary = null;
        BufferedReader commandInput = new BufferedReader(new InputStreamReader(System.in));

        server = new Socket("0.0.0.0", 8080);
        out = new PrintWriter(server.getOutputStream());

        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        objIn = new ObjectInputStream(server.getInputStream());
        booksDictionary = (HashMap<Integer, Book>) objIn.readObject();

        String commandPrompt = ">>";
        String startMessage = "Library Management System (LMS) v1.0\n" + 
            "type help to display the list of commands";
        String command = "";
        String response = "";

        System.out.println(startMessage);
        while (true) {
            command = getCommand(commandInput, commandPrompt);
            if (command.equals("exit")) {
                out.println(command);
                out.close();
                server.close();
                break;
            } else if (command.startsWith("all")){
                Printing.printAllBooks(booksDictionary, command);
            } else if (command.startsWith("help")) {
                out.println(command);
                out.flush();
                String line;
                while (!(line = in.readLine()).isEmpty()) {
                    response += line + "\n";
                }
                System.out.println(response);
                response = "";
            } else if (command.startsWith("find")) {
                out.println(command);
                out.flush(); 
                Printing.printBooks((HashMap<Integer, Book>) objIn.readObject());
            } else if (command.startsWith("status")) {
                command = command.substring(7);
                try {
                    Book book = booksDictionary.get(Integer.parseInt(command));
                    Printing.printBook(book);
                } catch (Exception e) {
                    System.out.println("No book with id " + command + " was found.");
                }
            }  else {
                out.println(command);
                out.flush(); 
                booksDictionary = (HashMap<Integer, Book>) objIn.readObject();
            }
        }
    }
}