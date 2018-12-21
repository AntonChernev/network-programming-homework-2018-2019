package com.fmi.mpr.hw.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.regex.*;

class HttpRequest {
    private String method;
    private String route;
    private String protocol;
    private HashMap<String, String> headers;
    private ArrayList<Byte> body;

    private byte[] streamBuffer;
    private int bufferIndex, bufferLegth, maxBufferLength = 512 * 512;

    HttpRequest() {
        method = route = protocol = "";
        headers = new HashMap<>();
        body = new ArrayList<>();

        bufferIndex = bufferLegth = 0;
        streamBuffer = new byte[maxBufferLength];
    }

    // Read a line of binary data (stop at bytes 13 10)
    private ArrayList<Byte> getLine(BufferedInputStream input) throws IOException {
        ArrayList<Byte> bytes = new ArrayList<>();
        byte last = 0, next;
        while(true) {
            if(bufferIndex == bufferLegth) {
                bufferLegth = input.read(streamBuffer, 0, maxBufferLength);
                bufferIndex = 0;
            }

            next = streamBuffer[bufferIndex++];
            if(last == 13 && next == 10) {
                bytes.remove(bytes.size() - 1);
                return bytes;
            } else {
                bytes.add(next);
            }
            last = next;
        }
    }

    // Convert bytes array list to string
    private String getString(ArrayList<Byte> bytes) {
        byte[] arr = new byte[bytes.size()];
        for(int i = 0; i < bytes.size(); i++) {
            arr[i] = bytes.get(i);
        }
        return new String(arr);
    }

    // Parse request
    void init(InputStream stream) throws Exception {
        BufferedInputStream input = new BufferedInputStream(stream);

        ArrayList<Byte> bytes = getLine(input);
        String line = getString(bytes);
        method = line.split(" ")[0];
        route = line.split(" ")[1];
        protocol = line.split(" ")[2];

        // Parse headers
        while(!(line = getString(getLine(input))).isEmpty()) {
            System.out.println(line);
        }

        if(method.equals("POST")) {
            // form data boundary
            String boundary = getString(getLine(input));

            // Content disposition
            String metadata = getString(getLine(input));
            Pattern pattern = Pattern.compile("filename=\"(.*?)\"");
            Matcher matcher = pattern.matcher(metadata);
            String filename;
            if(matcher.find()) {
                filename = matcher.group(1);
            } else {
                filename = "file";
            }
            headers.put("Custom-Filename", filename);

            // Other unneeded metadata
            while(!(line = getString(getLine(input))).isEmpty()) {
                System.out.println(line);
            }

            // File data
            while(true) {
                bytes = getLine(input);
                if(getString(bytes).equals(boundary)) {
                    break;
                }
                body.addAll(bytes);
            }
        }
    }

    String getMethod() {
        return method;
    }

    String getRoute() {
        return route;
    }

    String getProtocol() {
        return protocol;
    }

    HashMap<String, String> getHeaders() {
        return headers;
    }

    ArrayList<Byte> getBody() {
        return body;
    }
}
