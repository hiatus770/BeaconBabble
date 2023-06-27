package com.beacon;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class UserHandler implements HttpHandler {

    private void handleResponse(HttpExchange exchange, String response) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>")
                .append("<body>")
                .append("<h1>")
                .append(response)
                .append("</h1>")
                .append("</body>")
                .append("</html>");

        String htmlResponse = htmlBuilder.toString();
        exchange.sendResponseHeaders(200, htmlResponse.length()); // what it do dawg
        outputStream.write(htmlResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    /**
     * Extracts the request parameter value contained within the URI.
     * Pretty sure this returns the requesting user's IP address but i guess we will find out
     * @param exchange
     * @return
     */
    private String handleGetRequest(HttpExchange exchange) {
        return exchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
    }

    private String handlePostRequest(HttpExchange exchange) {
        return "POST request";
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = null;

        if (exchange.getRequestMethod().equals("GET")) {
            response = handleGetRequest(exchange);
        } else if (exchange.equals("POST")) {
            response = handlePostRequest(exchange);
        }
        handleResponse(exchange, response);
    }
}
