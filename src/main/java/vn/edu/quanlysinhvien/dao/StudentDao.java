package vn.edu.quanlysinhvien.dao;

import vn.edu.quanlysinhvien.db.Database;
import vn.edu.quanlysinhvien.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private static final String COLUMNS = "MaSV, HoTen, NgaySinh, GioiTinh, DiaChi, SoDienThoai, Email, MaLop, TrangThai";

    public List<Student> find(String keyword) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM SinhVien WHERE MaSV LIKE ? OR HoTen LIKE ? ORDER BY MaSV";
        String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> result = new ArrayList<>();
                while (rs.next()) {
                    Date date = rs.getDate("NgaySinh");
                    result.add(new Student(rs.getString("MaSV"), rs.getString("HoTen"), date == null ? null : date.toLocalDate(),
                            rs.getString("GioiTinh"), rs.getString("DiaChi"), rs.getString("SoDienThoai"),
                            rs.getString("Email"), rs.getString("MaLop"), rs.getString("TrangThai")));
                }
                return result;
            }
        }
    }

    public void insert(Student s) throws SQLException {
        String sql = "INSERT INTO SinhVien (MaSV,HoTen,NgaySinh,GioiTinh,DiaChi,SoDienThoai,Email,MaLop,TrangThai) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, s); ps.executeUpdate();
        }
    }

    public void update(Student s) throws SQLException {
        String sql = "UPDATE SinhVien SET HoTen=?,NgaySinh=?,GioiTinh=?,DiaChi=?,SoDienThoai=?,Email=?,MaLop=?,TrangThai=? WHERE MaSV=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.name());
            if (s.birthDate() == null) ps.setNull(2, Types.DATE); else ps.setDate(2, Date.valueOf(s.birthDate()));
            ps.setString(3, s.gender()); ps.setString(4, s.address()); ps.setString(5, s.phone()); ps.setString(6, s.email());
            ps.setString(7, s.classId()); ps.setString(8, s.status()); ps.setString(9, s.id());
            if (ps.executeUpdate() == 0) throw new SQLException("Không tìm thấy sinh viên cần cập nhật.");
        }
    }

    public void delete(String id) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM SinhVien WHERE MaSV=?")) {
            ps.setString(1, id);
            if (ps.executeUpdate() == 0) throw new SQLException("Không tìm thấy sinh viên cần xóa.");
        }
    }

    public List<String> findClassIds() throws SQLException {
        List<String> ids = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT MaLop FROM Lop ORDER BY MaLop"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ids.add(rs.getString(1));
        }
        return ids;
    }

    private void bind(PreparedStatement ps, Student s) throws SQLException {
        ps.setString(1, s.id()); ps.setString(2, s.name());
        if (s.birthDate() == null) ps.setNull(3, Types.DATE); else ps.setDate(3, Date.valueOf(s.birthDate()));
        ps.setString(4, s.gender()); ps.setString(5, s.address()); ps.setString(6, s.phone());
        ps.setString(7, s.email()); ps.setString(8, s.classId()); ps.setString(9, s.status());
    }
}
