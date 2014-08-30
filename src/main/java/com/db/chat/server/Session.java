package com.db.chat.server;

import com.db.chat.Writer;
import com.db.chat.server.command.CommandProcessor;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Student on 27.08.2014.
 */
public class Session implements Runnable {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final AtomicBoolean stop = new AtomicBoolean();

    private final int id;
    protected final Socket socket;
    protected Writer toClientWriter;
    private CommandProcessor commandProcessor;

    public Session(int id, Socket socket) {
        this.id = id;
        this.socket = socket;
    }

    @Override
    public void run() {
        commandProcessor = new CommandProcessor(this);
        try (BufferedReader socketReader = createReader(socket);
             BufferedWriter socketWriter = createWriter(socket)) {
            toClientWriter = new Writer(socketWriter);
            receive(socketReader);
        } catch (IOException e) {
            System.err.println("Socket closed by client " + id);
        } catch (Throwable e) {
            System.err.println("Something wrong with session " + id);
        } finally {
            close();
        }
    }

    BufferedReader createReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    BufferedWriter createWriter(Socket socket) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private void receive(BufferedReader reader) throws IOException {
        while (!stop.get()) {
            System.out.println("Receiving msg from session: " + id);
            final String message = reader.readLine();
            if (message == null) {
                return;
            }
            executor.submit(() -> commandProcessor.process(message));
            System.out.println(message);
        }
    }

    public void send(String msg) throws SocketException {
        try {
            toClientWriter.send(msg);
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

    public int getId() {
        return id;
    }

    public void stop() throws IOException {
        send("Server is shutting down");
        close();
        if (!stop.compareAndSet(false, true)) {
            return;
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
//            e.printStackTrace();
            return;
        }
        executor.shutdownNow();
    }

    public void close() {
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket " + id);
        } finally {
            Server.getSessionRegistry().unregisterSession(id);
        }
    }
}
