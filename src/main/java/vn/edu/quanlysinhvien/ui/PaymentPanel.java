package vn.edu.quanlysinhvien.ui;

import vn.edu.quanlysinhvien.db.Database;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

/** Records student payments. Requires the additive ThanhToan table migration. */
public class PaymentPanel extends JPanel {
    private final JComboBox<StudentOption> students = new JComboBox<>();
    private final JTextField amount = new JTextField(12), note = new JTextField(22), search = new JTextField(20);
    private final JComboBox<String> method = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"});
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Mã giao dịch", "Mã SV", "Họ tên", "Số tiền", "Ngày thanh toán", "Phương thức", "Ghi chú"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public PaymentPanel() {
        super(new BorderLayout(8, 8)); setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel wrapper = new JPanel(new BorderLayout(4, 4));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Sinh viên:")); form.add(students); form.add(new JLabel("Số tiền (VND):")); form.add(amount);
        form.add(new JLabel("Phương thức:")); form.add(method); form.add(new JLabel("Ghi chú:")); form.add(note);
        JButton save = new JButton("Ghi nhận thanh toán"); form.add(save); wrapper.add(form, BorderLayout.NORTH);
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT)); filter.add(new JLabel("Tìm theo mã/tên SV:")); filter.add(search);
        JButton find = new JButton("Tìm"), refresh = new JButton("Làm mới"); filter.add(find); filter.add(refresh); wrapper.add(filter, BorderLayout.SOUTH);
        add(wrapper, BorderLayout.NORTH);
        JTable table = new JTable(model); table.setAutoCreateRowSorter(true); add(new JScrollPane(table), BorderLayout.CENTER);
        save.addActionListener(e -> savePayment()); find.addActionListener(e -> loadPayments()); search.addActionListener(e -> loadPayments());
        refresh.addActionListener(e -> { search.setText(""); loadStudents(); loadPayments(); });
        loadStudents(); loadPayments();
    }

    private void loadStudents() {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT MaSV,HoTen FROM SinhVien ORDER BY MaSV"); ResultSet rs = ps.executeQuery()) {
            students.removeAllItems(); while (rs.next()) students.addItem(new StudentOption(rs.getString(1), rs.getString(2)));
        } catch (SQLException ex) { showError(ex); }
    }

    private void savePayment() {
        StudentOption student = (StudentOption) students.getSelectedItem();
        if (student == null) { JOptionPane.showMessageDialog(this, "Chưa có sinh viên để ghi nhận thanh toán."); return; }
        BigDecimal value;
        try { value = new BigDecimal(amount.getText().trim()); if (value.signum() <= 0 || value.scale() > 2) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0 và tối đa 2 chữ số thập phân."); return; }
        String sql = "INSERT INTO ThanhToan(MaSV,SoTien,PhuongThuc,GhiChu) VALUES(?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, student.id); ps.setBigDecimal(2, value); ps.setString(3, (String) method.getSelectedItem());
            String memo = note.getText().trim(); if (memo.isEmpty()) ps.setNull(4, Types.NVARCHAR); else ps.setString(4, memo);
            ps.executeUpdate(); amount.setText(""); note.setText(""); loadPayments();
            JOptionPane.showMessageDialog(this, "Đã ghi nhận khoản thanh toán.");
        } catch (SQLException ex) { showError(ex); }
    }

    private void loadPayments() {
        String sql = "SELECT tt.MaThanhToan,sv.MaSV,sv.HoTen,tt.SoTien,tt.NgayThanhToan,tt.PhuongThuc,tt.GhiChu FROM ThanhToan tt JOIN SinhVien sv ON sv.MaSV=tt.MaSV WHERE sv.MaSV LIKE ? OR sv.HoTen LIKE ? ORDER BY tt.NgayThanhToan DESC,tt.MaThanhToan DESC";
        String pattern = "%" + search.getText().trim() + "%";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                model.setRowCount(0);
                while (rs.next()) model.addRow(new Object[]{rs.getLong(1),rs.getString(2),rs.getString(3),rs.getBigDecimal(4),rs.getTimestamp(5),rs.getString(6),rs.getString(7)});
            }
        } catch (SQLException ex) { showError(ex); }
    }
    private void showError(Exception ex) { JOptionPane.showMessageDialog(this, "Không thao tác được dữ liệu thanh toán. Hãy chạy db/ThanhToan.sql trước.\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    private record StudentOption(String id, String name) { @Override public String toString() { return id + " - " + name; } }
}
