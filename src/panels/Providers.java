package panels;

import java.awt.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import database.Database;

public class Providers extends JPanel  {

    public  Providers() {
        setLayout(new BorderLayout());

        // Welcome Message
        JLabel title = new JLabel("Banks and E-Wallets", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        //Combo box para sa banks and e wallets
        String[] Providers = {
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

        JComboBox ProvidersList = new JComboBox(Providers);
        ProvidersList.setMaximumSize(new Dimension(250, 30));
        ProvidersList.setPreferredSize(new Dimension(250, 30));
        ProvidersList.setSelectedIndex(0);
       // ProvidersList.addActionListener(this);
        centerPanel.add(ProvidersList);
        centerPanel.add(Box.createVerticalStrut(20));

        // text field for inputting amount
        JTextField amountField = new JTextField();
        amountField.setMaximumSize(new Dimension(250, 30));
        amountField.setPreferredSize(new Dimension(250, 30));
        amountField.setBorder(BorderFactory.createTitledBorder("Amount"));
        centerPanel.add(amountField);
        centerPanel.add(Box.createVerticalStrut(20));

        String[] operations = {"Add", "Subtract"};
        JComboBox<String> operationList = new JComboBox<>(operations);
        operationList.setMaximumSize(new Dimension(250, 30));
        operationList.setPreferredSize(new Dimension(250, 30));
        centerPanel.add(operationList);
        centerPanel.add(Box.createVerticalStrut(20));
        
        //Button
        JButton saveBtn = new JButton("Save");
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.addActionListener(e -> refreshData());
        centerPanel.add(saveBtn);



        add(centerPanel, BorderLayout.CENTER);

        saveBtn.addActionListener(e -> {
            String selected = ProvidersList.getSelectedItem().toString();
            if (selected.equals("---- Banks ----") || selected.equals("---- E-Wallet ----")) {
                JOptionPane.showMessageDialog(null, "Select a Bank/E-Wallet");
                return;
            }
            if (amountField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Input amount!");
                return;
            }
            if (!isDouble(amountField.getText())) {
                JOptionPane.showMessageDialog(null, "Only use numbers!");
                return;
            }
            double amount = Double.parseDouble(amountField.getText());
            if ("Subtract".equals(operationList.getSelectedItem())) {
                amount = -amount;
            }
            String sql = "INSERT INTO main_accounts (provider_id, amount, updated_at) " +
                    "SELECT id, ?, datetime('now') FROM providers WHERE name = ? " +
                    "ON CONFLICT(provider_id) DO UPDATE SET " +
                    "amount = main_accounts.amount + excluded.amount, updated_at = excluded.updated_at";
            try (Connection conn = Database.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setDouble(1, amount);
                pst.setString(2, selected);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Saved successfully");
                amountField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            }
        });
    }

    //check niya kung input mo is num or not
    public boolean isDouble(String input){
        try{
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private void refreshData() {

    }


}
