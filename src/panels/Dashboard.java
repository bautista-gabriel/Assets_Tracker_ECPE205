package panels;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dashboard panel showing summary/statistics.
 *
 * ASSIGNED TO: Student 2 (Main Frame / Dashboard Owner)
 *
 * TODO for Student 2:
 * - Display total number of students -done
 * - Add a welcome message or app logo
 * - Show summary statistics (e.g., average age, total count) -done
 * - Add a refresh button to update the stats -done
 * - Make it visually appealing (use colors, larger fonts, icons)
 */
public class Dashboard extends JPanel implements ActionListener {
    private JLabel countLabel;
    private JLabel average;

    public Dashboard() {
        setLayout(new BorderLayout());

        // Welcome Message
        JLabel title = new JLabel("Asset Tracker Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        // Left Content
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20,40,10,40));

        //Combo box para sa banks
        String[] Providers = {
                "   ",
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
                "---- E-Wallet ----",
                "GCash",
                "Maya",
                "GoTyme",
                "Tonik",
                "UNO Digital Bank",
                "UnionDigital Bank",
                "GrabPay",
                "ShopeePay"
        };

        JComboBox ProvidersList = new JComboBox(Providers);
        ProvidersList.setMaximumSize(new Dimension(250, 30));
        ProvidersList.setPreferredSize(new Dimension(250, 30));
        ProvidersList.setSelectedIndex(0);
        ProvidersList.addActionListener(this);
        leftPanel.add(ProvidersList);
        centerPanel.add(Box.createVerticalStrut(20));

        //Button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshBtn.addActionListener(e -> refreshData());
        centerPanel.add(refreshBtn);



        add(centerPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
    }

    private void refreshData() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}