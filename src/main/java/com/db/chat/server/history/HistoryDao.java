package com.db.chat.server.history;

import java.sql.*;
import java.util.*;

/**
 * Created by Student on 28.08.2014.
 */
class HistoryDao implements AutoCloseable {
    private static final String HOST = "127.0.0.1";
    private static final String PORT = "1527";

    public void saveMessages(Queue<String> messages) {
        ConnectionHolder holder = null;
        try {
            holder = ConnectionHolderPool.getConnectionHolder();
            while (!messages.isEmpty()) {
                holder.insertStatement.setString(1, messages.poll());
                holder.insertStatement.addBatch();
            }
            holder.insertStatement.executeBatch();
        } catch (SQLException e) {
            System.err.println("Messages were not saved to db");
//            e.printStackTrace();
        } finally {
            if (holder != null) {
                ConnectionHolderPool.returnConnectionHolder(holder);
            }
        }
    }

    public void saveMessage(String message) {
        ConnectionHolder holder = null;
        try {
            holder = ConnectionHolderPool.getConnectionHolder();
            holder.insertStatement.setString(1, message);
            holder.insertStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Messages were not saved to db");
//            e.printStackTrace();
        } finally {
            if (holder != null) {
                ConnectionHolderPool.returnConnectionHolder(holder);
            }
        }
    }


    public List<String> getAllMessages() {
        ConnectionHolder holder = null;
        try {
            holder = ConnectionHolderPool.getConnectionHolder();
            List<String> historyMessages = new ArrayList<>();
            ResultSet resultSet = holder.selectStatement.executeQuery();
            while (resultSet.next()) {
                historyMessages.add(resultSet.getString("MESSAGE"));
            }
            return historyMessages;
        } catch (SQLException e) {
            System.err.println("Couldn't load messages");
//            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (holder != null) {
                ConnectionHolderPool.returnConnectionHolder(holder);
            }
        }
    }

    @Override
    public void close() throws Exception {
        ConnectionHolderPool.close();
    }

    private static class ConnectionHolder {
        static {
            try {
                Class.forName("org.apache.derby.jdbc.ClientDriver");
            } catch (ClassNotFoundException e) {
                System.err.println("DB driver not found ");
            }
        }
        final Connection connection;
        final PreparedStatement insertStatement;
        final PreparedStatement selectStatement;

        private ConnectionHolder() {
            try {
                connection = createConnection();
                insertStatement = connection.prepareStatement("insert into HISTORY(MESSAGE) value (?)");
                selectStatement = connection.prepareStatement("select MESSAGE from HISTORY");
            } catch (SQLException e) {
                throw new IllegalStateException("Seems like no more connections available!", e);
            } catch (Exception e) {
                System.err.println("Something wrong with db");
                throw e;
//                e.printStackTrace();
            }
        }

        private Connection createConnection() throws SQLException {
            try {
                return DriverManager.getConnection("jdbc:derby://" + HOST + ":" + PORT + "/team-01;create=true");
            } catch (SQLException e) {
                System.err.println("Couldn't create connection");
//                e.printStackTrace();
                throw e;
            }
        }

        public void close() throws Exception {
            if (insertStatement != null) {
                insertStatement.close();
            }
            if (selectStatement != null) {
                selectStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private static class ConnectionHolderPool {
        private static final Collection<ConnectionHolder> pool = Collections.synchronizedCollection(new LinkedList<>());

        public static ConnectionHolder getConnectionHolder() {
            if (pool.isEmpty()) {
                return new ConnectionHolder();
            }
            synchronized (pool) {
                Iterator<ConnectionHolder> it = pool.iterator();
                if (it.hasNext()) {
                    ConnectionHolder connectionHolder = it.next();
                    it.remove();
                    return connectionHolder;
                } else {
                    return new ConnectionHolder();
                }
            }
        }

        public static void returnConnectionHolder(ConnectionHolder holder) {
            pool.add(holder);
        }

        public static void close() throws Exception {
            synchronized (pool) {
                for (ConnectionHolder holder : pool) {
                    holder.close();
                }
            }
        }
    }

}
