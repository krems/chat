package com.db.chat.server.command;

import com.db.chat.server.history.HistoryController;
import com.db.chat.server.Server;
import com.db.chat.server.Session;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Student on 27.08.2014.
 */
class BroadcastingCommand implements Command {
    private final Session callerSession;
    private final Collection<Session> sessions;
    private final String message;

    public BroadcastingCommand(Session session, String message) {
        this.callerSession = session;
        this.message = message;
        this.sessions = Server.getSessionRegistry().getSessions();
    }

    @Override
    public void doWork() {
        String timeStamp = generateTimeStamp();
        System.out.println("Adding to dumper session: " + callerSession.getId());
        HistoryController.add(timeStamp + message);
        System.out.println("Added to dumper session: " + callerSession.getId());
        // yes, by reference
        sessions.parallelStream().filter(session -> this.callerSession != session).forEach(session -> {
            try {
                System.out.println("Sending from session: " + callerSession.getId() + " to session: " + session.getId());
                session.send(timeStamp + message);
                System.out.println("Sent from session: " + callerSession.getId() + " to session: " + session.getId());
            } catch (IOException e) {
                System.err.println("Socket closed by client");
            }
        });
    }

    private String generateTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] "); // [2014/08/06 15:59:48]
        return dateFormat.format(new Date());
    }
}
