package com.db.chat;

import java.io.*;
import java.net.Socket;

public class Client {
    private final String host;
    private final int port;
    InputStreamReader readerFromClient;
    OutputStreamWriter writerFromClient;

    public Client(String host, int port) {
        this(host, port, new InputStreamReader(System.in), new OutputStreamWriter(System.out));
    }

    public Client(String host, int port, InputStreamReader reader, OutputStreamWriter writer) {
        this.host = host;
        this.port = port;
        this.readerFromClient = reader;
        this.writerFromClient = writer;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid parameters.\nUsage: client <host> <port>");
            System.exit(-1);
        }
        try {
            Client client = new Client(args[0], Integer.parseInt(args[1]));
            System.out.println("Hello :-)");
            client.run();
        } catch (NumberFormatException e) {
            System.err.println("Port must be a number!");
            System.exit(-3);
        } catch (Throwable e) {
            System.err.println("Something get wrong! Shutting down.");
            System.exit(-2);
        }
    }

    public void run() {
        try (Socket socket = new Socket(host, port);
             BufferedWriter sockOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader consoleIn = new BufferedReader(readerFromClient);
             BufferedWriter consoleOut = new BufferedWriter(writerFromClient)) {

            ClientCycle clientCycle = new ClientCycle();
            clientCycle.communicate(consoleIn, sockOut, consoleOut, sockIn);
        } catch (IOException e) {
            System.out.println("No connection. Please, try to reconnect later");
            System.exit(0);
        }
    }
}
