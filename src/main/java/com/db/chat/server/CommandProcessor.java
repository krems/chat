package com.db.chat.server;

import com.db.chat.server.command.*;

import java.net.SocketException;

/**
 * Created by Student on 28.08.2014.
 */
class CommandProcessor {
    private final Session initiatingSession;
    private Command command;

    public CommandProcessor(Session initiatingSession) {
        this.initiatingSession = initiatingSession;
    }

    public void process(final String cmd) {
        if (Commands.SEND.applicable(cmd)) {
            String msg = Commands.SEND.parseMessage(cmd);
            setToBroadcasting(initiatingSession, msg);
        } else if (Commands.REQUEST_HISTORY.applicable(cmd)) {
            setToHistory(initiatingSession);
        } else {
            replyError();
            setToEmpty();
        }
        command.doWork();
    }

    public void setToBroadcasting(Session initiatingSession, String msg) {
        this.command = new BroadcastingCommand(initiatingSession, msg);
    }

    public void setToHistory(Session initiatingSession) {
        this.command = new HistoryCommand(initiatingSession);
    }

    public void setToEmpty() {
        this.command = new EmptyCommand();
    }

    public void replyError() {
        try {
            initiatingSession.send("Wrong command!");
        } catch (SocketException e) {
            System.err.println("Connection closed after wrong command sent");
        }
    }
}