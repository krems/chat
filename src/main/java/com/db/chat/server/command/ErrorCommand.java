package com.db.chat.server.command;

import com.db.chat.server.Session;

import java.net.SocketException;

/**
 * Created by Student on 29.08.2014.
 */
class ErrorCommand implements Command {
    private final Session initiatingSession;
    public ErrorCommand(Session session) {
        this.initiatingSession = session;
    }

    @Override
    public void doWork() {
        try {
            initiatingSession.send("Wrong command!");
        } catch (SocketException e) {
            System.err.println("Connection closed after wrong command sent");
        }
    }
}
