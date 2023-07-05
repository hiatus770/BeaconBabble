package com.beacon.client;

public class ReadThread extends Thread {
    Client client;

    public ReadThread(Client client) {
        this.client = client;
    }

    public void run() {
        while (true) {
            try {
                String message = client.reader.readLine();
                System.out.println(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
