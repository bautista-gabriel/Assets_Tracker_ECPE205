package panels;

import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Dashboard extends JPanel {
    private JLabel totalLabel;
    private JTable assetsTable;
    private DefaultTableModel tableModel;

    public Dashboard() {
        setLayout(new BorderLayout());

        // Header Panel to hold Title and Total
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));



        totalLabel = new JLabel("Total Assets: ₱0.00");
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));


        headerPanel.add(totalLabel);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Provider", "Type", "Amount"};

        // FIX TO MAKE TABLE READ ONLY
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        assetsTable = new JTable(tableModel);
        assetsTable.getTableHeader().setReorderingAllowed(false);
        assetsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(assetsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        add(scrollPane, BorderLayout.CENTER);

        updateDashboard();
    }

    public void updateDashboard() {
        tableModel.setRowCount(0);
        String sql = "SELECT provider_name, SUM(amount) AS total_amount FROM assets GROUP BY provider_name ORDER BY provider_name";

        double totalAssets = 0.0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String provider = rs.getString("provider_name");
                double amount = rs.getDouble("total_amount");
                String type = (provider.equals("GCash") || provider.equals("Maya") ||
                        provider.equals("GrabPay") || provider.equals("ShopeePay")) ? "E-Wallet" : "Bank";

                // FIX to have
                tableModel.addRow(new Object[]{provider, type, String.format("₱%,.2f", amount)});
                totalAssets += amount;
            }
            totalLabel.setText("Total Assets: " + String.format("₱%,.2f", totalAssets));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}