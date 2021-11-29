package com.library;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

/*
POST
            Map<String, String> parameters = new HashMap<>();
            parameters.put("param1", "val");
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            */

public class ClientHttp {

    // method for getting input from the user
    private static String getCommand(BufferedReader input, String prompt) {
        System.out.print(prompt + " ");
        try {
            return input.readLine();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        ConnectionHandler connection = new ConnectionHandler("http://localhost:8000/test");
        Map<String, String> params = new HashMap<String,String>();

        HashMap<Integer, Book>  booksDictionary = null;
        params.put("command", "all");
        connection.establishConnection(params);
        if (connection.getResponseCode() != 200) System.out.println("WARNING: Cannot connect to the books database. Starting system with empty database.");
        else booksDictionary = connection.responseObject();


        BufferedReader commandInput = new BufferedReader(new InputStreamReader(System.in));
        String command = "";
        String commandPrompt = ">>";
        String startMessage = "Library Management System (LMS) v2.0\n" +
                "type help to display the list of commands";

        System.out.println(startMessage);
        while (true) {
            command = getCommand(commandInput, commandPrompt);
            params.put("command", command);
            connection.establishConnection(params);
            if (connection.getResponseCode() != 200) break;

            if (command.equals("exit")) {
                connection.disconnect();
                break;
            } else {
                if (command.startsWith("help")) {
                    String response = connection.responseString();
                    System.out.println(response.toString());
                } else if (command.startsWith("all")) {
                    booksDictionary = connection.responseObject();
                    Printing.printAllBooks(booksDictionary, command);
                } else if (command.startsWith("find")) { 
                    Printing.printBooks((HashMap<Integer, Book>) connection.responseObject());
                } else {
                    try {
                    booksDictionary = connection.responseObject();
                        
                    } catch (EOFException e) {
                        System.out.println("WARNING: No command " + command);
                    }
                }
            }
        }
    }
}