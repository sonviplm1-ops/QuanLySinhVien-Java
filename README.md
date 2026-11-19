# Phần mềm Quản lý Sinh viên (Java Swing + SQL Server)

Ứng dụng đồ án desktop viết bằng Java 17, Maven và Swing. Giao diện có 3 màn hình chính: **Sinh viên** (thêm/sửa/xóa/tìm kiếm), **Lớp học** (tra cứu lớp/khoa) và **Bảng điểm** (tra cứu điểm, điểm tổng kết và điểm chữ). Kết nối SQL Server qua JDBC, truy vấn có tham số.

## Yêu cầu
- JDK 17+
- Maven 3.8+
- SQL Server (Express hoặc Developer) và SQL Server Authentication đã bật

## 1. Tạo cơ sở dữ liệu
Chạy file SQL quản lý sinh viên bạn đã cung cấp (`QuanLySinhVien (1).sql`) trong SQL Server Management Studio. Lưu ý: script đó **xóa database `QuanLySinhVien` nếu đã tồn tại rồi tạo lại**, vì vậy sẽ xóa dữ liệu cũ. Chỉ chạy khi đã sao lưu hoặc chấp nhận tạo mới.

Cơ sở dữ liệu cần có các bảng `SinhVien`, `Lop`, `Khoa`, `LopHocPhan`, `MonHoc`, `Diem` và view `vw_BangDiem` theo đúng schema đã gửi.

## 2. Cấu hình kết nối
Ứng dụng đọc cấu hình từ biến môi trường (không lưu mật khẩu trong source):

| Biến | Mặc định | Ví dụ |
|---|---|---|
| `DB_URL` | `jdbc:sqlserver://localhost:1433;databaseName=QuanLySinhVien;encrypt=true;trustServerCertificate=true` | thay bằng URL máy chủ SQL của bạn |
| `DB_USER` | `sa` | tài khoản SQL Server |
| `DB_PASSWORD` | rỗng | mật khẩu tài khoản |

Windows PowerShell (đặt trước khi chạy):
```powershell
$env:DB_URL="jdbc:sqlserver://localhost:1433;databaseName=QuanLySinhVien;encrypt=true;trustServerCertificate=true"
$env:DB_USER="sa"
$env:DB_PASSWORD="your-password"
```

Nếu SQL Server dùng instance name/port khác, cập nhật `DB_URL` theo cấu hình máy của bạn. Cần bật TCP/IP và cho phép kết nối SQL Server Authentication.

## 3. Chạy ứng dụng
```bash
mvn clean compile exec:java
```

Hoặc đóng gói:
```bash
mvn clean package
```

Maven tự tải Microsoft JDBC Driver. Chạy bằng IDE: import project dạng Maven rồi chạy `vn.edu.quanlysinhvien.Main`.

## Chức năng
- **Sinh viên:** danh sách đầy đủ; tìm theo mã/tên; thêm, cập nhật, xóa; kiểm tra trường bắt buộc và định dạng ngày; chọn lớp từ dữ liệu SQL.
- **Lớp học:** tra cứu mã lớp, tên lớp, khoa và năm nhập học.
- **Bảng điểm:** tra cứu điểm quá trình, điểm thi, điểm tổng kết và điểm chữ qua view `vw_BangDiem`.
- Lỗi kết nối, dữ liệu trùng mã, ràng buộc khóa ngoại được thông báo trên giao diện.

## Cấu trúc
```
src/main/java/vn/edu/quanlysinhvien/
  Main.java
  db/Database.java
  model/Student.java
  dao/StudentDao.java
  ui/MainFrame.java
  ui/StudentPanel.java
  ui/StudentFormDialog.java
  ui/ReportPanel.java
```
