
package GUI;

import Broker.DB;
import Domain.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class EquipmentManagementPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField tfSearch;

    private JTable tblEquip;
    private DefaultTableModel equipModel;

    private JTextField tfId;
    private JComboBox<Category> cbCategory;
    private JTextField tfName;
    private JTextField tfDesc;
    private JTextField tfRate;

    private JButton btnSearch;
    private JButton btnNew;
    private JButton btnSave;
    private JButton btnDelete;
    private JLabel lblStatus;

    public EquipmentManagementPanel() {
        buildUI();
        attachHandlers();
        loadCategories();
        loadAllEquipment();
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

        equipModel = new DefaultTableModel(new Object[]{
                "ID", "Category", "Name", "Description", "Daily Rate"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return switch (c) {
                    case 0 -> Integer.class;
                    case 4 -> Double.class;
                    default -> String.class;
                };
            }
        };
        tblEquip = new JTable(equipModel);
        tblEquip.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        split.setLeftComponent(new JScrollPane(tblEquip));

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        int row = 0;
        tfId = new JTextField(10); tfId.setEditable(false);
        cbCategory = new JComboBox<>();
        cbCategory.setPrototypeDisplayValue(new Category(99, "XXXXXXXXXXXXXXXX"));
        tfName = new JTextField(20);
        tfDesc = new JTextField(30);
        tfRate = new JTextField(8);

        addField(form, g, row++, "Equipment ID:", tfId);
        addField(form, g, row++, "Category:", cbCategory);
        addField(form, g, row++, "Name:", tfName);
        addField(form, g, row++, "Description:", tfDesc);
        addField(form, g, row++, "Daily Rate:", tfRate);

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
        tblEquip.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int sel = tblEquip.getSelectedRow();
                if (sel >= 0) {
                    tfId.setText(String.valueOf(equipModel.getValueAt(sel, 0)));
                    selectCategoryByLabel((String) equipModel.getValueAt(sel, 1)); // match by label
                    tfName.setText((String) equipModel.getValueAt(sel, 2));
                    tfDesc.setText((String) equipModel.getValueAt(sel, 3));
                    tfRate.setText(String.valueOf(equipModel.getValueAt(sel, 4)));
                    cbCategory.setEnabled(false); // lock category on edit
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
        btnSave.addActionListener(e -> saveEquipment());
        btnDelete.addActionListener(e -> deleteEquipment());
    }

    // --- Categories ---
    private void loadCategories() {
        cbCategory.removeAllItems();
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT category_id, name FROM category ORDER BY category_id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cbCategory.addItem(new Category(rs.getInt(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            showError("Load categories failed: " + ex.getMessage());
        }
    }

    private void selectCategoryByLabel(String label) {
        for (int i = 0; i < cbCategory.getItemCount(); i++) {
            Category c = cbCategory.getItemAt(i);
            String lbl = c.getCategoryId() + " - " + c.getName();
            if (lbl.equals(label)) {
                cbCategory.setSelectedIndex(i);
                return;
            }
        }
    }

    // --- Equipment ops ---
    private void loadAllEquipment() {
        tfSearch.setText("");
        search();
    }

    private void search() {
        String term = tfSearch.getText().trim();
        String sql =
            "SELECT e.equipment_id, CONCAT(c.category_id, ' - ', c.name) AS cat, " +
            "       e.name, e.description, e.daily_rate, e.seq_in_category " +
            "FROM equipment e JOIN category c ON e.category_id = c.category_id " +
            (term.isEmpty() ? "" :
             "WHERE e.name LIKE ? OR e.description LIKE ? ") +
            "ORDER BY e.equipment_id";

        equipModel.setRowCount(0);

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (!term.isEmpty()) {
                String like = "%" + term + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt(1));
                    row.add(rs.getString(2));
                    row.add(rs.getString(3));
                    row.add(rs.getString(4));
                    row.add(rs.getDouble(5));
                    row.add(rs.getInt(6));
                    equipModel.addRow(row);
                }
            }
            lblStatus.setText("Loaded " + equipModel.getRowCount() + " equipment item(s).");
        } catch (SQLException ex) {
            showError("Search failed: " + ex.getMessage());
        }
    }

    private void clearFormForNew() {
        tfId.setText("");
        tfName.setText("");
        tfDesc.setText("");
        tfRate.setText("");
        cbCategory.setEnabled(true);
        if (cbCategory.getItemCount() > 0) cbCategory.setSelectedIndex(0);
        tblEquip.clearSelection();
        lblStatus.setText("Ready for new equipment.");
    }

    private void saveEquipment() {
        Category cat = (Category) cbCategory.getSelectedItem();
        String name = tfName.getText().trim();
        String desc = tfDesc.getText().trim();
        String rateText = tfRate.getText().trim();

        if (cat == null || name.isEmpty() || desc.isEmpty() || rateText.isEmpty()) {
            showError("Category, Name, Description, and Daily Rate are required.");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateText);
            if (rate < 0.0) { showError("Daily Rate cannot be negative."); return; }
        } catch (NumberFormatException nfe) {
            showError("Daily Rate must be a number.");
            return;
        }

        String idText = tfId.getText().trim();
        boolean isNew = idText.isEmpty();

        try (Connection cn = DB.getConnection()) {
            if (isNew) {
                // INSERT without equipment_id & seq_in_category (trigger will set both)
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO equipment (category_id, name, description, daily_rate) " +
                        "VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, cat.getCategoryId());
                    ps.setString(2, name);
                    ps.setString(3, desc);
                    ps.setBigDecimal(4, new java.math.BigDecimal(String.format("%.2f", rate)));
                    ps.executeUpdate();
                }

                // Fetch newly generated equipment_id (max by category since ID = category||seq)
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT MAX(equipment_id) AS id, MAX(seq_in_category) AS seq " +
                        "FROM equipment WHERE category_id = ?")) {
                    ps.setInt(1, cat.getCategoryId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            tfId.setText(String.valueOf(rs.getInt("id")));
                        }
                    }
                }
                cbCategory.setEnabled(false);
                lblStatus.setText("Inserted equipment in category " + cat.getCategoryId());

            } else {
                int id = Integer.parseInt(idText);
                // Lock category on update; update name/desc/rate
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE equipment SET name=?, description=?, daily_rate=? WHERE equipment_id=?")) {
                    ps.setString(1, name);
                    ps.setString(2, desc);
                    ps.setBigDecimal(3, new java.math.BigDecimal(String.format("%.2f", rate)));
                    ps.setInt(4, id);
                    int count = ps.executeUpdate();
                    lblStatus.setText(count == 1 ? "Updated equipment #" + id : "No rows updated.");
                }
            }

            search(); // refresh list

        } catch (SQLException ex) {
            showError("Save failed: " + ex.getMessage());
        }
    }

    private void deleteEquipment() {
        String idText = tfId.getText().trim();
        if (idText.isEmpty()) { showError("Select an equipment to delete."); return; }
        int id = Integer.parseInt(idText);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete equipment #" + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "DELETE FROM equipment WHERE equipment_id=?")) {
            ps.setInt(1, id);
            int count = ps.executeUpdate();
            if (count == 1) {
                lblStatus.setText("Deleted equipment #" + id);
                clearFormForNew();
                search();
            } else {
                showError("Delete failed: no such equipment.");
            }
        } catch (SQLException ex) {
            // FK from rental_item will block delete; show friendly message
            showError("Delete failed: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
