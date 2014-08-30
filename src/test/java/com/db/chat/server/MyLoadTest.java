package com.db.chat.server;

import com.db.chat.Client;
import org.junit.Test;

import java.io.*;
import java.util.Random;

/**
 * Created by Student on 29.08.2014.
 */
public class MyLoadTest {

    private final Random rnd = new Random();

    @Test(timeout = 5_000)
    public void shouldServerReceiveAndSendMessageWhenClientSendManyMessages() throws IOException, InterruptedException {
        final String testMsg = rnd.nextLong() + Thread.currentThread().getName();
        final ByteArrayOutputStream income = new ByteArrayOutputStream();

        final Client clientSender = new Client("127.0.0.1", 13000,
                new InputStreamReader(new ByteArrayInputStream(("/snd " + testMsg).getBytes())),
                new OutputStreamWriter(System.out));
        final Client clientReceiver = new Client("127.0.0.1", 13000,
                new InputStreamReader(System.in),
                new OutputStreamWriter(income));
        Thread receiver = new Thread(clientReceiver::start);
        Thread sender = new Thread(clientSender::start);
        receiver.start();
        Thread.sleep(10);
        sender.start();

        for (int i = 0; i < 1_000_000; ++i) {
            String act = new String(income.toByteArray());
            if (act.contains(testMsg)) {
                break;
            }
        }
    }
}
