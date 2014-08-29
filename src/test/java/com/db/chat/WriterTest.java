package com.db.chat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WriterTest {

    Socket socket = new Socket();
    @Mock
    BufferedWriter mockwriter;
    Writer sut = new Writer(mockwriter);

    @Test
    public void testSend() throws Exception {
//        sut.send("test");
//        verify(mockwriter).write("test");
//        verify(mockwriter).flush();
    }

    @Test
    public void testEnd() throws Exception {

    }
}