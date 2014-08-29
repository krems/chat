package com.db.chat.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class HistoryDumperTest {


    @Test
    public void testAdd() throws Exception {
        String msg = "Test";
        int size = HistoryDumper.getSize();
        HistoryDumper.add(msg);
        assertThat(HistoryDumper.stringIsPresent(msg)).isTrue();
        assertThat(HistoryDumper.getSize()==size+1);
    }


    @Test
    public void testFlush() throws Exception {


    }

    @Test
    public void testGetRecentMessages() throws Exception {

    }
}