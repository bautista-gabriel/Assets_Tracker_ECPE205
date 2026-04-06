package app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.MalformedURLException;
import panels.Dashboard;
/**
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Asset Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null); // Center on screen
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Dashboard", new Dashboard());
        add(tabbedPane, BorderLayout.CENTER);
    }
    private static JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("About");
        helpMenu.add(helpItem);
        helpItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Submitted by Bautista, Juesna, Marfil, Milos, and Roquero");
        });
        return helpMenu;
    }

    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createHelpMenu());
        return menuBar;
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setJMenuBar(createMenuBar());
            frame.setVisible(true);
        });

    }
}