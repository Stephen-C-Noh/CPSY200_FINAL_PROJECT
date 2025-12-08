
package GUI;

import javax.swing.*;

public class AppDriver {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainWindowsUI ui = new MainWindowsUI();
            ui.show();
        });
    }
}
