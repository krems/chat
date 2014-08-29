package com.db.chat.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Student on 28.08.2014.
 */
public class SessionRegistry {
    private static final ConcurrentMap<Integer, Session> sessions = new ConcurrentHashMap<>(10);

    public void registerSession(Session session) {
        sessions.put(session.getId(), session);
    }

    public void unregisterSession(Session session) {
        this.unregisterSession(session.getId());
    }

    public void unregisterSession(int id) {
        sessions.remove(id);
    }

    public Session getSession(int id) {
        return sessions.get(id);
    }

    public Collection<Session> getSessions() {
        return Collections.unmodifiableCollection(sessions.values());
    }
    public void clear() {
        sessions.clear();
    }
}

