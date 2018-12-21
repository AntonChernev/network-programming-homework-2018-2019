package com.fmi.mpr.hw.http;

import java.net.*;
import java.io.*;

class HttpServer {
    private ServerSocket server;
    private final int port;
    private boolean isRunning;

    HttpServer(int port) throws IOException {
        server = new ServerSocket(port);
        this.port = port;
        isRunning = false;
    }

    private void listen() throws IOException {
        Socket connection = null;
        try {
            connection = server.accept();
            System.out.println("New connection: " + connection.getInetAddress());

            HttpRequest request = new HttpRequest();
            request.init(connection.getInputStream());

            HttpHandler handler = new HttpHandler(request);
            HttpResponse response = handler.process();

            response.send(connection.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Closing connection");
            if(connection != null) {
                connection.close();
            }
        }
    }

    private void run() {
        while(isRunning) {
            try {
                listen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    int getPort() {
        return port;
    }

    void start() {
        if(!isRunning) {
            isRunning = true;
            run();
        }
    }
}
