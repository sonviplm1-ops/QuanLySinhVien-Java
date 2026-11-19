# Phần mềm Quản lý Sinh viên (Java Swing + SQL Server)

Ứng dụng đồ án desktop dùng Java 17, Maven, Swing và Microsoft JDBC Driver. Các màn hình gồm đăng nhập SQL Server; quản lý sinh viên (CRUD, tìm kiếm); tra cứu lớp; bảng điểm; đăng ký học phần; và ghi nhận/lọc lịch sử thanh toán.

## Yêu cầu
- JDK 17+ và Maven 3.8+
- SQL Server Express/Developer, TCP/IP và SQL Server Authentication được bật

## Cài đặt database
1. Chạy script `QuanLySinhVien (1).sql` bạn đã cung cấp trong SQL Server Management Studio. **Cảnh báo:** script gốc xóa database `QuanLySinhVien` nếu đã tồn tại rồi tạo lại, do đó sẽ xóa dữ liệu cũ. Hãy sao lưu trước hoặc chỉ chạy trên database mới.
2. Chạy `db/ThanhToan.sql` để thêm bảng ghi nhận thanh toán. Script bổ sung này không xóa dữ liệu hiện có.
3. Dùng tài khoản SQL Server có quyền truy cập database (đọc/ghi các bảng ứng dụng) để đăng nhập. Tài khoản không được lưu vào source.

## Cấu hình kết nối
Đăng nhập xác thực bằng username/password SQL Server. URL máy chủ lấy từ biến môi trường `DB_URL`; mặc định:
`jdbc:sqlserver://localhost:1433;databaseName=QuanLySinhVien;encrypt=true;trustServerCertificate=true`

PowerShell, nếu server/port khác mặc định:
```powershell
$env:DB_URL="jdbc:sqlserver://localhost:1433;databaseName=QuanLySinhVien;encrypt=true;trustServerCertificate=true"
```

## Chạy
```bash
mvn clean compile exec:java
```
Đóng gói bằng `mvn clean package`; import `pom.xml` vào IDE và chạy `vn.edu.quanlysinhvien.Main` cũng được.

## Chức năng
- **Đăng nhập:** kiểm tra thông tin đăng nhập trực tiếp với SQL Server trước khi mở các màn hình.
- **Sinh viên:** danh sách, tìm theo mã/tên, thêm/sửa/xóa; chọn lớp có sẵn; kiểm tra dữ liệu bắt buộc và ngày.
- **Lớp học, bảng điểm:** tra cứu và lọc dữ liệu từ database/view.
- **Đăng ký học phần:** chọn sinh viên và lớp học phần, lưu vào `DangKyHocPhan`, ngăn đăng ký trùng nhờ khóa chính và kiểm tra sĩ số tối đa.
- **Thanh toán:** ghi nhận số tiền, phương thức, ghi chú và thời gian; xem/lọc lịch sử theo mã hoặc tên sinh viên. Đây là sổ ghi nhận khoản thu, không tự tính học phí phải nộp vì schema gốc không có học phí/tín chỉ thu.

## Cấu trúc
```
db/ThanhToan.sql
src/main/java/vn/edu/quanlysinhvien/
  Main.java
  db/Database.java
  model/Student.java
  dao/StudentDao.java
  ui/LoginDialog.java
  ui/MainFrame.java
  ui/StudentPanel.java
  ui/StudentFormDialog.java
  ui/ReportPanel.java
  ui/EnrollmentPanel.java
  ui/PaymentPanel.java
```
