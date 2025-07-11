package com.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 8080;

        // Create a basic HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new HelloHandler());

        server.setExecutor(null); // Default executor
        server.start();

        System.out.println("✅ Server started at http://localhost:" + port + "/");
        
        // Optional: add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Server is stopping...");
            server.stop(0);
            System.out.println("✅ Server stopped.");
        }));
    }

    // Handler class
    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello from myapp!";
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
