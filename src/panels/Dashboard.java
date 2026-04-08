package panels;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Dashboard extends JPanel {

    private JLabel totalLabel;

    public Dashboard() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Total Assets Overview", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        totalLabel = new JLabel("Total Assets: ₱0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 24));

        add(title, BorderLayout.NORTH);
        add(totalLabel, BorderLayout.CENTER);

        refreshTotal();
    }

    public void refreshTotal() {
        String sql = "SELECT IFNULL(SUM(amount), 0) AS total FROM accounts";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                double total = rs.getDouble("total");
                totalLabel.setText(String.format("Total Assets: ₱%,.2f", total));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}