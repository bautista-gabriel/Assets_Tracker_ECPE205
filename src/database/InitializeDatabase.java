package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class InitializeDatabase {
    private static final String[] BANKS = {
            "BDO Unibank",
            "Bank of the Philippine Islands (BPI)",
            "Land Bank of the Philippines",
            "Metropolitan Bank & Trust Company (Metrobank)",
            "Philippine National Bank (PNB)",
            "Security Bank",
            "China Banking Corporation (China Bank)",
            "Rizal Commercial Banking Corporation (RCBC)",
            "Union Bank of the Philippines (UnionBank)",
            "Development Bank of the Philippines (DBP)",
            "EastWest Bank",
            "Asia United Bank (AUB)",
            "Philippine Bank of Communications (PBCom)",
            "Philippine Veterans Bank",
            "Philtrust Bank",
            "Bank of Commerce",
            "Tonik",
            "UNO Digital Bank",
            "UnionDigital Bank",
            "GoTyme"
    };

    private static final String[] EWALLETS = {
            "GCash",
            "Maya",
            "GrabPay",
            "ShopeePay"
    };

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            createTables(conn);
            createTriggers(conn);
            seedAccounts(conn);
            resetAll(conn);
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(InitializeDatabase::resetAll));
    }

    private static void createTables(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS accounts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        type TEXT NOT NULL CHECK (type IN ('Bank', 'E-Wallet')),
                        amount REAL NOT NULL DEFAULT 0
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS assets (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        provider_name TEXT NOT NULL,
                        amount REAL NOT NULL,
                        created_at TEXT NOT NULL DEFAULT (datetime('now'))
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS sub_accounts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        account_id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        amount REAL NOT NULL,
                        created_at TEXT NOT NULL DEFAULT (datetime('now')),
                        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
                    )
                    """);
        }
    }

    private static void createTriggers(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TRIGGER IF NOT EXISTS assets_after_insert
                    AFTER INSERT ON assets
                    BEGIN
                        UPDATE accounts
                        SET amount = amount + NEW.amount
                        WHERE name = NEW.provider_name;

                        INSERT INTO accounts (name, type, amount)
                        SELECT NEW.provider_name,
                               CASE
                                   WHEN NEW.provider_name IN ('GCash', 'Maya', 'GrabPay', 'ShopeePay')
                                       THEN 'E-Wallet'
                                   ELSE 'Bank'
                               END,
                               NEW.amount
                        WHERE NOT EXISTS (
                            SELECT 1 FROM accounts WHERE name = NEW.provider_name
                        );
                    END;
                    """);
        }
    }

    private static void seedAccounts(Connection conn) throws Exception {
        String sql = "INSERT OR IGNORE INTO accounts (name, type, amount) VALUES (?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String bank : BANKS) {
                ps.setString(1, bank);
                ps.setString(2, "Bank");
                ps.addBatch();
            }
            for (String ewallet : EWALLETS) {
                ps.setString(1, ewallet);
                ps.setString(2, "E-Wallet");
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void resetAll(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("UPDATE accounts SET amount = 0");
            stmt.execute("DELETE FROM assets");
            stmt.execute("DELETE FROM sub_accounts");
        }
    }

    private static void resetAll() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            resetAll(conn);
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
