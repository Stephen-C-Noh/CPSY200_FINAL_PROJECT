
package GUI;

import Broker.DB;
import Domain.Customer;
import Domain.Equipment;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class RentalPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<Customer> cbCustomer;
    private JXDatePicker dpRequestDate;

    private JComboBox<Equipment> cbEquipment;
    private JXDatePicker dpRentDate;
    private JXDatePicker dpReturnDate;

    private JButton btnAddItem;
    private JTable tblItems;
    private DefaultTableModel itemsModel;
    private JButton btnSave;
    private JLabel lblStatus;

    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public RentalPanel() {
        buildUI();
        loadCustomers();
        loadEquipment();
        setDefaults();
        attachHandlers();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel top = new JPanel(new GridLayout(2,1,8,8));

        // Row 1: Customer + request date
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Customer:"));
        cbCustomer = new JComboBox<>();
        cbCustomer.setPrototypeDisplayValue(new Customer(9999, "XXXXXXXXXXXXXXXX"));
        row1.add(cbCustomer);

        row1.add(new JLabel("Request Date (yyyy-MM-dd):"));
        dpRequestDate = makeDatePicker();
        row1.add(dpRequestDate);
        top.add(row1);

        // Row 2: Equipment + rental/return + add
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Equipment:"));
        cbEquipment = new JComboBox<>();
        cbEquipment.setPrototypeDisplayValue(new Equipment(9999, "XXXXXXXXXXXXXXXX"));
        row2.add(cbEquipment);

        row2.add(new JLabel("Rental Date:"));
        dpRentDate = makeDatePicker();
        row2.add(dpRentDate);

        row2.add(new JLabel("Return Date:"));
        dpReturnDate = makeDatePicker();
        row2.add(dpReturnDate);

        btnAddItem = new JButton("Add Item");
        row2.add(btnAddItem);
        top.add(row2);

        add(top, BorderLayout.NORTH);

        // Items table
        itemsModel = new DefaultTableModel(new Object[] {
                "Equipment ID", "Name", "Rental Date", "Return Date"
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return switch (c) {
                    case 0 -> Integer.class;
                    default -> String.class;
                };
            }
        };
        tblItems = new JTable(itemsModel);
        add(new JScrollPane(tblItems), BorderLayout.CENTER);

        // Bottom
        JPanel bottom = new JPanel(new BorderLayout());
        btnSave = new JButton("Save Rental");
        bottom.add(btnSave, BorderLayout.WEST);
        lblStatus = new JLabel(" ");
        bottom.add(lblStatus, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void attachHandlers() {
        btnAddItem.addActionListener(e -> addItem());
        btnSave.addActionListener(e -> saveRental());
    }

    private JXDatePicker makeDatePicker() {
        JXDatePicker dp = new JXDatePicker();
        dp.setFormats(new java.text.SimpleDateFormat("yyyy-MM-dd"));
        return dp;
    }

    private void setDefaults() {
        setPickerDate(dpRequestDate, LocalDate.now());
        setPickerDate(dpRentDate, LocalDate.now());
        setPickerDate(dpReturnDate, LocalDate.now().plusDays(1));
    }

    private void setPickerDate(JXDatePicker dp, LocalDate d) {
        ZonedDateTime zdt = d.atStartOfDay(ZoneId.systemDefault());
        dp.setDate(Date.from(zdt.toInstant()));
    }

    private LocalDate getPickerLocalDate(JXDatePicker dp) {
        Date val = dp.getDate();
        if (val == null) return null;
        return Instant.ofEpochMilli(val.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void loadCustomers() {
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT customer_id, CONCAT(first_name, ' ', last_name) AS name " +
                     "FROM customer WHERE is_active='Y' ORDER BY customer_id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cbCustomer.addItem(new Customer(rs.getInt(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            showError("Load customers failed: " + ex.getMessage());
        }
    }

    private void loadEquipment() {
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT equipment_id, name FROM equipment ORDER BY equipment_id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cbEquipment.addItem(new Equipment(rs.getInt(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            showError("Load equipment failed: " + ex.getMessage());
        }
    }

    private void addItem() {
        Equipment eq = (Equipment) cbEquipment.getSelectedItem();
        LocalDate rent = getPickerLocalDate(dpRentDate);
        LocalDate ret  = getPickerLocalDate(dpReturnDate);

        if (eq == null) { showError("Select equipment."); return; }
        if (rent == null || ret == null) { showError("Select rental and return dates."); return; }
        if (ret.isBefore(rent)) { showError("Return date must be on/after rental date."); return; }

        itemsModel.addRow(new Object[] {
                eq.getEquipmentId(),
                eq.getName(),
                ISO_FMT.format(rent),
                ISO_FMT.format(ret)
        });
        lblStatus.setText("Item added.");
    }

    private void saveRental() {
        Customer cust = (Customer) cbCustomer.getSelectedItem();
        LocalDate reqDate = getPickerLocalDate(dpRequestDate);

        if (cust == null) { showError("Select customer."); return; }
        if (reqDate == null) { showError("Select request date."); return; }
        if (itemsModel.getRowCount() == 0) { showError("Add at least one item."); return; }

        Connection cn = null;
        PreparedStatement psRental = null;
        PreparedStatement psItem = null;
        ResultSet rsKeys = null;

        try {
            cn = DB.getConnection();
            cn.setAutoCommit(false);

            // Insert rental
            psRental = cn.prepareStatement(
                    "INSERT INTO rental (request_date, customer_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            psRental.setDate(1, java.sql.Date.valueOf(reqDate));
            psRental.setInt(2, cust.getCustomerId());
            psRental.executeUpdate();

            rsKeys = psRental.getGeneratedKeys();
            if (!rsKeys.next()) throw new SQLException("No rental_id generated.");
            int rentalId = rsKeys.getInt(1);

            // Insert items (trigger fills daily_rate_snapshot; cost is generated)
            psItem = cn.prepareStatement(
                    "INSERT INTO rental_item (rental_id, equipment_id, rental_date, return_date, daily_rate_snapshot) " +
                    "VALUES (?, ?, ?, ?, 0.00)");

            for (int r = 0; r < itemsModel.getRowCount(); r++) {
                int eqId = (int) itemsModel.getValueAt(r, 0);
                LocalDate rentDate = LocalDate.parse((String) itemsModel.getValueAt(r, 2), ISO_FMT);
                LocalDate returnDate = LocalDate.parse((String) itemsModel.getValueAt(r, 3), ISO_FMT);

                psItem.setInt(1, rentalId);
                psItem.setInt(2, eqId);
                psItem.setDate(3, java.sql.Date.valueOf(rentDate));
                psItem.setDate(4, java.sql.Date.valueOf(returnDate));
                psItem.addBatch();
            }
            psItem.executeBatch();

            cn.commit();
            lblStatus.setText("Saved rental #" + rentalId);
            itemsModel.setRowCount(0);

        } catch (SQLException ex) {
            if (cn != null) try { cn.rollback(); } catch (SQLException ignored) {}
            showError("Save failed: " + ex.getMessage());
        } finally {
            closeQuietly(rsKeys);
            closeQuietly(psItem);
            closeQuietly(psRental);
            if (cn != null) {
                try { cn.setAutoCommit(true); } catch (SQLException ignored) {}
                closeQuietly(cn);
            }
        }
    }
    public void refreshList() {
    	// remove pre-existing drop down entries.
    	cbCustomer.removeAllItems();
    	cbEquipment.removeAllItems();
    	
    	// reload from the DB
    	loadCustomers();
    	loadEquipment();
    	
    	// reset date pickers.
    	setDefaults();
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void closeQuietly(AutoCloseable c) {
        if (c != null) { try { c.close(); } catch (Exception ignored) {} }
    }
}
