package panels;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AmountManagement extends JPanel {

    private JLabel accountNameLabel;
    private JLabel accountTypeLabel;
    private JTextField mainAmountField;
    private JLabel subTotalLabel;

    private DefaultListModel<String> subListModel;
    private JList<String> subList;

    private int currentAccountId = -1;

    private Runnable onBack;
    private Runnable onDataChanged;

    public AmountManagement(Runnable onBack, Runnable onDataChanged) {
        this.onBack = onBack;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> onBack.run());

        JPanel labelsPanel = new JPanel(new GridLayout(2, 1));
        accountNameLabel = new JLabel("Account");
        accountNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        accountTypeLabel = new JLabel("Type");

        labelsPanel.add(accountNameLabel);
        labelsPanel.add(accountTypeLabel);

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(labelsPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        centerPanel.add(new JLabel("Main Amount"));

        mainAmountField = new JTextField();
        mainAmountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        centerPanel.add(mainAmountField);
        centerPanel.add(Box.createVerticalStrut(10));

        JButton saveAmountButton = new JButton("Save Amount");
        saveAmountButton.addActionListener(e -> saveMainAmount());
        centerPanel.add(saveAmountButton);
        centerPanel.add(Box.createVerticalStrut(20));

        centerPanel.add(new JLabel("Sub Accounts"));

        subListModel = new DefaultListModel<>();
        subList = new JList<>(subListModel);

        JScrollPane scrollPane = new JScrollPane(subList);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        centerPanel.add(scrollPane);
        centerPanel.add(Box.createVerticalStrut(10));

        subTotalLabel = new JLabel("Sub Total: ₱0.00");
        subTotalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        centerPanel.add(subTotalLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        JButton addSubButton = new JButton("Add Sub Account");
        addSubButton.addActionListener(e -> addSubAccount());
        centerPanel.add(addSubButton);
        centerPanel.add(Box.createVerticalStrut(5));

        JButton removeSubButton = new JButton("Remove Selected");
        removeSubButton.addActionListener(e -> removeSelectedSubAccount());
        centerPanel.add(removeSubButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void loadAccount(int accountId) {
        currentAccountId = accountId;

        String sql = "SELECT p.name, p.type, COALESCE(ma.amount, 0) AS amount " +
                "FROM providers p " +
                "LEFT JOIN main_accounts ma ON ma.provider_id = p.id " +
                "WHERE p.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentAccountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    accountNameLabel.setText(rs.getString("name"));
                    accountTypeLabel.setText(rs.getString("type"));
                    mainAmountField.setText(String.valueOf(rs.getDouble("amount")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        refreshSubAccounts();
    }

    private void saveMainAmount() {
        if (currentAccountId == -1) return;

        double amount;

        try {
            amount = Double.parseDouble(mainAmountField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }

        if (amount < 0) {
            JOptionPane.showMessageDialog(this, "Amount cannot be negative.");
            return;
        }

        String sql = "INSERT INTO main_accounts (provider_id, amount, updated_at) " +
                "VALUES (?, ?, datetime('now')) " +
                "ON CONFLICT(provider_id) DO UPDATE SET " +
                "amount = excluded.amount, updated_at = excluded.updated_at";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentAccountId);
            ps.setDouble(2, amount);
            ps.executeUpdate();

            onDataChanged.run();
            JOptionPane.showMessageDialog(this, "Main amount updated.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSubAccount() {
        if (currentAccountId == -1) return;

        String name = JOptionPane.showInputDialog(this, "Enter sub-account name:");
        if (name == null || name.trim().isEmpty()) return;

        String amountText = JOptionPane.showInputDialog(this, "Enter amount:");
        if (amountText == null || amountText.trim().isEmpty()) return;

        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }

        if (amount < 0) {
            JOptionPane.showMessageDialog(this, "Amount cannot be negative.");
            return;
        }

        String sql = "INSERT INTO sub_accounts (provider_id, name, amount) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentAccountId);
            ps.setString(2, name);
            ps.setDouble(3, amount);
            ps.executeUpdate();

            refreshSubAccounts();
            onDataChanged.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeSelectedSubAccount() {
        if (currentAccountId == -1) return;

        String selected = subList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a sub-account first.");
            return;
        }

        int dashIndex = selected.indexOf(" | ID=");
        if (dashIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a sub-account first.");
            return;
        }

        int subId = Integer.parseInt(selected.substring(dashIndex + 6));

        String sql = "DELETE FROM sub_accounts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subId);
            ps.executeUpdate();

            refreshSubAccounts();
            onDataChanged.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshSubAccounts() {
        subListModel.clear();

        if (currentAccountId == -1) return;

        String sql = "SELECT id, name, amount FROM sub_accounts WHERE provider_id = ? ORDER BY id ASC";
        double total = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentAccountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int subId = rs.getInt("id");
                    String name = rs.getString("name");
                    double amount = rs.getDouble("amount");

                    total += amount;
                    subListModel.addElement(name + " - ₱" + String.format("%,.2f", amount) + " | ID=" + subId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        subTotalLabel.setText(String.format("Sub Total: ₱%,.2f", total));
    }
}
