package com.db.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Created by Student on 27.08.2014.
 */
class Receiver {
    private final Session session;
    private final BufferedReader reader;
    private CommandProcessor commandProcessor;
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static volatile boolean stop = false;

    public Receiver(BufferedReader reader, Session session) {
        this.reader = reader;
        commandProcessor = new CommandProcessor(session);
        this.session = session;
    }

    public void startReceiving() throws IOException {
        while (!stop) {
            System.out.println("Receiving msg from session: " + session.getId());
            final String message = reader.readLine();
            if (message == null) {
                return;
            }
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    commandProcessor.process(message);
                }
            });
            System.out.println(message); // TODO log in prod
        }
    }

    public void end() throws IOException {
        if (!stop) {
            stop = true;
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
//            e.printStackTrace();
            return;
        }
        executor.shutdownNow();
    }

}

