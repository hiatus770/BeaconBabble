package com.beacon;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    HttpServer httpserver;
    ThreadPoolExecutor threadPoolExecutor;

    public Server() throws IOException {
        // See Back Logging
        httpserver = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.httpserver.createContext("/test", new UserHandler());
        server.httpserver.setExecutor(server.threadPoolExecutor);
        server.httpserver.start();
    }
}
