package vn.edu.quanlysinhvien;

import vn.edu.quanlysinhvien.ui.LoginDialog;
import vn.edu.quanlysinhvien.ui.MainFrame;
import javax.swing.*;

public final class Main {
    private Main() { }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);
            if (login.isAuthenticated()) new MainFrame().setVisible(true);
        });
    }
}
