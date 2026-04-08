package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InitializeDatabase {

    public static void initialize() {
        String createAccountsTable = """
                CREATE TABLE IF NOT EXISTS accounts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL,
                    amount REAL NOT NULL DEFAULT 0
                )
                """;

        String createSubAccountsTable = """
                CREATE TABLE IF NOT EXISTS sub_accounts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    account_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    amount REAL NOT NULL DEFAULT 0,
                    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
                )
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps1 = conn.prepareStatement(createAccountsTable);
             PreparedStatement ps2 = conn.prepareStatement(createSubAccountsTable)) {

            ps1.execute();
            ps2.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}