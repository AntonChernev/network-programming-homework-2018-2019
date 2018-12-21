package com.fmi.mpr.hw.http;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

class HttpResponse {
    private String protocol;
    private int statusCode;
    private HashMap<String, String> headers;
    private ArrayList<Byte> body;

    HttpResponse(String protocol) {
        this.protocol = protocol;
        headers = new HashMap<>();
        body = new ArrayList<>();
    }

    void send(OutputStream stream) throws IOException {
        PrintStream printStream = new PrintStream(stream);

        printStream.println(protocol + ' ' + statusCode);
        for(String key: headers.keySet()) {
            printStream.println(key + ": " + headers.get(key));
        }
        printStream.println();
        printStream.flush();

        for(byte element: body) {
            stream.write(element);
        }
    }

    String getProtocol() {
        return protocol;
    }

    int getStatusCode() {
        return statusCode;
    }

    void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    HashMap<String, String> getHeaders() {
        return headers;
    }

    void setHeader(String key, String value) {
        headers.put(key, value);
    }

    ArrayList<Byte> getBody() {
        return body;
    }

    void setBody(ArrayList<Byte> body) {
        this.body = body;
    }
}
