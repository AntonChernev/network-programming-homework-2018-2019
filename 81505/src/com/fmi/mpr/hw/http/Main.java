package com.fmi.mpr.hw.http;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(8888);
        server.start();
    }
}