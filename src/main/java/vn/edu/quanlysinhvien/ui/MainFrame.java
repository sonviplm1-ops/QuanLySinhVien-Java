package vn.edu.quanlysinhvien.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Quản lý sinh viên - SQL Server");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Sinh viên", new StudentPanel());
        tabs.addTab("Lớp học", new ReportPanel(
                "SELECT l.MaLop AS [Mã lớp], l.TenLop AS [Tên lớp], k.TenKhoa AS [Khoa], l.NamNhapHoc AS [Năm nhập học] " +
                "FROM Lop l JOIN Khoa k ON k.MaKhoa=l.MaKhoa ORDER BY l.MaLop", "Mã lớp / tên lớp / khoa"));
        tabs.addTab("Bảng điểm", new ReportPanel(
                "SELECT MaSV AS [Mã sinh viên], HoTen AS [Họ tên], TenMH AS [Môn học], SoTinChi AS [Tín chỉ], HocKy AS [Học kỳ], NamHoc AS [Năm học], DiemQT AS [Điểm quá trình], DiemThi AS [Điểm thi], DiemTK AS [Điểm tổng kết], DiemChu AS [Điểm chữ] FROM dbo.vw_BangDiem ORDER BY MaSV, TenMH",
                "Mã sinh viên / họ tên / môn học"));
        tabs.addTab("Đăng ký học phần", new EnrollmentPanel());
        tabs.addTab("Thanh toán", new PaymentPanel());
        add(tabs, BorderLayout.CENTER);
        JLabel status = new JLabel("  Java Swing  |  SQL Server  |  Quản lý sinh viên");
        status.setBorder(BorderFactory.createEmptyBorder(7, 4, 7, 4)); add(status, BorderLayout.SOUTH); pack();
    }
}
