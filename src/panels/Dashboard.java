package panels;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Dashboard extends JPanel {

    private JLabel totalLabel;
    private DefaultTableModel tableModel;

    public Dashboard() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Total Assets Overview", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        totalLabel = new JLabel("Total Assets: ₱0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(totalLabel);
        add(header, BorderLayout.NORTH);

        String[] columns = {"Provider", "Type", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshTotal();
            }
        });
        refreshTotal();
    }

    public void refreshTotal() {
        String sql = "SELECT p.name, p.type, " +
                "(COALESCE(ma.amount, 0) + COALESCE(sa.total_amount, 0)) AS total_amount " +
                "FROM providers p " +
                "LEFT JOIN main_accounts ma ON ma.provider_id = p.id " +
                "LEFT JOIN (" +
                "   SELECT provider_id, SUM(amount) AS total_amount " +
                "   FROM sub_accounts GROUP BY provider_id" +
                ") sa ON sa.provider_id = p.id " +
                "ORDER BY p.name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            tableModel.setRowCount(0);
            double total = 0.0;
            while (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                double amount = rs.getDouble("total_amount");
                total += amount;
                tableModel.addRow(new Object[]{name, type, String.format("₱%,.2f", amount)});
            }
            totalLabel.setText(String.format("Total Assets: ₱%,.2f", total));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
