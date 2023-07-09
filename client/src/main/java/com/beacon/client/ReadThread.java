package com.beacon.client;

public class ReadThread extends Thread {
    Client client;
    ChatWindow chatWindow;

    public ReadThread(Client client, ChatWindow chatWindow) {
        this.client = client;
        this.chatWindow = chatWindow;
    }

    public void run() {
        while (true) {
            try {
                String message = client.reader.readLine();
                chatWindow.appendUserMessage(message);
                System.out.println(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
