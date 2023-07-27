package org.beacon.client;

import javafx.application.Platform;

import java.io.IOException;
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
            // Fixes java.lang.IllegalStateException: Not on FX application thread
            message = client.reader.readLine();
            Platform.runLater(() -> chatWindow.appendUserMessage(message + "\n"));
            waitForRunLater();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Waits until a JavaFX runLater call is finished.
     * @throws InterruptedException if the thread is interrupted
     */
    public static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }
}
