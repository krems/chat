package com.db.chat;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private final AtomicBoolean run = new AtomicBoolean(true);
    private final String host;
    private final int port;
    private final InputStreamReader readerFromClient;
    private final OutputStreamWriter writerFromClient;
    private Socket socket;

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
            client.start();
        } catch (NumberFormatException e) {
            System.err.println("Port must be a number!");
            System.exit(-2);
        } catch (Throwable e) {
            System.err.println("Something get wrong! Shutting down.");
            System.exit(-3);
        }
    }

    public void start() {
        try (Socket socket = new Socket(host, port);
             BufferedWriter socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader consoleIn = new BufferedReader(readerFromClient);
             BufferedWriter consoleOut = new BufferedWriter(writerFromClient)) {
            this.socket = socket;
            Thread senderThread = new Thread(new MessageReceiver(consoleOut, socketIn));
            senderThread.setDaemon(true);
            senderThread.start();
            listenConsole(consoleIn, socketOut);
        } catch (IOException e) {
            System.out.println("No connection. Please, try to reconnect later");
        }
    }

    private void listenConsole(BufferedReader consoleIn, BufferedWriter socketOut) {
        try {
            Writer sender = new Writer(socketOut);
            while (run.get()) {
                String message = consoleIn.readLine();
                if (message == null || "/q".equals(message) || "/quit".equals(message) || "/exit".equals(message)) {
                    System.out.println("Shutting down. Bye! :-)");
                    stop();
                    return;
                }
                sender.send(message);
            }
        } catch (IOException e) {
            System.out.println("Interaction error. Please, try to reconnect");
            stop();
        }
    }

    private class MessageReceiver implements Runnable {

        private final BufferedWriter consoleOut;
        private final BufferedReader socketIn;

        private MessageReceiver(BufferedWriter consoleOut, BufferedReader socketIn) {
            this.consoleOut = consoleOut;
            this.socketIn = socketIn;
        }

        @Override
        public void run() {
            Writer writer = new Writer(consoleOut);
            try {
                while (run.get()) {
                    String response = socketIn.readLine();
                    if (response == null) {
                        stop();
                        return;
                    }
                    writer.send(response);
                }
            } catch (IOException e) {
                System.out.println("Interaction error. Please, try to reconnect");
                stop();
            }
        }
    }

    public void stop() {
        try {
            readerFromClient.close();
            writerFromClient.close();
            socket.close();
        } catch (IOException e) {
            // ignore
//            e.printStackTrace();
        } finally {
            run.set(false);
        }
    }
}
