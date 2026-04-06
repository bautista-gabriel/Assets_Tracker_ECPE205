package app;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import panels.Dashboard;
import panels.Providers;
import panels.SubAcc;

/**
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Asset Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null); // Center on screen

        //add(new Providers(), BorderLayout.CENTER);

        JPanel SidePanel = new JPanel();
            SidePanel.setPreferredSize(new Dimension(200, 0));
            SidePanel.setLayout(new BoxLayout(SidePanel, BoxLayout.Y_AXIS));
            SidePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

            JButton dashboardBtn = new JButton("Dashboard");
            dashboardBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            dashboardBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            JButton providersBtn = new JButton("Banks and E-Wallets");
            providersBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            providersBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            JButton subAccBtn = new JButton("Sub Accounts");
            subAccBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            subAccBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

            SidePanel.add(dashboardBtn);
            SidePanel.add(Box.createVerticalStrut(10));
            SidePanel.add(providersBtn);
            SidePanel.add(Box.createVerticalStrut(10));
            SidePanel.add(subAccBtn);


            SidePanel.setBackground(Color.gray);
            add(SidePanel, BorderLayout.WEST);

             CardLayout cardLayout = new CardLayout();
             JPanel mainPanel = new JPanel(cardLayout);

            mainPanel.add(new Dashboard(), "Dashboard");
            mainPanel.add(new Providers(), "Providers");
            mainPanel.add(new SubAcc(), "SubAcc");

             add(mainPanel, BorderLayout.CENTER);

            dashboardBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, "Dashboard");
                }
            });

            providersBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, "Providers");
                }
            });

        subAccBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "SubAcc");
            }
        });

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });

    }
}