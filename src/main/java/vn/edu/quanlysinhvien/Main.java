package vn.edu.quanlysinhvien;

import vn.edu.quanlysinhvien.ui.MainFrame;
import javax.swing.*;
import java.awt.*;

public final class Main {
    private Main() {}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
