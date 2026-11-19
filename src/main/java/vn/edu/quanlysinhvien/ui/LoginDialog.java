package vn.edu.quanlysinhvien.ui;

import vn.edu.quanlysinhvien.db.Database;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

/** Login validates SQL Server credentials before opening the application. */
public class LoginDialog extends JDialog {
    private final JTextField username = new JTextField(System.getenv().getOrDefault("DB_USER", ""), 22);
    private final JPasswordField password = new JPasswordField(22);
    private boolean authenticated;

    public LoginDialog(Window owner) {
        super(owner, "Đăng nhập hệ thống", ModalityType.APPLICATION_MODAL);
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        addRow(fields, 0, "Tài khoản SQL Server", username);
        addRow(fields, 1, "Mật khẩu", password);
        JLabel hint = new JLabel("Đăng nhập bằng tài khoản SQL Server được cấp quyền vào database.");
        hint.setBorder(BorderFactory.createEmptyBorder(0, 24, 12, 24));
        JButton login = new JButton("Đăng nhập"), cancel = new JButton("Thoát");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT)); buttons.add(cancel); buttons.add(login);
        add(fields, BorderLayout.CENTER); add(hint, BorderLayout.NORTH); add(buttons, BorderLayout.SOUTH);
        login.addActionListener(e -> login());
        password.addActionListener(e -> login());
        cancel.addActionListener(e -> dispose());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); pack(); setResizable(false); setLocationRelativeTo(owner);
    }

    private void login() {
        String user = username.getText().trim();
        char[] chars = password.getPassword();
        if (user.isBlank() || chars.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu."); return;
        }
        String secret = new String(chars);
        Arrays.fill(chars, '\0');
        try {
            Database.authenticate(user, secret);
            authenticated = true;
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Đăng nhập SQL Server thất bại. Kiểm tra tài khoản, mật khẩu, DB_URL và quyền truy cập.\n" + ex.getMessage(), "Không thể đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addRow(JPanel panel, int row, String label, Component field) {
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(6, 6, 6, 6); c.gridy = row;
        c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel(label), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; panel.add(field, c);
    }
    public boolean isAuthenticated() { return authenticated; }
}
