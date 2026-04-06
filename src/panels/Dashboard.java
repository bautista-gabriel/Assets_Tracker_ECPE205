package panels;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;

public class Dashboard extends JPanel {
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


        centerPanel.add(Box.createVerticalStrut(20));

        //Button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshBtn.addActionListener(e -> refreshData());
        centerPanel.add(refreshBtn);



        add(centerPanel, BorderLayout.CENTER);
    }

    private void refreshData() {

    }
}