package com.db.chat.server.command;

import com.db.chat.server.Session;

/**
 * Created by Student on 28.08.2014.
 */
public class CommandProcessor {
    private final Session initiatingSession;

    public CommandProcessor(Session initiatingSession) {
        this.initiatingSession = initiatingSession;
    }

    public void process(final String cmd) {
        Command command;
        if (Commands.SEND.applicable(cmd)) {
            String msg = Commands.SEND.parseMessage(cmd);
            command = createBroadcastingCommand(initiatingSession, msg);
        } else if (Commands.REQUEST_HISTORY.applicable(cmd)) {
            command = createHistoryCommand(initiatingSession);
        } else {
            command = createEmptyCommand(initiatingSession);
        }
        command.doWork();
    }

    private Command createBroadcastingCommand(Session initiatingSession, String msg) {
        return new BroadcastingCommand(initiatingSession, msg);
    }

    private Command createHistoryCommand(Session initiatingSession) {
        return new HistoryCommand(initiatingSession);
    }

    private Command createEmptyCommand(Session initiatingSession) {
        return new ErrorCommand(initiatingSession);
    }
}