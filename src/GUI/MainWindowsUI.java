
package GUI;

import javax.swing.*;
import java.awt.*;

public class MainWindowsUI {

    private JFrame frame;
    private JTabbedPane tabs;

    private CustomerManagementPanel customerPanel;
    private RentalPanel rentalPanel;
    private EquipmentManagementPanel equipmentPanel;

    public MainWindowsUI() {
        buildUI();
    }

    private void buildUI() {
        frame = new JFrame("VRMS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        tabs = new JTabbedPane(JTabbedPane.TOP);

        // Create panels
        customerPanel  = new CustomerManagementPanel();
        rentalPanel    = new RentalPanel();
        equipmentPanel = new EquipmentManagementPanel();

        // Attach panels as tabs
        tabs.addTab("Customers", null, customerPanel, "Search/Add/Modify/Delete customers");
        tabs.addTab("Rental",    null, rentalPanel,    "Create rentals and add items");
        tabs.addTab("Equipment", null, equipmentPanel, "Manage equipment inventory");
        
        // Add ChangeListener so It will detect user gets to rental page and stay on top of the latest DB.
        tabs.addChangeListener(e ->{
        	int index = tabs.getSelectedIndex();
        	String title = tabs.getTitleAt(index);
        	if(title.equals("Rental")) {
        		rentalPanel.refreshList();
        	}
        });

        frame.setJMenuBar(buildMenuBar()); // optional: global app menu
        frame.add(tabs, BorderLayout.CENTER);
        frame.setSize(1100, 700);
        frame.setLocationByPlatform(true);
    }

    // Simple app menu (optional)
    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu mApp = new JMenu("VRMS");
        JMenuItem miAbout = new JMenuItem("About...");
        miAbout.addActionListener(e ->
            JOptionPane.showMessageDialog(frame,
                "VRMS Developed by Stephen Noh for the final project of CPSY200A-Fall 2025",
                "About", JOptionPane.INFORMATION_MESSAGE)
        );
        mApp.add(miAbout);
        bar.add(mApp);
        return bar;
    }

    public void show() {
        frame.setVisible(true);
    }
}