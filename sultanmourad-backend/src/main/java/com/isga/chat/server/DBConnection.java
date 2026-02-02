package com.isga.chat.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:isga_chat.db";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(URL);
                initializeDatabase();
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite Driver not found", e);
            }
        }
        return connection;
    }

    private static void initializeDatabase() throws SQLException {
        String createUserTable = """
                    CREATE TABLE IF NOT EXISTS USER (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        USERNAME TEXT UNIQUE NOT NULL,
                        FIRST_NAME TEXT NOT NULL,
                        LAST_NAME TEXT NOT NULL,
                        EMAIL TEXT UNIQUE NOT NULL,
                        PASSWORD TEXT NOT NULL,
                        PERMISSION INTEGER NOT NULL DEFAULT 3,
                        LAST_CONNECTION_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        STATUS TEXT DEFAULT 'offline',
                        IS_BANNED INTEGER DEFAULT 0
                    )
                """;

        String createMessageTable = """
                    CREATE TABLE IF NOT EXISTS MESSAGE (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        USER_ID INTEGER,
                        TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONTENT TEXT NOT NULL,
                        FOREIGN KEY (USER_ID) REFERENCES USER(ID)
                    )
                """;

        String createLogTable = """
                    CREATE TABLE IF NOT EXISTS LOG (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        USER_ID INTEGER,
                        TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        TYPE TEXT NOT NULL,
                        FOREIGN KEY (USER_ID) REFERENCES USER(ID)
                    )
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUserTable);
            stmt.execute(createMessageTable);
            stmt.execute(createLogTable);

            // Check if admin exists, if not, insert seed data
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM USER WHERE USERNAME = 'admin'");
            if (rs.next() && rs.getInt(1) == 0) {
                // password123 ->
                // ef92b778bafe421e592022c3904a0a1e36601e444400e96030c6aee6ad005fbc
                String seedData = """
                            INSERT INTO USER (USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, PERMISSION) VALUES
                            ('admin', 'Admin', 'User', 'admin@isga.com', 'ef92b778bafe421e592022c3904a0a1e36601e444400e96030c6aee6ad005fbc', 1),
                            ('mod', 'Moderator', 'User', 'mod@isga.com', 'ef92b778bafe421e592022c3904a0a1e36601e444400e96030c6aee6ad005fbc', 2),
                            ('user1', 'Normal', 'User1', 'user1@isga.com', 'ef92b778bafe421e592022c3904a0a1e36601e444400e96030c6aee6ad005fbc', 3),
                            ('user2', 'Normal', 'User2', 'user2@isga.com', 'ef92b778bafe421e592022c3904a0a1e36601e444400e96030c6aee6ad005fbc', 3)
                        """;
                stmt.execute(seedData);
                System.out.println("Seed data inserted!");
            }
        }
        System.out.println("Database initialized successfully!");
    }
}
