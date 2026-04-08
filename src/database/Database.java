//package database;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public final class Database {
//    private static final String[] BANKS = {
//            "BDO Unibank",
//            "Bank of the Philippine Islands (BPI)",
//            "Land Bank of the Philippines",
//            "Metropolitan Bank & Trust Company (Metrobank)",
//            "Philippine National Bank (PNB)",
//            "Security Bank",
//            "China Banking Corporation (China Bank)",
//            "Rizal Commercial Banking Corporation (RCBC)",
//            "Union Bank of the Philippines (UnionBank)",
//            "Development Bank of the Philippines (DBP)",
//            "EastWest Bank",
//            "Asia United Bank (AUB)",
//            "Philippine Bank of Communications (PBCom)",
//            "Philippine Veterans Bank",
//            "Philtrust Bank",
//            "Bank of Commerce",
//            "Tonik",
//            "UNO Digital Bank",
//            "UnionDigital Bank",
//            "GoTyme"
//    };
//
//    private static final String[] EWALLETS = {
//            "GCash",
//            "Maya",
//            "GrabPay",
//            "ShopeePay"
//    };
//
//    private static final String DB_URL = "jdbc:sqlite:" + databasePath();
//
//    private Database() {
//    }
//
//    private static String databasePath() {
//        Path path = Paths.get(System.getProperty("user.dir"), "assets_tracker.db");
//        return path.toAbsolutePath().toString();
//    }
//
//    public static Connection getConnection() throws SQLException {
//        loadDriver();
//        return DriverManager.getConnection(DB_URL);
//    }
//
//    public static void init() {
//        loadDriver();
//        try (Connection connection = getConnection()) {
//            connection.setAutoCommit(false);
//            createTables(connection);
//            seedProviders(connection);
//            seedMainAccounts(connection);
//            resetAll(connection);
//            connection.commit();
//        } catch (SQLException e) {
//            throw new IllegalStateException("Failed to initialize database", e);
//        }
//        Runtime.getRuntime().addShutdownHook(new Thread(Database::resetAll));
//    }
//
//    private static void loadDriver() {
//        try {
//            Class.forName("org.sqlite.JDBC");
//        } catch (ClassNotFoundException e) {
//            throw new IllegalStateException("SQLite JDBC driver not found on classpath", e);
//        }
//    }
//
//    private static void createTables(Connection connection) throws SQLException {
//        try (Statement statement = connection.createStatement()) {
//            statement.executeUpdate(
//                    "CREATE TABLE IF NOT EXISTS providers (" +
//                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                            "name TEXT NOT NULL UNIQUE," +
//                            "type TEXT NOT NULL CHECK (type IN ('BANK', 'EWALLET'))" +
//                            ")"
//            );
//            statement.executeUpdate(
//                    "CREATE TABLE IF NOT EXISTS main_accounts (" +
//                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                            "provider_id INTEGER NOT NULL UNIQUE," +
//                            "amount REAL NOT NULL DEFAULT 0," +
//                            "updated_at TEXT NOT NULL DEFAULT (datetime('now'))," +
//                            "FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE" +
//                            ")"
//            );
//            statement.executeUpdate(
//                    "CREATE TABLE IF NOT EXISTS sub_accounts (" +
//                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                            "provider_id INTEGER NOT NULL," +
//                            "name TEXT NOT NULL," +
//                            "amount REAL NOT NULL DEFAULT 0," +
//                            "created_at TEXT NOT NULL DEFAULT (datetime('now'))," +
//                            "updated_at TEXT NOT NULL DEFAULT (datetime('now'))," +
//                            "FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE," +
//                            "UNIQUE(provider_id, name)" +
//                            ")"
//            );
//        }
//    }
//
//    private static void seedProviders(Connection connection) throws SQLException {
//        String sql = "INSERT OR IGNORE INTO providers (name, type) VALUES (?, ?)";
//        try (PreparedStatement statement = connection.prepareStatement(sql)) {
//            for (String bank : BANKS) {
//                statement.setString(1, bank);
//                statement.setString(2, "BANK");
//                statement.addBatch();
//            }
//            for (String ewallet : EWALLETS) {
//                statement.setString(1, ewallet);
//                statement.setString(2, "EWALLET");
//                statement.addBatch();
//            }
//            statement.executeBatch();
//        }
//    }
//
//    private static void seedMainAccounts(Connection connection) throws SQLException {
//        try (Statement statement = connection.createStatement()) {
//            statement.executeUpdate(
//                    "INSERT OR IGNORE INTO main_accounts (provider_id, amount) " +
//                            "SELECT id, 0 FROM providers"
//            );
//        }
//    }
//
//    private static void resetAll(Connection connection) throws SQLException {
//        try (Statement statement = connection.createStatement()) {
//            statement.executeUpdate("UPDATE main_accounts SET amount = 0, updated_at = datetime('now')");
//            statement.executeUpdate("DELETE FROM sub_accounts");
//        }
//    }
//
//    private static void resetAll() {
//        try (Connection connection = getConnection()) {
//            connection.setAutoCommit(false);
//            resetAll(connection);
//            connection.commit();
//        } catch (SQLException e) {
//            throw new IllegalStateException("Failed to reset database", e);
//        }
//    }
//}
