package app;

import database.Database;
import panels.AmountManagement;
import panels.Dashboard;
import panels.Providers;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private Providers providersPanel;
    private Dashboard dashboardPanel;
    private AmountManagement subAccPanel;

    public MainFrame() {
        setTitle("Asset Tracker");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Database.init();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        dashboardPanel = new Dashboard();

        providersPanel = new Providers((accountId, accountName) -> {
            subAccPanel.loadAccount(accountId);
            cardLayout.show(mainPanel, "SUBACC");
        }, this::refreshHome);

        subAccPanel = new AmountManagement(
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
        dashboardPanel.setPreferredSize(new Dimension(0, 140));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
