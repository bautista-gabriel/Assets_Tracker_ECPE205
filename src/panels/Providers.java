package panels;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Providers extends JPanel {

    public interface OnAccountClick {
        void onClick(int accountId, String accountName);
    }

    private JPanel listPanel;
    private OnAccountClick listener;

    public Providers(OnAccountClick listener) {
        this.listener = listener;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Banks / E-Wallets");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        listPanel.removeAll();

        String sql = "SELECT id, name, type, amount FROM accounts ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");

                JButton btn = new JButton(name + " (" + type + ") - ₱" + String.format("%,.2f", amount));
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                btn.addActionListener(e -> listener.onClick(id, name));

                listPanel.add(btn);
                listPanel.add(Box.createVerticalStrut(8));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        revalidate();
        repaint();
    }

    public void addAccount() {
        JTextField nameField = new JTextField();
        String[] types = {"Bank", "E-Wallet"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        JTextField amountField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Bank / E-Wallet",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String type = typeBox.getSelectedItem().toString();
            String amountText = amountField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required.");
                return;
            }

            double amount = 0;
            if (!amountText.isEmpty()) {
                try {
                    amount = Double.parseDouble(amountText);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Invalid amount.");
                    return;
                }
            }

            String sql = "INSERT INTO accounts (name, type, amount) VALUES (?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, type);
                ps.setDouble(3, amount);
                ps.executeUpdate();

                refresh();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
