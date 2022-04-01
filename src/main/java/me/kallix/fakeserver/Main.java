package me.kallix.fakeserver;

import me.kallix.fakeserver.connection.ServerConnection;

import java.net.InetAddress;

public final class Main {

    public static void main(String[] args) throws InterruptedException {

        ServerConnection connection = new ServerConnection();

        try {
            connection.bind(InetAddress.getByName("127.0.0.1"), 25569);
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("An error occurred");
        }

        Thread.sleep(Long.MAX_VALUE);
    }
}
