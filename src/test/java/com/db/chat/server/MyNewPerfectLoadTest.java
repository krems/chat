package com.db.chat.server;

import com.db.chat.Writer;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Student on 29.08.2014.
 */
public class MyNewPerfectLoadTest {

    private final Random rnd = new Random();

    @Ignore
    @Test
    public void shouldServerReceiveAndSendMessageWhenClientSendManyMessages() throws IOException {
        try {
            Socket readSocket = new Socket("127.0.0.1", 13000);
            Socket writeSocket = new Socket("127.0.0.1", 13000);
            Thread t = null;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));
                 BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(writeSocket.getOutputStream()));
                 BufferedReader bRW = new BufferedReader(new InputStreamReader(writeSocket.getInputStream()));
                 final BufferedWriter bWR = new BufferedWriter(new OutputStreamWriter(readSocket.getOutputStream()))) {
                 t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while(true) {
                                bRW.readLine();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.setDaemon(true);
                t.start();
                Writer writer = new Writer(bufferedWriter);
                String expected = rnd.nextLong() + Thread.currentThread().getName();
                writer.send("/snd " + expected);
                for (int i = 0; i < 100_000; ++i) {
                    String str = bufferedReader.readLine();
                    str = str.substring(str.lastIndexOf(']') + 2, str.length());
                    if (expected.equals(str)) {
                        break;
                    }
                    bufferedReader.readLine();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                throw e;
            } finally {
                try {
                    readSocket.close();
                } finally {
                    writeSocket.close();
                    if (t != null) {
                        t.stop(); // yep, stop
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }
}
