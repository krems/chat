package com.db.chat.server.history;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by Student on 28.08.2014.
 */
public class HistoryController implements Runnable {
    private static final int MAX_CAPACITY = 20;
    private static final HistoryDao HISTORY_DAO = new HistoryDao();
    private static final BlockingQueue<String> messages = new ArrayBlockingQueue<>(MAX_CAPACITY + 10);

    public static void add(String message) {
        if (messages.size() >= MAX_CAPACITY + 5) {
            messages.notifyAll();
            HISTORY_DAO.saveMessage(message);
            return;
        }
        synchronized (messages) {
            try {
                messages.put(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (messages.size() >= MAX_CAPACITY) {
                messages.notifyAll();
            }
        }
    }

    @Override
    public void run() {
        while(true) {
            synchronized (messages) {
                if (messages.size() < MAX_CAPACITY) {
                    try {
                        messages.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            HISTORY_DAO.saveMessages(messages);
        }
    }

    public static void flush() {
        synchronized (messages) {
            messages.notifyAll();
        }
    }

    public List<String> getHistory() {
        List<String> hist;
        synchronized (messages) {
            hist = HISTORY_DAO.getAllMessages();
            hist.addAll(messages.parallelStream().collect(Collectors.toList()));
        }
        return hist;
    }
}

