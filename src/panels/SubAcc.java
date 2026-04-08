package panels;

import model.SubAccount;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SubAcc extends JPanel implements ActionListener {

    
    // Table Model
    
    private static class SubAccountTableModel extends AbstractTableModel {
        private final String[] COLUMNS = {
                "Parent Account", "Account Name", "Account Number",
                "Type", "Balance", "Currency", "Status"
        };
        private List<SubAccount> data = new ArrayList<>();

        public void setData(List<SubAccount> list) {
            this.data = new ArrayList<>(list);
            fireTableDataChanged();
        }

        public SubAccount getRow(int row)             { return data.get(row); }
        public void updateRow(int row, SubAccount sa) { data.set(row, sa); fireTableRowsUpdated(row, row); }
        public void removeRow(int row)                { data.remove(row); fireTableRowsDeleted(row, row); }

        @Override public int    getRowCount()          { return data.size(); }
        @Override public int    getColumnCount()       { return COLUMNS.length; }
        @Override public String getColumnName(int col) { return COLUMNS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            SubAccount sa = data.get(row);
            switch (col) {
                case 0: return sa.getParentAccount();
                case 1: return sa.getAccountName();
                case 2: return sa.getAccountNumber();
                case 3: return sa.getAccountType();
                case 4: return String.format("%.2f", sa.getBalance());
                case 5: return sa.getCurrency();
                case 6: return sa.isActive() ? "Active" : "Inactive";
                default: return "";
            }
        }
    }

    
    // Constants
    
    private static final String[] PARENT_ACCOUNTS = {
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
    private static final String[] ACCOUNT_TYPES = {
            "Savings", "Checking", "Wallet", "Investment", "Business"
    };
    private static final String[] CURRENCIES = { "PHP", "USD", "EUR", "GBP", "JPY" };

    private static final Color TOOLBAR_BG = new Color(44, 62, 80);
    private static final Color BTN_ADD    = new Color(39, 174, 96);
    private static final Color BTN_EDIT   = new Color(41, 128, 185);
    private static final Color BTN_DEL    = new Color(192, 57, 43);
    private static final Color BTN_TOGGLE = new Color(243, 156, 18);
    private static final Color ROW_EVEN   = Color.WHITE;
    private static final Color ROW_ODD    = new Color(245, 248, 250);

    
    // Fields
    
    private SubAccountTableModel tableModel;
    private JTable               table;
    private JTextField           searchField;
    private JComboBox<String>    filterParent;
    private List<SubAccount>     allData = new ArrayList<>();

    private JButton btnAdd, btnEdit, btnDelete, btnToggleStatus;
    private JLabel  lblTotal, lblActive, lblBalance;

    
    // Constructor
    
    public SubAcc() {
        setLayout(new BorderLayout(0, 0));

        // Panel fills its parent completely — critical for 700×500 frames
        setMinimumSize(new Dimension(600, 400));
        setPreferredSize(new Dimension(700, 500));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);   // CENTER stretches in both axes
        add(buildFooter(),  BorderLayout.SOUTH);

        refreshSummary();
    }
    
    private JPanel buildToolbar() {

        //  Outer wrapper: locks height to its preferred size 
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override public Dimension getMaximumSize() {
                // Allow horizontal stretch; block vertical growth
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
            @Override public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        };
        wrapper.setBackground(TOOLBAR_BG);
        wrapper.setBorder(new EmptyBorder(6, 10, 6, 10));

        //  Left: Title 
        JLabel title = new JLabel("Sub Accounts");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 0, 10));
        wrapper.add(title, BorderLayout.WEST);

        //  Centre: 4 action buttons, all forced to the same fixed size 
        btnAdd          = makeBtn("+ Add",    BTN_ADD);
        btnEdit         = makeBtn("✎ Edit",   BTN_EDIT);
        btnDelete       = makeBtn("✕ Delete", BTN_DEL);
        btnToggleStatus = makeBtn("⇄ Status", BTN_TOGGLE);
        btnAdd.addActionListener(this);
        btnEdit.addActionListener(this);
        btnDelete.addActionListener(this);
        btnToggleStatus.addActionListener(this);

        // Measure the widest button label, then lock every button to that size
        Dimension btnSize = new Dimension(90, 28);   // fixed: wide enough for "✕ Delete"
        for (JButton b : new JButton[]{ btnAdd, btnEdit, btnDelete, btnToggleStatus }) {
            b.setPreferredSize(btnSize);
            b.setMinimumSize(btnSize);
            b.setMaximumSize(btnSize);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnToggleStatus);
        wrapper.add(btnPanel, BorderLayout.CENTER);

        //  Right: Filter combo + Search field 
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        rightPanel.setOpaque(false);

        rightPanel.add(makeWhiteLabel("Filter:"));

        filterParent = new JComboBox<>();
        filterParent.addItem("All Accounts");
        for (String p : PARENT_ACCOUNTS) filterParent.addItem(p);
        filterParent.setFont(new Font("SansSerif", Font.PLAIN, 11));
        filterParent.setPreferredSize(new Dimension(120, 24));
        filterParent.addActionListener(e -> applyFilter());
        rightPanel.add(filterParent);

        rightPanel.add(makeWhiteLabel("Search:"));

        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 11));
        searchField.setPreferredSize(new Dimension(110, 24));
        searchField.setToolTipText("Search by name, number or type");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { applyFilter(); }
        });
        rightPanel.add(searchField);

        wrapper.add(rightPanel, BorderLayout.EAST);

        return wrapper;
    }

    
    // Table  — fills all remaining vertical space between toolbar and footer
    
    private JScrollPane buildTable() {
        tableModel = new SubAccountTableModel();
        table      = new JTable(tableModel);

        // Slightly smaller font so 7 columns fit comfortably at 700 px
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(26);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(210, 230, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);   // blank area below rows fills the view

        // Columns proportionally resize whenever the panel resizes
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 30));
        header.setReorderingAllowed(false);

        // Default alternating row renderer
        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                setBorder(new EmptyBorder(0, 6, 0, 6));
                if (!sel) {
                    setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                    setForeground(new Color(33, 33, 33));
                }
                return this;
            }
        };
        for (int i = 0; i < tableModel.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);

        // Status column — coloured text
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                setBorder(new EmptyBorder(0, 6, 0, 6));
                if (!sel) {
                    setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                    setForeground("Active".equals(value)
                            ? new Color(39, 174, 96) : new Color(192, 57, 43));
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return this;
            }
        });

        // Minimum column widths — AUTO_RESIZE handles distribution beyond these
        int[] minWidths = { 80, 90, 90, 65, 65, 50, 60 };
        for (int i = 0; i < minWidths.length; i++)
            table.getColumnModel().getColumn(i).setMinWidth(minWidths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Ensures JScrollPane grows/shrinks with the panel (critical for CENTER slot)
        scroll.setPreferredSize(new Dimension(0, 0));
        return scroll;
    }

    
    // Footer  — single-row summary, fixed height, spans full width
    
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new GridLayout(1, 3, 0, 0)) {   // 3 equal columns
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
            @Override public Dimension getMinimumSize() { return getPreferredSize(); }
        };
        footer.setBackground(new Color(236, 240, 241));
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
                new EmptyBorder(5, 10, 5, 10)
        ));

        lblTotal   = new JLabel("Sub-accounts: 0");
        lblActive  = new JLabel("Active: 0", SwingConstants.CENTER);
        lblBalance = new JLabel("Total Balance: PHP 0.00", SwingConstants.RIGHT);

        Font  f = new Font("SansSerif", Font.BOLD, 11);
        Color c = new Color(44, 62, 80);

        for (JLabel l : new JLabel[]{ lblTotal, lblActive, lblBalance }) {
            l.setFont(f);
            l.setForeground(c);
            footer.add(l);
        }
        return footer;
    }

    
    // ActionListener
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if      (src == btnAdd)          showAddDialog();
        else if (src == btnEdit)         showEditDialog();
        else if (src == btnDelete)       deleteSelected();
        else if (src == btnToggleStatus) toggleStatus();
    }

    
    // CRUD
    
    private void showAddDialog() {
        SubAccount sa = showFormDialog("Add Sub-Account", null);
        if (sa != null) { allData.add(sa); applyFilter(); }
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Please select a sub-account to edit."); return; }
        SubAccount displayed = tableModel.getRow(row);
        int realIndex = allData.indexOf(displayed);
        SubAccount updated = showFormDialog("Edit Sub-Account", displayed);
        if (updated != null && realIndex >= 0) {
            allData.set(realIndex, updated);
            applyFilter();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Please select a sub-account to delete."); return; }
        SubAccount displayed = tableModel.getRow(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + displayed.getAccountName() + "\" from " + displayed.getParentAccount() + "?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) { allData.remove(displayed); applyFilter(); }
    }

    private void toggleStatus() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Please select a sub-account to toggle."); return; }
        SubAccount sa = tableModel.getRow(row);
        sa.setActive(!sa.isActive());
        tableModel.updateRow(row, sa);
        refreshSummary();
    }

    
    // Form Dialog
    
    private SubAccount showFormDialog(String title, SubAccount existing) {
        JComboBox<String> cmbParent = new JComboBox<>(PARENT_ACCOUNTS);
        JTextField        txtName   = new JTextField(16);
        JTextField        txtNumber = new JTextField(16);
        JComboBox<String> cmbType   = new JComboBox<>(ACCOUNT_TYPES);
        JTextField        txtBal    = new JTextField(16);
        JComboBox<String> cmbCurr   = new JComboBox<>(CURRENCIES);
        JCheckBox         chkActive = new JCheckBox("Active", true);

        if (existing != null) {
            cmbParent.setSelectedItem(existing.getParentAccount());
            txtName.setText(existing.getAccountName());
            txtNumber.setText(existing.getAccountNumber());
            cmbType.setSelectedItem(existing.getAccountType());
            txtBal.setText(String.valueOf(existing.getBalance()));
            cmbCurr.setSelectedItem(existing.getCurrency());
            chkActive.setSelected(existing.isActive());
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        String[]    labels = {
                "Parent Account:", "Account Name:", "Account Number:",
                "Account Type:", "Balance:", "Currency:", "Status:"
        };
        Component[] fields = {
                cmbParent, txtName, txtNumber, cmbType, txtBal, cmbCurr, chkActive
        };

        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            form.add(lbl, gc);
            gc.gridx = 1; gc.weightx = 1.0;
            form.add(fields[i], gc);
        }

        int result = JOptionPane.showConfirmDialog(
                this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return null;

        String name   = txtName.getText().trim();
        String number = txtNumber.getText().trim();
        String balStr = txtBal.getText().trim();

        if (name.isEmpty() || number.isEmpty() || balStr.isEmpty()) {
            warn("All fields are required."); return null;
        }
        double balance;
        try {
            balance = Double.parseDouble(balStr);
            if (balance < 0) { warn("Balance cannot be negative."); return null; }
        } catch (NumberFormatException ex) {
            warn("Balance must be a valid number."); return null;
        }

        return new SubAccount(
                (String) cmbParent.getSelectedItem(),
                name, number,
                (String) cmbType.getSelectedItem(),
                balance,
                (String) cmbCurr.getSelectedItem(),
                chkActive.isSelected()
        );
    }

    
    // Filter
    
    private void applyFilter() {
        String keyword = searchField.getText().trim().toLowerCase();
        String parent  = (String) filterParent.getSelectedItem();

        List<SubAccount> filtered = new ArrayList<>(allData);
        if (!"All Accounts".equals(parent))
            filtered.removeIf(sa -> !sa.getParentAccount().equals(parent));
        if (!keyword.isEmpty())
            filtered.removeIf(sa ->
                    !sa.getAccountName().toLowerCase().contains(keyword) &&
                            !sa.getAccountNumber().toLowerCase().contains(keyword) &&
                            !sa.getAccountType().toLowerCase().contains(keyword));

        tableModel.setData(filtered);
        refreshSummary();
    }

    
    // Summary
    
    private void refreshSummary() {
        int total  = tableModel.getRowCount();
        int active = 0;
        double sum = 0;
        for (int i = 0; i < total; i++) {
            SubAccount sa = tableModel.getRow(i);
            if (sa.isActive()) active++;
            sum += sa.getBalance();
        }
        lblTotal.setText("Sub-accounts: " + total);
        lblActive.setText("Active: " + active);
        lblBalance.setText(String.format("Total Balance: PHP %.2f", sum));
    }

    
    // Helpers
    
    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 10, 5, 10));
        return btn;
    }

    private JLabel makeWhiteLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        return lbl;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}