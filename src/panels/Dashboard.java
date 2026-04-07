package panels;
import database.Database;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Dashboard extends JPanel {
    private JLabel totalLabel;
    private JTable assetsTable;
    private DefaultTableModel tableModel;

    public Dashboard() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        totalLabel = new JLabel("Total Assets: ₱0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(totalLabel);
        add(header, BorderLayout.NORTH);

        String[] columns = {"Provider", "Type", "Amount"};
        tableModel = new DefaultTableModel(columns, 0);
        assetsTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(assetsTable);
        add(scrollPane, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateDashboard();
            }
        });
        updateDashboard();
    }

    public void updateDashboard() {
        tableModel.setRowCount(0);
        String sql = "SELECT p.name, p.type, " +
                "(COALESCE(ma.amount, 0) + COALESCE(sa.total_amount, 0)) AS total_amount " +
                "FROM providers p " +
                "LEFT JOIN main_accounts ma ON ma.provider_id = p.id " +
                "LEFT JOIN (" +
                "   SELECT provider_id, SUM(amount) AS total_amount " +
                "   FROM sub_accounts GROUP BY provider_id" +
                ") sa ON sa.provider_id = p.id " +
                "ORDER BY p.name";

        double totalAssets = 0.0;
        totalLabel.setText("Total Assets: ₱0.00");
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String provider = rs.getString("name");
                double amount = rs.getDouble("total_amount");
                String type = rs.getString("type");
                String typeLabel = "EWALLET".equals(type) ? "E-Wallet" : "Bank";
                tableModel.addRow(new Object[]{provider, typeLabel, String.format("₱%.2f", amount)});
                totalAssets += amount;
            }
            totalLabel.setText("Total Assets: " + String.format("₱%.2f", totalAssets));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading dashboard: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
