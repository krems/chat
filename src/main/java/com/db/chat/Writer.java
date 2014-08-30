package com.db.chat;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by Student on 27.08.2014.
 */
public class Writer {
    private final BufferedWriter writer;

    public Writer(BufferedWriter writer) {
        this.writer = writer;
    }

    public void send(String msg) throws IOException {
        synchronized (writer) {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        }
    }

}

