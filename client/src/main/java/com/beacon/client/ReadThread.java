package com.beacon.client;

import javafx.application.Platform;

import java.io.IOException;

public class ReadThread extends Thread {
    Client client;
    ChatWindow chatWindow;
    String message;

    public ReadThread(Client client, ChatWindow chatWindow) {
        this.client = client;
        this.chatWindow = chatWindow;
    }

    public void run() {
        while (client.isRunning) try {
            System.out.println("Reading...");
            message = client.reader.readLine();
            // Fixes java.lang.IllegalStateException: Not on FX application thread
            Platform.runLater(() -> chatWindow.appendUserMessage(message + "\n"));
            System.out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
