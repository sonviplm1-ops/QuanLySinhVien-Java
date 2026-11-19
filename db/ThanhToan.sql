/*
  Chạy sau script QuanLySinhVien.sql. Script này chỉ tạo bảng thanh toán,
  không xóa hoặc sửa dữ liệu hiện có.
*/
USE QuanLySinhVien;
GO
IF OBJECT_ID(N'dbo.ThanhToan', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ThanhToan (
        MaThanhToan BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_ThanhToan PRIMARY KEY,
        MaSV VARCHAR(15) NOT NULL,
        SoTien DECIMAL(18,2) NOT NULL CONSTRAINT CK_ThanhToan_SoTien CHECK (SoTien > 0),
        NgayThanhToan DATETIME2 NOT NULL CONSTRAINT DF_ThanhToan_Ngay DEFAULT SYSDATETIME(),
        PhuongThuc NVARCHAR(30) NOT NULL,
        GhiChu NVARCHAR(250) NULL,
        CONSTRAINT FK_ThanhToan_SinhVien FOREIGN KEY (MaSV) REFERENCES dbo.SinhVien(MaSV)
    );
    CREATE INDEX IX_ThanhToan_MaSV_Ngay ON dbo.ThanhToan(MaSV, NgayThanhToan DESC);
END;
GO
