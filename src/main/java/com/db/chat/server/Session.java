package com.db.chat.server;

import com.db.chat.Writer;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Student on 27.08.2014.
 */
public class Session implements Runnable {
    private final int id;
    protected final Socket socket;
    protected Writer writer;
    protected Receiver receiver;

    public Session(int id, Socket socket) {
        this.id = id;
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            writer = createWriter(socket);
            reader = this.createReader(socket);
            this.writer = new Writer(writer);
            this.receiver = new Receiver(reader, this);
            this.receiver.startReceiving();
        } catch (IOException e) {
            System.err.println("Socket closed by client " + id);
        } catch (Throwable e) {
            System.err.println("Something wrong with session " + id);
        } finally {
            try {
                if (writer != null) {
                    synchronized (writer) {
                        writer.close();
                    }
                }
            } catch (IOException e) {
                //
            } finally {
                try {
                    if (reader != null) {
                        synchronized (reader) {
                            reader.close();
                        }
                    }
                } catch (IOException e) {
                    //
                } finally {
                    close();
                }
            }
        }
    }

    BufferedReader createReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    BufferedWriter createWriter(Socket socket) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void send(String msg) throws SocketException {
        try {
            writer.send(msg);
        } catch (SocketException e) {
            System.err.println("Socket closed. Message doesn't sent. " + id);
            close();
        } catch (IOException e) {
            System.err.println("Couldn't send message " + id);
            close();
        } catch (Throwable e) {
            System.err.println("Something wrong with session " + id);
            close();
        }
    }

    public void end() throws IOException {
        send("Server is shutting down");
        this.socket.shutdownInput();
        this.socket.shutdownOutput();
        this.socket.close();

        this.receiver.end();
    }

    public int getId() {
        return id;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket " + id);
        } finally {
            Server.getSessionRegistry().unregisterSession(id);
        }
    }
}
