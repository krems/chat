package com.db.chat.server.command;

import com.db.chat.server.Server;
import com.db.chat.server.Session;
import com.db.chat.server.history.HistoryController;

import java.net.SocketException;
import java.util.List;

/**
 * Created by Student on 28.08.2014.
 */
class HistoryCommand implements Command {
    private Session session;

    public HistoryCommand(Session session) {
        this.session = session;
    }

    @Override
    public void doWork() {
        try {
            session.send("HISTORY!!!");
            List<String> hist = Server.getHistoryController().getHistory();
            StringBuilder msgs = new StringBuilder();
            for (String msg : hist) {
                msgs.append(msg);
                msgs.append("\n");
            }
            session.send(msgs.toString());
            HistoryController.flush();
        } catch (SocketException e) {
            System.err.println("Socket closed. History doesn't sent");
        }
    }
}
