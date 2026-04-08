package database;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static final String DB_URL = "jdbc:sqlite:" + databasePath();

    private Database() {
    }

    private static String databasePath() {
        Path path = Paths.get(System.getProperty("user.dir"), "assets_tracker.db");
        return path.toAbsolutePath().toString();
    }

    public static Connection getConnection() throws SQLException {
        loadDriver();
        Connection connection = DriverManager.getConnection(DB_URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void init() {
        loadDriver();
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            createTables(connection);
            connection.commit();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize database", e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("SQLite JDBC driver not found on classpath", e);
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS providers (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL UNIQUE," +
                            "type TEXT NOT NULL CHECK (type IN ('BANK', 'EWALLET'))," +
                            "logo_path TEXT" +
                            ")"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS main_accounts (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "provider_id INTEGER NOT NULL UNIQUE," +
                            "amount REAL NOT NULL DEFAULT 0," +
                            "updated_at TEXT NOT NULL DEFAULT (datetime('now'))," +
                            "FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE" +
                            ")"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS sub_accounts (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "provider_id INTEGER NOT NULL," +
                            "name TEXT NOT NULL," +
                            "amount REAL NOT NULL DEFAULT 0," +
                            "created_at TEXT NOT NULL DEFAULT (datetime('now'))," +
                            "updated_at TEXT NOT NULL DEFAULT (datetime('now'))," +
                            "FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE," +
                            "UNIQUE(provider_id, name)" +
                            ")"
            );
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE providers ADD COLUMN logo_path TEXT");
        } catch (SQLException ignored) {
        }
    }

}
