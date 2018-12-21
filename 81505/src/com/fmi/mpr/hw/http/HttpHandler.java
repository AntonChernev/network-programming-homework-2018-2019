package com.fmi.mpr.hw.http;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

class HttpHandler {
    private HttpRequest request;
    private HttpResponse response;

    HttpHandler(HttpRequest request) {
        this.request = request;
        response = new HttpResponse(request.getProtocol());
    }

    HttpResponse process() throws Exception {
        if(request.getMethod().equals("GET")) {
            String filename = "." + request.getRoute();
            if(request.getRoute().equals("/")) {
                filename += "index.html";
            }

            try {
                sendFile(filename);

                String extension = getExtension(filename);
                setContentType(extension);

                response.setStatusCode(200);
            } catch(Exception e) {
                e.printStackTrace();

                sendFile("./not_found.html");
                setContentType("html");
                response.setStatusCode(404);
            }
        }

        return response;
    }

    private String getExtension(String name) {
        try {
            String[] components = name.split("/");
            String[] words = components[components.length - 1].split("\\.");
            return words[words.length - 1];
        } catch (Exception e) {
            return "";
        }
    }

    private void setContentType(String extension) {
        switch(extension) {
            case "txt":
                response.setHeader("Content-Type", "text/plain");
                break;
            case "html":
                response.setHeader("Content-Type", "text/html");
                break;
            case "png":
                response.setHeader("Content-Type", "image/png");
                break;
            case "jpeg":
                response.setHeader("Content-Type", "image/jpeg");
                break;
            case "mp4":
                response.setHeader("Content-Type", "video/mp4");
                break;
        }
    }

    private void sendFile(String filename) throws Exception {
        Path filePath = Paths.get(filename);
        File file = filePath.toFile();
        InputStream fileStream = new FileInputStream(file);
        int length, maxLength = 1024;
        byte[] buffer = new byte[maxLength];
        ArrayList<Byte> body = new ArrayList<>();

        while((length = fileStream.read(buffer, 0, maxLength)) > 0) {
            for(int i = 0; i < length; i++) {
                body.add(buffer[i]);
            }
        }

        response.setBody(body);
    }
}
