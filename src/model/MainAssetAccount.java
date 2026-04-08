package model;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import database.DatabaseConnection;

public class MainAssetAccount extends JPanel {

    private JLabel totalLabel;

    public MainAssetAccount() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Main Asset Account", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        totalLabel = new JLabel("Total Assets: ₱0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshBtn.addActionListener(e -> loadTotalAssets());

        centerPanel.add(totalLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(refreshBtn);

        add(centerPanel, BorderLayout.CENTER);

        loadTotalAssets();
    }

    public void loadTotalAssets() {
        double totalAssets = 0.0;

        String sql = "SELECT SUM(amount) AS total FROM assets";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                totalAssets = rs.getDouble("total");
            }

            totalLabel.setText("Total Assets: ₱" + String.format("%,.2f", totalAssets));

        } catch (Exception e) {
            totalLabel.setText("Total Assets: Error");
            JOptionPane.showMessageDialog(this, "Error loading total assets: " + e.getMessage());
        }
    }
}