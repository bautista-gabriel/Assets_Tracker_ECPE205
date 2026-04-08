package panels;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Providers extends JPanel {

    public interface OnAccountClick {
        void onClick(int accountId, String accountName);
    }

    private JPanel listPanel;
    private OnAccountClick listener;
    private Runnable onDataChanged;

    public Providers(OnAccountClick listener, Runnable onDataChanged) {
        this.listener = listener;
        this.onDataChanged = onDataChanged;

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

        String sql = "SELECT p.id, p.name, p.type, p.logo_path, " +
                "(COALESCE(ma.amount, 0) + COALESCE(sa.total_amount, 0)) AS amount " +
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

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String logoPath = rs.getString("logo_path");

                JButton btn = new JButton(name + " (" + type + ") - ₱" + String.format("%,.2f", amount));
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                btn.setHorizontalTextPosition(SwingConstants.RIGHT);
                btn.setIconTextGap(10);
                if (logoPath != null && !logoPath.isEmpty()) {
                    File logoFile = new File(logoPath);
                    if (logoFile.isFile()) {
                        ImageIcon icon = new ImageIcon(logoPath);
                        Image scaled = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                        btn.setIcon(new ImageIcon(scaled));
                        btn.setToolTipText(logoPath);
                    }
                }
                btn.addActionListener(e -> listener.onClick(id, name));

                JButton removeBtn = new JButton("Remove");
                removeBtn.addActionListener(e -> removeProvider(id, name));

                JPanel row = new JPanel(new BorderLayout(10, 0));
                row.add(btn, BorderLayout.CENTER);
                row.add(removeBtn, BorderLayout.EAST);

                listPanel.add(row);
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
        JTextField logoField = new JTextField();
        logoField.setEditable(false);
        JButton logoButton = new JButton("Choose PNG");
        logoButton.addActionListener(e -> chooseLogo(logoField));

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Logo (PNG):"));
        panel.add(logoField);
        panel.add(logoButton);

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

            String sql = "INSERT INTO providers (name, type, logo_path) VALUES (?, ?, ?)";
            String mainSql = "INSERT INTO main_accounts (provider_id, amount, updated_at) " +
                    "VALUES (?, ?, datetime('now'))";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement mainPs = conn.prepareStatement(mainSql)) {

                conn.setAutoCommit(false);
                ps.setString(1, name);
                ps.setString(2, type.equals("E-Wallet") ? "EWALLET" : "BANK");
                ps.setString(3, logoField.getText().trim().isEmpty() ? null : logoField.getText().trim());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int providerId = keys.getInt(1);
                        mainPs.setInt(1, providerId);
                        mainPs.setDouble(2, amount);
                        mainPs.executeUpdate();
                    }
                }
                conn.commit();

                refresh();
                notifyDataChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void chooseLogo(JTextField logoField) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select PNG Logo");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".png")) {
                JOptionPane.showMessageDialog(this, "Please choose a PNG file.");
                return;
            }
            logoField.setText(path);
        }
    }

    private void removeProvider(int providerId, String providerName) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Remove " + providerName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        String sql = "DELETE FROM providers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, providerId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();
        notifyDataChanged();
    }

    private void notifyDataChanged() {
        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }
}
