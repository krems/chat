package com.db.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final SessionRegistry SESSION_REGISTRY = new SessionRegistry();
    private static final ExecutorService executor = Executors.newFixedThreadPool(1000);
    private static final AtomicBoolean stopped = new AtomicBoolean();
    private static final AtomicInteger sessionIdGen = new AtomicInteger();
    private static final HistoryDao historyDao = new HistoryDao();
    private static volatile boolean stop = false;
    private final int port;

    public static void main(String[] args) {
        new Server(13000).startServer();
    }

    public void startServer() {
        fireHistoryDumper();
        try (ServerSocket listener = new ServerSocket(port)) {
            Thread finisher = new Thread(new Finisher());
            finisher.start();
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
        stopped.set(true);
        executor.shutdown();
        try {
            executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
//            e.printStackTrace();
            return;
        }
        executor.shutdownNow();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static SessionRegistry getSessionRegistry() {
        return SESSION_REGISTRY;
    }

    private void fireHistoryDumper() {
        Thread historyDumper = new Thread(new HistoryDumper());
        historyDumper.setDaemon(true);
        historyDumper.setName("HistoryDumper");
        historyDumper.start();
    }


    public static HistoryDao getHistoryDao() {
        return historyDao;
    }
    public Server(int port) {
        this.port = port;
    }
    class Finisher implements Runnable {

        @Override
        public void run() {
            String line = new String();
            while(!stopped.get()) {

                BufferedReader finishLine = new BufferedReader(new InputStreamReader(System.in));
                try {
                    line = finishLine.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (line.startsWith("/stop")) {

                    Collection<Session> sessions = SESSION_REGISTRY.getSessions();
                    for (Session session : sessions) {
                        try {
                            session.end();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    HistoryDumper.flush();
                    stop(10);

                }
            }

        }
    }
}
