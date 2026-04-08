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
            "BPI", "BDO", "Metrobank", "UnionBank", "GCash", "Maya", "ShopeePay", "GrabPay"
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
        setLayout(new BorderLayout());
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
        refreshSummary();
    }

    // Toolbar — uses GridBagLayout so controls stretch with window width
    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new GridBagLayout());
        toolbar.setBackground(TOOLBAR_BG);
        toolbar.setBorder(new EmptyBorder(8, 14, 8, 14));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets     = new Insets(0, 4, 0, 4);
        gc.fill       = GridBagConstraints.HORIZONTAL;
        gc.gridy      = 0;
        gc.anchor     = GridBagConstraints.WEST;

        // Title — takes all leftover horizontal space
        JLabel title = new JLabel("Sub Accounts");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(Color.WHITE);
        gc.gridx = 0; gc.weightx = 1.0;
        toolbar.add(title, gc);

        // Buttons — fixed size
        btnAdd          = makeBtn("+ Add",    BTN_ADD);
        btnEdit         = makeBtn("✎ Edit",   BTN_EDIT);
        btnDelete       = makeBtn("✕ Delete", BTN_DEL);
        btnToggleStatus = makeBtn("⇄ Status", BTN_TOGGLE);
        btnAdd.addActionListener(this);
        btnEdit.addActionListener(this);
        btnDelete.addActionListener(this);
        btnToggleStatus.addActionListener(this);

        gc.weightx = 0;
        gc.gridx = 1; toolbar.add(btnAdd,          gc);
        gc.gridx = 2; toolbar.add(btnEdit,         gc);
        gc.gridx = 3; toolbar.add(btnDelete,        gc);
        gc.gridx = 4; toolbar.add(btnToggleStatus,  gc);

        // Separator
        gc.gridx = 5;
        toolbar.add(Box.createHorizontalStrut(8), gc);

        // Filter label + combo
        JLabel filterLbl = new JLabel("Filter:");
        filterLbl.setForeground(Color.WHITE);
        filterLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gc.gridx = 6; toolbar.add(filterLbl, gc);

        filterParent = new JComboBox<>();
        filterParent.addItem("All Accounts");
        for (String p : PARENT_ACCOUNTS) filterParent.addItem(p);
        filterParent.addActionListener(e -> applyFilter());
        gc.gridx = 7; gc.weightx = 0.15;
        toolbar.add(filterParent, gc);

        // Search label + field — stretches a little with the window
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setForeground(Color.WHITE);
        searchLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gc.gridx = 8; gc.weightx = 0;
        toolbar.add(searchLbl, gc);

        searchField = new JTextField();
        searchField.setToolTipText("Search by name, number or type");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { applyFilter(); }
        });
        gc.gridx = 9; gc.weightx = 0.25;
        toolbar.add(searchField, gc);

        return toolbar;
    }

    // Table — AUTO_RESIZE_ALL_COLUMNS so columns fill the full width
    private JScrollPane buildTable() {
        tableModel = new SubAccountTableModel();
        table      = new JTable(tableModel);

        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(210, 230, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);

        // Columns auto-resize to fill the full table width
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        // Alternating row renderer
        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!sel) {
                    setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                    setForeground(new Color(33, 33, 33));
                }
                return this;
            }
        };
        for (int i = 0; i < tableModel.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);

        // Status column — colored
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!sel) {
                    setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                    setForeground("Active".equals(value)
                            ? new Color(39, 174, 96) : new Color(192, 57, 43));
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return this;
            }
        });

        // Proportional column weights (sum = 100)
        int total = 7;
        int[] weights = {15, 18, 17, 12, 12, 9, 10}; // rough % each column gets
        // Set min widths; AUTO_RESIZE_ALL_COLUMNS handles the rest
        int[] minWidths = {90, 110, 110, 80, 80, 60, 70};
        for (int i = 0; i < total; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setMinWidth(minWidths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        // Ensure scroll pane fills all remaining vertical space
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    // Footer — stretches labels evenly across full width
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new GridBagLayout());
        footer.setBackground(new Color(236, 240, 241));
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
                new EmptyBorder(6, 14, 6, 14)
        ));

        lblTotal   = new JLabel("Sub-accounts: 0");
        lblActive  = new JLabel("Active: 0");
        lblBalance = new JLabel("Total Balance: PHP 0.00");

        Font  f = new Font("SansSerif", Font.BOLD, 12);
        Color c = new Color(44, 62, 80);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy   = 0;
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        for (JLabel l : new JLabel[]{lblTotal, lblActive, lblBalance}) {
            l.setFont(f);
            l.setForeground(c);
            footer.add(l, gc);
        }
        return footer;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // ActionListener
    // ─────────────────────────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────────────────────────
    // Form Dialog
    // ─────────────────────────────────────────────────────────────────────────────
    private SubAccount showFormDialog(String title, SubAccount existing) {
        JComboBox<String> cmbParent = new JComboBox<>(PARENT_ACCOUNTS);
        JTextField        txtName   = new JTextField(18);
        JTextField        txtNumber = new JTextField(18);
        JComboBox<String> cmbType   = new JComboBox<>(ACCOUNT_TYPES);
        JTextField        txtBal    = new JTextField(18);
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
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
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
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
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
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}