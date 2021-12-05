package com.library;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.net.URL;
import java.net.URLEncoder;


public class ConnectionHandler {
    private static String URL;

    private static HttpURLConnection connection = null;

    public ConnectionHandler(String url) {
        URL = url;
    }

    public HttpURLConnection establishConnection(Map<String, String> params, Method method) throws IOException {
        URL urlToConnect = new URL(URL + "?" + getParamsString(params));
        connection = (HttpURLConnection) urlToConnect.openConnection();
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestMethod(method.toString());
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();
        return connection;
    }

    public byte[] responseBytes() throws IOException {
        return this.getInputStream().readAllBytes();
    }

    public String responseString() throws IOException {
        return new String(this.responseBytes());
    }

    public <T> T responseObject() throws IOException, ClassNotFoundException {
        byte bytes[] = this.responseBytes(); 
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream objIn = new ObjectInputStream(in);
        return (T) objIn.readObject();
    }

    public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public int getResponseCode() throws IOException {
        return connection.getResponseCode();
    }

    public InputStream getInputStream() throws IOException {
        return connection.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return connection.getOutputStream();
    }

    public void disconnect() {
        connection.disconnect();
    }    
}
