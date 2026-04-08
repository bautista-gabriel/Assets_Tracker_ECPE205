package app;

import database.DatabaseConnection;
import database.InitializeDatabase;
import panels.Dashboard;
import panels.Providers;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private Providers providersPanel;
    private Dashboard dashboardPanel;
    private SubAcc subAccPanel;

    public MainFrame() {
        setTitle("Asset Tracker");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        InitializeDatabase.initialize();
        insertSampleDataIfEmpty();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        dashboardPanel = new Dashboard();

        providersPanel = new Providers((accountId, accountName) -> {
            subAccPanel.loadAccount(accountId);
            cardLayout.show(mainPanel, "SUBACC");
        });

        subAccPanel = new SubAcc(
                () -> {
                    refreshHome();
                    cardLayout.show(mainPanel, "HOME");
                },
                this::refreshHome
        );

        JPanel homePanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Providers / Banks", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JButton addButton = new JButton("+ Add Bank / E-Wallet");
        addButton.addActionListener(e -> {
            providersPanel.addAccount();
            refreshHome();
        });

        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(addButton, BorderLayout.EAST);

        homePanel.add(topPanel, BorderLayout.NORTH);
        homePanel.add(providersPanel, BorderLayout.CENTER);
        homePanel.add(dashboardPanel, BorderLayout.SOUTH);

        mainPanel.add(homePanel, "HOME");
        mainPanel.add(subAccPanel, "SUBACC");

        add(mainPanel);

        refreshHome();
    }

    private void refreshHome() {
        providersPanel.refresh();
        dashboardPanel.refreshTotal();
    }

    private void insertSampleDataIfEmpty() {
        String countSql = "SELECT COUNT(*) AS total FROM accounts";
        String insertSql = "INSERT INTO accounts (name, type, amount) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement countPs = conn.prepareStatement(countSql);
             ResultSet rs = countPs.executeQuery()) {

            if (rs.next() && rs.getInt("total") == 0) {
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setString(1, "BPI");
                    insertPs.setString(2, "Bank");
                    insertPs.setDouble(3, 1000);
                    insertPs.executeUpdate();

                    insertPs.setString(1, "GCash");
                    insertPs.setString(2, "E-Wallet");
                    insertPs.setDouble(3, 2500);
                    insertPs.executeUpdate();

                    insertPs.setString(1, "Maya");
                    insertPs.setString(2, "E-Wallet");
                    insertPs.setDouble(3, 1800);
                    insertPs.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}