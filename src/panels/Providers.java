package panels;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import database.DatabaseConnection;


public class Providers extends JPanel {
    private Dashboard dashboard; // Reference to the dashboard for live updates


    // FIX: Constructor now requires a Dashboard object
    public Providers(Dashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());


        JLabel title = new JLabel("Banks and E-Wallets",
                SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));


        String[] providerList = {
                "---- Banks ----", //i=0
                "BDO Unibank",
                "Bank of the Philippine Islands (BPI)",
                "Land Bank of the Philippines",
                "Metropolitan Bank & Trust Company (Metrobank)",
                "Philippine National Bank (PNB)",
                "Security Bank",
                "China Banking Corporation (China Bank)",
                "Rizal Commercial Banking Corporation (RCBC)",
                "Union Bank of the Philippines (UnionBank)",
                "Development Bank of the Philippines (DBP)",
                "EastWest Bank",
                "Asia United Bank (AUB)",
                "Philippine Bank of Communications (PBCom)",
                "Philippine Veterans Bank",
                "Philtrust Bank",
                "Bank of Commerce",
                "Tonik",
                "UNO Digital Bank",
                "UnionDigital Bank",
                "GoTyme",
                "---- E-Wallet ----",
                "GCash",
                "Maya",
                "GrabPay",
                "ShopeePay"

        };


        JComboBox<String> providersList = new JComboBox<>(providerList);
        providersList.setMaximumSize(new Dimension(250, 30));
        centerPanel.add(providersList);
        centerPanel.add(Box.createVerticalStrut(20));


        JTextField amountField = new JTextField();
        amountField.setMaximumSize(new Dimension(250, 40));
        amountField.setBorder(BorderFactory.createTitledBorder("Amount"));
        centerPanel.add(amountField);
        centerPanel.add(Box.createVerticalStrut(20));


        JButton saveBtn = new JButton("Save");
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(saveBtn);
        add(centerPanel, BorderLayout.CENTER);


        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = providersList.getSelectedItem().toString();
                String amountStr = amountField.getText();


                if(isDouble(amountStr) && !selected.startsWith("----")) {
                    double amount = Double.parseDouble(amountStr);
                    String sql = "INSERT INTO assets (provider_name, amount) VALUES (?,?)";


                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement pst = conn.prepareStatement(sql)) {


                        pst.setString(1, selected);
                        pst.setDouble(2, amount);
                        pst.executeUpdate();


                        JOptionPane.showMessageDialog(null, "Saved successfully");
                        amountField.setText("");


                        // FIX: LIVE UPDATE - Trigger the dashboard to refresh
                        if (dashboard != null) {
                            dashboard.updateDashboard();
                        }


                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid selection or amount!");
                }
            }
        });
    }


    public boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true; }
        catch (Exception e) {
            return false; }
    }
}

