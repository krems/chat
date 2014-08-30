package com.db.chat.server.command;

import com.db.chat.server.Server;
import com.db.chat.server.Session;
import com.db.chat.server.history.HistoryController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Student on 27.08.2014.
 */
class BroadcastingCommand implements Command {
    // [2014/08/06 15:59:48]
    private final static ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] ");
        }
    };
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
        String msgWithTime = timeStamp + message;
//        System.out.println("Adding to dumper session: " + callerSession.getId());
        HistoryController.add(msgWithTime);
//        System.out.println("Added to dumper session: " + callerSession.getId());
        // yes, by reference
        sessions.parallelStream().filter(session -> callerSession != session).forEach(session -> {
            try {
//                System.out.println("Sending from session: " + callerSession.getId() + " to session: " + session.getId());
                session.send(msgWithTime);
//                System.out.println("Sent from session: " + callerSession.getId() + " to session: " + session.getId());
            } catch (IOException e) {
                System.err.println("Socket closed by client");
            }
        });
    }

    private String generateTimeStamp() {
        return dateFormat.get().format(new Date());
    }
}
