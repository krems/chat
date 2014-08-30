package com.db.chat.server.command;

/**
 * Created by Student on 29.08.2014.
 */
enum Commands {
    SEND("/snd "),
    REQUEST_HISTORY("/hist");

    private final String name;
    Commands(String cmdName) {
        name = cmdName;
    }

    public String cmd() {
        return name;
    }

    public boolean applicable(String msg) {
        return msg.startsWith(name);
    }

    public String parseMessage(String msg) {
        if (!applicable(msg)) {
            throw new IllegalArgumentException("Must be applicable!");
        }
        return msg.substring(name.length(), msg.length());
    }
}
