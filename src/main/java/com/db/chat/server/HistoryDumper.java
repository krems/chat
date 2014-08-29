package com.db.chat.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Student on 28.08.2014.
 */
public class HistoryDumper implements Runnable {
    private static final int MAX_CAPACITY = 20;
    private static final BlockingQueue<String> q = new ArrayBlockingQueue<>(MAX_CAPACITY + 10);

    public static void add(String message) {
        if (q.size() >= MAX_CAPACITY + 10) {
            Server.getHistoryDao().saveMessage(message);
            return;
        }
        synchronized (q) {
            try {
                q.notifyAll();
                q.put(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (q.size() >= MAX_CAPACITY) {
                q.notifyAll();
            }
        }
    }

    @Override
    public void run() {
        while(true) {
            synchronized (q) {
                if (q.size() < MAX_CAPACITY) {
                    try {
                        q.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            Server.getHistoryDao().saveMessages(q);
        }
    }

    public static void flush() {
        synchronized (q) {
            q.notifyAll();
        }
    }

    public static Collection<String> getRecentMessages() {
        List<String> msgs = new ArrayList<>();
        synchronized (q) {
            for (String msg : q) {
                msgs.add(msg);
            }
        }
        return msgs;
    }
    public static int getSize() {
        return q.size();
    }
    public static boolean stringIsPresent(String string) {
        return q.contains(string);
    }
}

