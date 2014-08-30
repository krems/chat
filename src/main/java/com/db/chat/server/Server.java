package com.db.chat.server;

import com.db.chat.server.history.HistoryController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int DEFAULT_PORT = 13000;
    private static final SessionRegistry SESSION_REGISTRY = new SessionRegistry();
    private static final HistoryController HISTORY_CONTROLLER = new HistoryController();
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final AtomicBoolean stopped = new AtomicBoolean();
    private static final AtomicInteger sessionIdGen = new AtomicInteger();
    private static volatile boolean stop = false;
    private final int port;

    public static void main(String[] args) {
        if (args.length == 0) {
            new Server(DEFAULT_PORT).startServer();
        } else if (args.length == 1) {
            try {
                new Server(Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                System.err.println("Port must be a number");
                printUsage();
            }
        } else {
            System.err.println("Illegal number of arguments!");
            printUsage();
        }
    }

    public void startServer() {
        fireHistoryController();
        Thread finisher = new Thread(new ServerConsole());
        finisher.setDaemon(true);
        finisher.start();
        try (ServerSocket listener = new ServerSocket(port)) {
            while (!stopped.get()) {
                Socket socket = listener.accept();
                Session newSession = new Session(sessionIdGen.incrementAndGet(), socket);
                executor.execute(newSession);
                SESSION_REGISTRY.registerSession(newSession);
            }
        } catch (IOException e) {
            System.err.println("Exception thrown out!");
            e.printStackTrace();
        }
    }

    public void stop(long timeoutMillis) {
        if (stopped.compareAndSet(false, true)) {
            executor.shutdown();
            try {
                executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
                executor.shutdownNow();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
//            e.printStackTrace();
                return;
            }
            System.exit(0);
        }
    }

    public static SessionRegistry getSessionRegistry() {
        return SESSION_REGISTRY;
    }

    public static HistoryController getHistoryController() {
        return HISTORY_CONTROLLER;
    }

    private void fireHistoryController() {
        Thread historyDumper = new Thread(HISTORY_CONTROLLER);
        historyDumper.setDaemon(true);
        historyDumper.setName("HistoryDumper");
        historyDumper.start();
    }

    private Server(int port) {
        this.port = port;
    }

    private class ServerConsole implements Runnable {
        @Override
        public void run() {
            String cmd;
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (!stopped.get()) {
                try {
                    cmd = consoleReader.readLine();
                    if ("/q".equals(cmd) || cmd.startsWith("/stop") ||
                            cmd.startsWith("/quit") || cmd.startsWith("/exit")) {
                        SESSION_REGISTRY.getSessions().parallelStream().forEach(
                                (session) -> {
                                    try {
                                        session.stop();
                                    } catch (IOException e) {
                                        System.err.println("Error stopping server");
//                                    e.printStackTrace();
                                    }
                                });
                        HistoryController.flush();
                        stop(300);
                    }
                } catch (IOException e) {
                    System.err.println("Something wrong with console");
//                    e.printStackTrace();
                }
            }
        }
    }

    private static void printUsage() {
        System.err.println("Usage: Server <port>\nor\nServer\nDefault port is " + DEFAULT_PORT);
    }
}
