package panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Providers extends JPanel implements ActionListener {

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
                "---- Banks ----",
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
        ProvidersList.addActionListener(this);
        centerPanel.add(ProvidersList);
        centerPanel.add(Box.createVerticalStrut(20));

        // text field for inputting amount
        JTextField amountField = new JTextField();
        amountField.setMaximumSize(new Dimension(250, 30));
        amountField.setPreferredSize(new Dimension(250, 30));
        amountField.setBorder(BorderFactory.createTitledBorder("Amount"));
        centerPanel.add(amountField);
        centerPanel.add(Box.createVerticalStrut(20));
        
        //Button
        JButton saveBtn = new JButton("Save");
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.addActionListener(e -> refreshData());
        centerPanel.add(saveBtn);



        add(centerPanel, BorderLayout.CENTER);
    }

    private void refreshData() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}