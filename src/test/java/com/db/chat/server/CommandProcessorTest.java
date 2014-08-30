package com.db.chat.server;

import com.db.chat.server.command.CommandProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.Socket;

import static org.mockito.Mockito.spy;


@RunWith(MockitoJUnitRunner.class)
public class CommandProcessorTest {
    private String testMsg;
    private CommandProcessor spyCommandProcessor;
    @Mock
    private Socket mockSocket;
    private Session stubSession = new Session(0, mockSocket) {
        @Override
        public void send(String msg) {
            // do nothing
        }
    };

    @Before
    public void setUp() throws Exception {
        spyCommandProcessor = spy(new CommandProcessor(stubSession));
    }

    @Test
    public void testBroadcastingCommand() throws Exception {
        testMsg = "/snd Msg";
        spyCommandProcessor.process(testMsg);
//        verify(spyCommandProcessor).setToBroadcasting(stubSession,testMsg.split(" ")[1]);
    }

    @Test
    public void testHistCommand() throws Exception {
        testMsg = "/hist";
        spyCommandProcessor.process(testMsg);
//        verify(spyCommandProcessor).setToHistory(stubSession);

    }

    @Test
    public void testEmptyCommand() throws Exception {
        testMsg = "/emptytest";
        spyCommandProcessor.process(testMsg);
//        verify(spyCommandProcessor).setToEmpty();
    }
}