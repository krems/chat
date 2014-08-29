package com.db.chat;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by Student on 27.08.2014.
 */

public class ClientTest {

    @Test
    public void shouldClientSendMessageWhenEntered() throws IOException {
//        final int PORT=1234;
//        final String EXPECTED_GREETING="hello, I am Client";
//        final String EXPECTED_BUY="Goodbye, client";
//        ClientCycle clientCycle = new ClientCycle();
//        ServerSocket serverSocket = new ServerSocket(PORT);
//
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    Socket socket = serverSocket.accept();
//                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
//                            String message = reader.readLine();
//                            org.junit.Assert.assertEquals(EXPECTED_GREETING,message);
//                            writer.write(EXPECTED_BUY);
//                            writer.newLine();
//                            writer.flush();
//                    }
//                } catch (IOException e) {
//                }
//            }
//        }
//
//        );
//        t.start();
//
//      //  InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream( "Str".getBytes( ));
//        Client client = new Client("127.0.0.1", PORT);
//        client.run();
    }

}
