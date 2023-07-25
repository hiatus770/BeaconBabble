package org.beacon.client;

import javafx.application.Platform;

import java.util.concurrent.Semaphore;

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
            // Fixes java.lang.IllegalStateException: Not on FX application thread
            message = client.reader.readLine();
            Platform.runLater(() -> chatWindow.appendUserMessage(message + "\n"));
            waitForRunLater();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Waits for the JavaFX thread to finish running.
     * @throws InterruptedException if the thread is interrupted
     */
    public static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }
}
