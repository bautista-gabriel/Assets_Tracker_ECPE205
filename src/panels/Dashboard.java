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

        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        totalLabel = new JLabel("Welcome Juan Dela Cruz");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalLabel.setBounds(1,1,1,1);
        centerPanel.add(totalLabel,BorderLayout.NORTH);

        JLabel banks = new JLabel("Banks");
        banks.setFont(new Font("Arial", Font.BOLD, 14));
        banks.setBounds(1,10,1,1);
        centerPanel.add(banks,BorderLayout.NORTH);

        JLabel ewallets = new JLabel("E-Wallets");

        add(centerPanel,BorderLayout.CENTER);



//        String[] columns = {"Provider", "Type", "Amount"};
//        tableModel = new DefaultTableModel(columns, 0);
//        assetsTable = new JTable(tableModel);
//
//        JScrollPane scrollPane = new JScrollPane(assetsTable);
//        add(scrollPane, BorderLayout.CENTER);
//
//            updateDashboard();
//    }
//
//    public void updateDashboard() {
//        tableModel.setRowCount(0);
//        String sql = "SELECT provider_name, SUM(amount) AS total_amount from assets GROUP BY provider_name ORDER BY provider_name";
//
//        double totalAssets = 0.0;
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {
//            while (rs.next()) {
//                String provider = rs.getString("provider_name");
//                double amount = rs.getDouble("total_amount");
//                String type;
//
//                if (provider.equals("GCash") || provider.equals("Maya") || provider.equals("GrabPay") || provider.equals("ShopeePay")) {
//                    type = "E-Wallet";
//                } else {
//                    type = "Bank";
//                }
//                tableModel.addRow(new Object[]{provider, type, String.format("₱%.2f", amount)});
//                totalAssets += amount;
//
//                totalLabel.setText("Total Assets: " + String.format("₱%.2f", totalAssets));
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Error loading dashboard: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }

    }
}