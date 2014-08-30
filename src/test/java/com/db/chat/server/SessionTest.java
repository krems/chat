//package com.db.chat.server;
//
//import com.db.chat.Writer;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.nio.charset.StandardCharsets;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class SessionTest {
//    @Mock
//    private Socket sudSocket;
//    @Mock
//    private BufferedWriter mockWriter;
//    @Mock
//    private BufferedReader mockReader;
//    private Session sut;
//    @Before
//    public void setUp() throws Exception {
//        sut = new Session(0, sudSocket);
//
//    }
//
//    @Ignore
//    @Test
//    public void testRun() throws Exception {
//        when(mockReader.readLine()).thenThrow(new RuntimeException());
//        try {
//            sut.run();
////            fail();
//        } catch (RuntimeException e) {
//            return;
//        }
//
//    }
//
//
//    @Test
//    public void testReader() throws IOException {
//
//        when(sudSocket.getInputStream()).thenReturn(
//                new ByteArrayInputStream("Test string".getBytes(StandardCharsets.UTF_8)));
//        BufferedReader reader = sut.createReader(sudSocket);
//        assertEquals("Test string",reader.readLine());
//    }
//
//    @Ignore
//    @Test
//    public void testSend() throws Exception {
//        sut = new TestableSession(0, sudSocket) {
//            @Override
//            public void run() {
//                try (BufferedReader reader = this.createReader(this.socket);
//                     BufferedWriter writer = createWriter(this.socket)) {
//                    this.toClientWriter = new Writer(writer);
////                    this.receiver = new Receiver(reader, this);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        when(sudSocket.getInputStream()).thenThrow(new RuntimeException());
//        sut.run();
//        sut.send("Test");
//        verify(mockWriter).write("Test");
//    }
//
//    public class TestableSession extends Session {
//
//        public TestableSession(int id, Socket socket) {
//            super(id, socket);
//        }
//
//        public TestableSession(Socket socket) {
//            super(0, socket);
//        }
//
//        @Override
//        public BufferedReader createReader(Socket socket) {
//            return mockReader;
//        }
//
//        @Override
//        public BufferedWriter createWriter(Socket socket) {
//            return mockWriter;
//        }
//    }
//}