package com.db.chat.server.command;

import com.db.chat.server.HistoryDumper;
import com.db.chat.server.Server;
import com.db.chat.server.Session;
import com.db.chat.server.command.Command;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Student on 28.08.2014.
 */
public class HistoryCommand implements Command {
    private Session session ;

    public HistoryCommand(Session session) {
        this.session = session;
    }

    @Override
    public void doWork() {
        try {
            session.send("HISTORY!!!");
            List<String> hist = new ArrayList<>();

            hist.addAll(Server.getHistoryDao().getAllMessages());
            hist.addAll(HistoryDumper.getRecentMessages());
            StringBuilder msgs = new StringBuilder();
            for (String msg : hist) {
                msgs.append(msg);
                msgs.append("\n");
            }
            session.send(msgs.toString());
            HistoryDumper.flush();
        } catch (SocketException e) {
            System.err.println("Socket closed. History doesn't sent");
            return;
        }
    }
}
