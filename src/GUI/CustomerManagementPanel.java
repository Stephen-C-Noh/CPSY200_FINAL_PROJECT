
package GUI;

import Broker.DB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class CustomerManagementPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField tfSearch;

    private JTable tblCustomers;
    private DefaultTableModel customersModel;

    private JTextField tfId;
    private JTextField tfFirst;
    private JTextField tfLast;
    private JTextField tfPhone;
    private JTextField tfEmail;
    private JCheckBox cbActive;
    private JTextField tfDiscount;
    private JTextArea taNote;

    private JButton btnSearch;
    private JButton btnNew;
    private JButton btnSave;
    private JButton btnDelete;
    private JLabel lblStatus;

    public CustomerManagementPanel() {
        buildUI();
        attachHandlers();
        loadAllCustomers();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Top: search bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tfSearch = new JTextField(20);
        btnSearch = new JButton("Search");
        btnNew    = new JButton("New");
        top.add(new JLabel("Search:"));
        top.add(tfSearch);
        top.add(btnSearch);
        top.add(btnNew);
        add(top, BorderLayout.NORTH);

        // Center: table + form
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.55);

        customersModel = new DefaultTableModel(new Object[]{
                "ID", "First", "Last", "Phone", "Email", "Active", "Discount", "Note"
        }, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 0) return Integer.class;
                if (c == 5) return Boolean.class;
                if (c == 6) return Double.class;
                return String.class;
            }
        };
        tblCustomers = new JTable(customersModel);
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        split.setLeftComponent(new JScrollPane(tblCustomers));

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        int row = 0;
        tfId = new JTextField(10); tfId.setEditable(false);
        tfFirst = new JTextField(15);
        tfLast  = new JTextField(15);
        tfPhone = new JTextField(15);
        tfEmail = new JTextField(20);
        cbActive = new JCheckBox("Active");
        tfDiscount = new JTextField(6);
        taNote = new JTextArea(4, 20);
        taNote.setLineWrap(true);
        taNote.setWrapStyleWord(true);

        addField(form, g, row++, "Customer ID:", tfId);
        addField(form, g, row++, "First Name:", tfFirst);
        addField(form, g, row++, "Last Name:", tfLast);
        addField(form, g, row++, "Phone Number:", tfPhone);
        addField(form, g, row++, "Email:", tfEmail);
        addField(form, g, row++, "Discount (%)", tfDiscount);

        // Active checkbox row
        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        form.add(cbActive, g);
        row++;

        // Note
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        form.add(new JLabel("Note:"), g);
        g.gridx = 1; g.gridy = row; g.gridwidth = 1; g.fill = GridBagConstraints.BOTH; g.weighty = 1.0;
        form.add(new JScrollPane(taNote), g);
        g.fill = GridBagConstraints.HORIZONTAL; g.weighty = 0.0;
        row++;

        // Buttons
        JPanel formBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnSave  = new JButton("Save");
        btnDelete= new JButton("Delete");
        formBtns.add(btnSave);
        formBtns.add(btnDelete);

        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        form.add(formBtns, g);

        split.setRightComponent(form);
        add(split, BorderLayout.CENTER);

        // Bottom: status
        lblStatus = new JLabel(" ");
        add(lblStatus, BorderLayout.SOUTH);

        // Table selection to form
        tblCustomers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int sel = tblCustomers.getSelectedRow();
                if (sel >= 0) {
                    tfId.setText(String.valueOf(customersModel.getValueAt(sel, 0)));
                    tfFirst.setText((String) customersModel.getValueAt(sel, 1));
                    tfLast.setText((String) customersModel.getValueAt(sel, 2));
                    tfPhone.setText((String) customersModel.getValueAt(sel, 3));
                    tfEmail.setText((String) customersModel.getValueAt(sel, 4));
                    cbActive.setSelected((Boolean) customersModel.getValueAt(sel, 5));
                    tfDiscount.setText(String.valueOf(customersModel.getValueAt(sel, 6)));
                    Object n = customersModel.getValueAt(sel, 7);
                    taNote.setText(n == null ? "" : n.toString());
                }
            }
        });
    }

    private void addField(JPanel panel, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        panel.add(new JLabel(label), g);
        g.gridx = 1; g.gridy = row; g.gridwidth = 1;
        panel.add(field, g);
    }

    private void attachHandlers() {
        btnSearch.addActionListener(e -> search());
        btnNew.addActionListener(e -> clearFormForNew());
        btnSave.addActionListener(e -> saveCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
    }

    private void loadAllCustomers() { tfSearch.setText(""); search(); }

    private void search() {
        String term = tfSearch.getText().trim();
        String sql =
            "SELECT customer_id, first_name, last_name, phone_number, email, is_active, discount_rate, note " +
            "FROM customer " +
            (term.isEmpty() ? "" :
                "WHERE first_name LIKE ? OR last_name LIKE ? OR phone_number LIKE ? OR email LIKE ? ") +
            "ORDER BY customer_id";

        customersModel.setRowCount(0);

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (!term.isEmpty()) {
                String like = "%" + term + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
                ps.setString(4, like);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("customer_id"));
                    row.add(rs.getString("first_name"));
                    row.add(rs.getString("last_name"));
                    row.add(rs.getString("phone_number"));
                    row.add(rs.getString("email"));
                    row.add("Y".equalsIgnoreCase(rs.getString("is_active")));
                    row.add(rs.getDouble("discount_rate"));
                    row.add(rs.getString("note"));
                    customersModel.addRow(row);
                }
            }
            lblStatus.setText("Loaded " + customersModel.getRowCount() + " customer(s).");
        } catch (SQLException ex) {
            showError("Search failed: " + ex.getMessage());
        }
    }

    private void clearFormForNew() {
        tfId.setText("");
        tfFirst.setText("");
        tfLast.setText("");
        tfPhone.setText("");
        tfEmail.setText("");
        cbActive.setSelected(true);
        tfDiscount.setText("0.00");
        taNote.setText("");
        tblCustomers.clearSelection();
        lblStatus.setText("Ready for new customer.");
    }

    private void saveCustomer() {
        String first = tfFirst.getText().trim();
        String last  = tfLast.getText().trim();
        String phone = tfPhone.getText().trim();
        String email = tfEmail.getText().trim();
        boolean active = cbActive.isSelected();

        if (first.isEmpty() || last.isEmpty() || phone.isEmpty()) {
            showError("First, Last, and Phone number are required.");
            return;
        }

        double discount = 0.0;
        try {
            String discText = tfDiscount.getText().trim();
            discount = discText.isEmpty() ? 0.0 : Double.parseDouble(discText);
            if (discount < 0.0) { showError("Discount cannot be negative."); return; }
        } catch (NumberFormatException nfe) {
            showError("Discount must be a number.");
            return;
        }

        String note = taNote.getText().trim();
        String idText = tfId.getText().trim();
        boolean isNew = idText.isEmpty();

        try (Connection cn = DB.getConnection()) {
            if (isNew) {
                try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO customer (first_name, last_name, phone_number, email, is_active, discount_rate, note) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, first);
                    ps.setString(2, last);
                    ps.setString(3, phone);
                    ps.setString(4, email.isEmpty() ? null : email);
                    ps.setString(5, active ? "Y" : "N");
                    ps.setBigDecimal(6, new java.math.BigDecimal(String.format("%.2f", discount)));
                    ps.setString(7, note.isEmpty() ? null : note);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            int newId = keys.getInt(1);
                            tfId.setText(String.valueOf(newId));
                            lblStatus.setText("Inserted customer #" + newId);
                        }
                    }
                }
            } else {
                int id = Integer.parseInt(idText);
                try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE customer SET first_name=?, last_name=?, phone_number=?, email=?, is_active=?, discount_rate=?, note=? " +
                    "WHERE customer_id=?")) {
                    ps.setString(1, first);
                    ps.setString(2, last);
                    ps.setString(3, phone);
                    ps.setString(4, email.isEmpty() ? null : email);
                    ps.setString(5, active ? "Y" : "N");
                    ps.setBigDecimal(6, new java.math.BigDecimal(String.format("%.2f", discount)));
                    ps.setString(7, note.isEmpty() ? null : note);
                    ps.setInt(8, id);
                    int count = ps.executeUpdate();
                    lblStatus.setText(count == 1 ? "Updated customer #" + id : "No rows updated.");
                }
            }
            search(); // refresh list
        } catch (SQLException ex) {
            showError("Save failed: " + ex.getMessage());
        }
    }

    private void deleteCustomer() {
        String idText = tfId.getText().trim();
        if (idText.isEmpty()) { showError("Select a customer to delete."); return; }
        int id = Integer.parseInt(idText);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete customer #" + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
         

        try (Connection cn = DB.getConnection(); PreparedStatement pps = cn.prepareStatement("SELECT COUNT(rental_id) FROM rental WHERE customer_id=?");
             PreparedStatement ps = cn.prepareStatement("DELETE FROM customer WHERE customer_id=?")) {
            pps.setInt(1, id);
        	int rentalCount = 0;
        	try(ResultSet rs = pps.executeQuery()){
        		if(rs.next()) {
        			rentalCount = rs.getInt(1);
        		}
        	}
        	if (rentalCount > 0) {
        		showError("This customer has "+rentalCount+" rental request(s). Please delete those request first and Try again.");
        		return;
        	}
        	
        	ps.setInt(1, id);
        	int count = ps.executeUpdate();
            if (count == 1) {
                lblStatus.setText("Deleted customer #" + id);
                clearFormForNew();
                search();
            } else {
                showError("Delete failed: no such customer.");
            }
        } catch (SQLException ex) {
            showError("Delete failed: \n" + ex.getMessage()+"\n");
        }
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}