//package com.db.chat.server;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import java.net.Socket;
//
//import static org.fest.assertions.Assertions.assertThat;
//import static org.junit.Assume.assumeTrue;
//
//@RunWith(MockitoJUnitRunner.class)
//public class SessionRegistryTest {
//    private SessionRegistry sut;
//    private Socket dummySocket;
//    private Session dummySession;
//
//    @Before
//    public void setUp() throws Exception {
//        sut = new SessionRegistry();
//        dummySocket = new Socket();
//        dummySession = new Session(0,dummySocket);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        Server.getSessionRegistry().clear();
//
//    }
//
//    @Test
//    public void testRegisterSession() throws Exception {
//        assumeTrue(sut.getSessions().isEmpty());
//        sut.registerSession(dummySession);
//
//
//        assertThat(sut.getSessions().size()).isEqualTo(1);
//        assertThat(sut.getSessions().contains(dummySession)).isTrue();
//    }
//
//    @Test
//    public void testUnregisterSession() throws Exception {
//        assumeTrue(sut.getSessions().isEmpty());
//        sut.registerSession(dummySession);
//        sut.unregisterSession(dummySession.getId());
//
//        assertThat(sut.getSessions().size()).isEqualTo(0);
//        assertThat(sut.getSessions().contains(dummySession)).isFalse();
//        assertThat(sut.getSession(0)).isNull();
//    }
//
//    @Test
//    public void testUnregisterSession1() throws Exception {
//        assumeTrue(sut.getSessions().isEmpty());
//        sut.registerSession(dummySession);
//        sut.unregisterSession(dummySession);
//
//        assertThat(sut.getSessions().size()).isEqualTo(0);
//        assertThat(sut.getSessions().contains(dummySession)).isFalse();
//        assertThat(sut.getSession(0)).isNull();
//    }
//
//    @Test
//    public void testGetSession() throws Exception {
//        assumeTrue(sut.getSessions().isEmpty());
//        sut.registerSession(dummySession);
//        assertThat(sut.getSession(0)).isEqualTo(dummySession);
//
//    }
//
//    @Test
//    public void testGetSessions() throws Exception {
//        assumeTrue(sut.getSessions().isEmpty());
//        sut.registerSession(dummySession);
//        assertThat(sut.getSessions().size()).isEqualTo(1);
//    }
//
//}