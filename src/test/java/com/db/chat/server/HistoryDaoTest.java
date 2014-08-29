package com.db.chat.server;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

public class HistoryDaoTest {

    @Ignore
    @Test
    public void testSaveMessage() throws Exception {
        try (HistoryDao historyDao = new HistoryDao()) {
            Queue<String> queue = new LinkedList<String>();
            queue.add("test1");
            historyDao.saveMessages(queue);
            Assert.assertEquals("test1", historyDao.getAllMessages().get(0));
        }
    }

    @Ignore
    @Test
    public void testGetAllMessages() throws Exception {
    }

}