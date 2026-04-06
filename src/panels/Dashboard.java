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
        String[] BankNames = {
                "   ",
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
                "Bank of Commerce"
        };

        JComboBox BankList = new JComboBox(BankNames);
        BankList.setMaximumSize(new Dimension(250, 30));
        BankList.setPreferredSize(new Dimension(250, 30));
        BankList.setSelectedIndex(0);
        BankList.addActionListener(this);

        //combo box para sa e wallets
        String[] EwalletNames = {
                "  ",
                "GCash",
                "Maya",
                "GoTyme",
                "Tonik",
                "UNO Digital Bank",
                "UnionDigital Bank",
                "GrabPay",
                "ShopeePay"
        };
        JComboBox EwalletList = new JComboBox(EwalletNames);
        EwalletList.setMaximumSize(new Dimension(250, 30));
        EwalletList.setPreferredSize(new Dimension(250, 30));
        EwalletList.setSelectedIndex(0);
        EwalletList.addActionListener( this);
        leftPanel.add(EwalletList);
        leftPanel.add(BankList);
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