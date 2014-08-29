package com.db.chat;

import java.io.*;

/**
 * Created by Student on 27.08.2014.
 */
public class ClientCycle {

    public void communicate(final BufferedReader consoleIn, final BufferedWriter sockOut,
                            final BufferedWriter consoleOut, final BufferedReader sockIn) {
        new Thread(new MessageSender(consoleIn, sockOut)).start();
        Writer writer = new Writer(consoleOut);
        try {
            while (true) {
                String response = sockIn.readLine();
                writer.send(response);
            }
        } catch (IOException e) {
            System.out.println("Interaction error. Please, try to reconnect");
            System.exit(0);
        }
    }

    private static class MessageSender implements Runnable {

        private final BufferedReader consoleIn;
        private final BufferedWriter sockOut;

        MessageSender(BufferedReader consoleIn, BufferedWriter sockOut) {
            this.consoleIn = consoleIn;
            this.sockOut = sockOut;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = consoleIn.readLine();
                    if (message == null || "/q".equals(message) || "/quit".equals(message) || "/exit".equals(message)) {
                        System.out.println("Shutting down. Bye! :-)");
                        System.exit(0);
                    }
                    sockOut.write(message);
                    sockOut.newLine();
                    sockOut.flush();
                }
            } catch (IOException e) {
                System.out.println("Interaction error. Please, try to reconnect");
                System.exit(0);
            }
        }
    }
}
