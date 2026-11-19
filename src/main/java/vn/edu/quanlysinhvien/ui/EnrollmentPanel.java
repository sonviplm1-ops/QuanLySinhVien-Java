package vn.edu.quanlysinhvien.ui;

import vn.edu.quanlysinhvien.db.Database;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Enrolls students in course sections and enforces the configured section capacity. */
public class EnrollmentPanel extends JPanel {
    private final JComboBox<StudentOption> students = new JComboBox<>();
    private final JComboBox<SectionOption> sections = new JComboBox<>();
    private final JTextField search = new JTextField(22);
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Mã SV", "Họ tên", "Mã LHP", "Môn học", "Học kỳ", "Năm học", "Ngày đăng ký"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public EnrollmentPanel() {
        super(new BorderLayout(8, 8)); setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Sinh viên:")); top.add(students); top.add(new JLabel("Lớp học phần:")); top.add(sections);
        JButton enroll = new JButton("Đăng ký học phần"); top.add(enroll); add(top, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(4, 4));
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT)); filter.add(new JLabel("Lọc theo mã/tên SV:")); filter.add(search);
        JButton find = new JButton("Tìm"), refresh = new JButton("Làm mới"); filter.add(find); filter.add(refresh); center.add(filter, BorderLayout.NORTH);
        table.setAutoCreateRowSorter(true); center.add(new JScrollPane(table), BorderLayout.CENTER); add(center, BorderLayout.CENTER);
        enroll.addActionListener(e -> enroll()); find.addActionListener(e -> loadRegistrations()); search.addActionListener(e -> loadRegistrations());
        refresh.addActionListener(e -> { search.setText(""); loadOptions(); loadRegistrations(); });
        loadOptions(); loadRegistrations();
    }

    private void loadOptions() {
        try (Connection c = Database.getConnection()) {
            students.removeAllItems();
            try (PreparedStatement ps = c.prepareStatement("SELECT MaSV,HoTen FROM SinhVien ORDER BY MaSV"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) students.addItem(new StudentOption(rs.getString(1), rs.getString(2)));
            }
            sections.removeAllItems();
            String sql = "SELECT lhp.MaLHP,mh.TenMH,lhp.HocKy,lhp.NamHoc,COALESCE(lhp.SiSoToiDa,50) AS SucChua,(SELECT COUNT(*) FROM DangKyHocPhan dk WHERE dk.MaLHP=lhp.MaLHP) AS DaDangKy FROM LopHocPhan lhp JOIN MonHoc mh ON mh.MaMH=lhp.MaMH ORDER BY lhp.MaLHP";
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) sections.addItem(new SectionOption(rs.getString("MaLHP"), rs.getString("TenMH"), rs.getInt("HocKy"), rs.getString("NamHoc"), rs.getInt("SucChua"), rs.getInt("DaDangKy")));
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void enroll() {
        StudentOption student = (StudentOption) students.getSelectedItem();
        SectionOption section = (SectionOption) sections.getSelectedItem();
        if (student == null || section == null) { JOptionPane.showMessageDialog(this, "Cần có sinh viên và lớp học phần trước khi đăng ký."); return; }
        String sql = "SELECT COUNT(*) FROM DangKyHocPhan WITH (UPDLOCK,HOLDLOCK) WHERE MaLHP=?";
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                int current;
                try (PreparedStatement count = c.prepareStatement(sql)) { count.setString(1, section.id); try (ResultSet rs = count.executeQuery()) { rs.next(); current = rs.getInt(1); } }
                if (current >= section.capacity) throw new SQLException("Lớp học phần đã đủ sĩ số.");
                try (PreparedStatement insert = c.prepareStatement("INSERT INTO DangKyHocPhan(MaSV,MaLHP,NgayDangKy) VALUES(?,?,CAST(GETDATE() AS DATE))")) {
                    insert.setString(1, student.id); insert.setString(2, section.id); insert.executeUpdate();
                }
                c.commit(); loadOptions(); loadRegistrations();
                JOptionPane.showMessageDialog(this, "Đăng ký học phần thành công.");
            } catch (SQLException ex) { c.rollback(); throw ex; }
            finally { c.setAutoCommit(true); }
        } catch (SQLException ex) { showError(ex); }
    }

    private void loadRegistrations() {
        String sql = "SELECT sv.MaSV,sv.HoTen,lhp.MaLHP,mh.TenMH,lhp.HocKy,lhp.NamHoc,dk.NgayDangKy FROM DangKyHocPhan dk JOIN SinhVien sv ON sv.MaSV=dk.MaSV JOIN LopHocPhan lhp ON lhp.MaLHP=dk.MaLHP JOIN MonHoc mh ON mh.MaMH=lhp.MaMH WHERE sv.MaSV LIKE ? OR sv.HoTen LIKE ? ORDER BY sv.MaSV,lhp.MaLHP";
        String pattern = "%" + search.getText().trim() + "%";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                model.setRowCount(0);
                while (rs.next()) model.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getInt(5),rs.getString(6),rs.getDate(7)});
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void showError(Exception ex) { JOptionPane.showMessageDialog(this, "Không thể hoàn tất đăng ký. Có thể sinh viên đã đăng ký lớp này hoặc dữ liệu bị ràng buộc.\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    private record StudentOption(String id, String name) { @Override public String toString() { return id + " - " + name; } }
    private record SectionOption(String id, String subject, int semester, String year, int capacity, int enrolled) {
        @Override public String toString() { return id + " - " + subject + " (HK" + semester + ", " + year + "; " + enrolled + "/" + capacity + ")"; }
    }
}
