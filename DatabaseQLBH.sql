CREATE DATABASE QLBH
USE QLBH 

-- Bảng quản lý nhân viên
CREATE TABLE NhanVien (
    MaNV INT IDENTITY(1,1) PRIMARY KEY,
    TenTaiKhoan NVARCHAR(50) UNIQUE NOT NULL,
    MatKhau NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100) NULL,
    VaiTro NVARCHAR(20) CHECK (VaiTro IN (N'Quản lý', N'Nhân viên')) NOT NULL
);

-- Bảng nhà cung cấp
CREATE TABLE NhaCungCap (
    MaNCC INT IDENTITY(1,1) PRIMARY KEY,
    TenNCC NVARCHAR(100) NOT NULL,
    DiaChi NVARCHAR(255) NOT NULL,
    SoDienThoai NVARCHAR(15) NOT NULL
);

-- Bảng nguyên liệu, liên kết với nhà cung cấp
CREATE TABLE NguyenLieu (
    MaNL INT IDENTITY(1,1) PRIMARY KEY,
    TenNL NVARCHAR(100) NOT NULL,
    MaNCC INT NOT NULL,
    NgayNhap DATE NOT NULL,
    HanSuDung DATE NOT NULL,
    SoLuongTon INT CHECK (SoLuongTon >= 0) NOT NULL,
    DonViTinh NVARCHAR(20) NOT NULL,
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC)
);

-- Bảng khách hàng
CREATE TABLE KhachHang (
    MaKH INT IDENTITY(1,1) PRIMARY KEY,
    TenKH NVARCHAR(100) NOT NULL,
    SoDienThoai NVARCHAR(15) NOT NULL,
    DiaChi NVARCHAR(255) NOT NULL
);

-- Bảng sản phẩm
CREATE TABLE SanPham (
    MaSP INT IDENTITY(1,1) PRIMARY KEY,
    TenSP NVARCHAR(100) NOT NULL,
    Gia DECIMAL(18,2) NOT NULL,
    SoLuongTon INT CHECK (SoLuongTon >= 0) NOT NULL
);

-- Bảng liên kết sản phẩm với nguyên liệu (sản phẩm có thể được tạo thành từ nhiều nguyên liệu)
CREATE TABLE SanPham_NguyenLieu (
    MaSP INT NOT NULL,
    MaNL INT NOT NULL,
    SoLuongCan INT CHECK (SoLuongCan > 0) NOT NULL,
    PRIMARY KEY (MaSP, MaNL),
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP),
    FOREIGN KEY (MaNL) REFERENCES NguyenLieu(MaNL)
);

-- Bảng đơn hàng
CREATE TABLE DonHang (
    MaDH INT IDENTITY(1,1) PRIMARY KEY,
    MaKH INT NOT NULL,
    NgayDatHang DATE NOT NULL,
    TrangThai NVARCHAR(20) CHECK (TrangThai IN (N'Chờ xử lý', N'Đang giao', N'Đã giao')) NOT NULL,
    CongNo DECIMAL(18,2) DEFAULT 0 NOT NULL,
    NgayThanhToan DATE NULL,
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH)
);

-- Bảng chi tiết đơn hàng (1 đơn hàng có thể chứa nhiều sản phẩm)
CREATE TABLE ChiTietDonHang (
    MaDH INT NOT NULL,
    MaSP INT NOT NULL,
    SoLuong INT CHECK (SoLuong > 0) NOT NULL,
    GiaBan DECIMAL(18,2) NOT NULL,
    PRIMARY KEY (MaDH, MaSP),
    FOREIGN KEY (MaDH) REFERENCES DonHang(MaDH),
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
);

-- Bảng hóa đơn
CREATE TABLE HoaDon (
    MaHD INT IDENTITY(1,1) PRIMARY KEY,
    MaDH INT NOT NULL,
    NgayLapHD DATE NOT NULL,
    SoTienThanhToan DECIMAL(18,2) NOT NULL,
    MaNV INT NOT NULL,
    FOREIGN KEY (MaDH) REFERENCES DonHang(MaDH),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);

-- Bảng chi tiết hóa đơn (1 hóa đơn có thể chứa nhiều sản phẩm)
CREATE TABLE ChiTietHoaDon (
    MaHD INT NOT NULL,
    MaSP INT NOT NULL,
    SoLuong INT CHECK (SoLuong > 0) NOT NULL,
    PRIMARY KEY (MaHD, MaSP),
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD),
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
);

-- Bảng giao hàng
CREATE TABLE GiaoHang (
    MaDH INT PRIMARY KEY,
    MaNV INT NOT NULL,
    ThoiGianGiao DATE NOT NULL,
    TrangThai NVARCHAR(20) CHECK (TrangThai IN (N'Đã giao', N'Chưa giao', N'Giao thiếu')) NOT NULL,
    GhiChu NVARCHAR(255) NULL,
    FOREIGN KEY (MaDH) REFERENCES DonHang(MaDH),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);

-- Trigger tự động giảm số lượng tồn kho của sản phẩm khi đơn hàng đã giao được cập nhật
CREATE TRIGGER trg_CapNhatSoLuongTonKho
ON GiaoHang
AFTER UPDATE
AS
BEGIN
    IF UPDATE(TrangThai)
    BEGIN
        UPDATE sp
        SET sp.SoLuongTon = sp.SoLuongTon - ctdh.SoLuong
        FROM SanPham sp
        INNER JOIN ChiTietDonHang ctdh ON sp.MaSP = ctdh.MaSP
        INNER JOIN DonHang dh ON ctdh.MaDH = dh.MaDH
        INNER JOIN GiaoHang gh ON dh.MaDH = gh.MaDH
        WHERE gh.TrangThai = N'Đã giao';
    END
END;


-- Dữ liệu mẫu cho bảng NhanVien
INSERT INTO NhanVien (TenTaiKhoan, MatKhau, Email, VaiTro)
VALUES 
    ('hung', '123', '', N'Quản lý'),
    ('kha', '123', '', N'Quản lý'),
    ('trung', '123', '', N'Quản lý'),
    ('hai', '123', '', N'Quản lý');

-- Dữ liệu mẫu cho bảng NhaCungCap
INSERT INTO NhaCungCap (TenNCC, DiaChi, SoDienThoai)
VALUES 
    (N'Công ty A', N'Hà Nội', '0123456789'),
    (N'Công ty B', N'TP.HCM', '0987654321'),
    (N'Công ty C', N'Đà Nẵng', '0345678901'),
    (N'Công ty D', N'Hải Phòng', '0765432109'),
    (N'Công ty E', N'Cần Thơ', '0912345678');

-- Dữ liệu mẫu cho bảng NguyenLieu
INSERT INTO NguyenLieu (TenNL, MaNCC, NgayNhap, HanSuDung, SoLuongTon, DonViTinh)
VALUES 
    (N'Bột mì', 1, '2025-01-01', '2025-06-01', 100, 'kg'),
    (N'Đường', 2, '2025-02-01', '2025-07-01', 80, 'kg'),
    (N'Muối', 3, '2025-03-01', '2025-08-01', 60, 'kg'),
    (N'Bơ', 4, '2025-04-01', '2025-09-01', 50, 'kg'),
    (N'Sữa', 5, '2025-05-01', '2025-10-01', 90, N'lít'),
    (N'Trứng', 1, '2025-01-10', '2025-06-10', 200, N'quả'),
    (N'Bột cacao', 2, '2025-02-10', '2025-07-10', 70, 'kg');

-- Dữ liệu mẫu cho bảng KhachHang
INSERT INTO KhachHang (TenKH, SoDienThoai, DiaChi)
VALUES 
    (N'Nguyễn Văn A', '0901122334', N'Hà Nội'),
    (N'Trần Thị B', '0911223344', N'Hải Phòng'),
    (N'Lê Văn C', '0922334455', N'TP.HCM'),
    (N'Phạm Thị D', '0933445566', N'Đà Nẵng'),
    (N'Hoàng Văn E', '0944556677', N'Cần Thơ'),
    (N'Bùi Thị F', '0955667788', N'Bình Dương'),
    (N'Đặng Văn G', '0966778899', N'Nha Trang');

-- Dữ liệu mẫu cho bảng SanPham
INSERT INTO SanPham (TenSP, Gia, SoLuongTon)
VALUES 
    (N'Bánh mì', 15000, 50),
    (N'Bánh quy', 20000, 40),
    (N'Bánh bông lan', 25000, 30),
    (N'Bánh kem', 100000, 20),
    (N'Sữa chua', 10000, 60),
    (N'Trà sữa', 35000, 50),
    (N'Cacao nóng', 40000, 45);

-- Dữ liệu mẫu cho bảng SanPham_NguyenLieu
INSERT INTO SanPham_NguyenLieu (MaSP, MaNL, SoLuongCan)
VALUES 
    (1, 1, 1),  -- Bánh mì cần Bột mì
    (2, 2, 2),  -- Bánh quy cần Đường
    (3, 1, 1),  -- Bánh bông lan cần Bột mì
    (3, 6, 1),  -- Bánh bông lan cần Trứng
    (4, 1, 2),  -- Bánh kem cần Bột mì
    (4, 4, 1),  -- Bánh kem cần Bơ
    (5, 5, 2);  -- Sữa chua cần Sữa

-- Dữ liệu mẫu cho bảng DonHang
INSERT INTO DonHang (MaKH, NgayDatHang, TrangThai, CongNo, NgayThanhToan)
VALUES 
    (1, '2025-03-01', N'Chờ xử lý', 0, NULL),
    (2, '2025-03-02', N'Đang giao', 50000, NULL),
    (3, '2025-03-03', N'Đã giao', 0, '2025-03-04'),
    (4, '2025-03-04', N'Chờ xử lý', 0, NULL),
    (5, '2025-03-05', N'Đã giao', 0, '2025-03-06'),
    (6, '2025-03-06', N'Đang giao', 70000, NULL),
    (7, '2025-03-07', N'Chờ xử lý', 0, NULL);

-- Dữ liệu mẫu cho bảng ChiTietDonHang
INSERT INTO ChiTietDonHang (MaDH, MaSP, SoLuong, GiaBan)
VALUES 
    (1, 1, 2, 15000),
    (1, 2, 1, 20000),
    (2, 3, 3, 25000),
    (2, 4, 1, 100000),
    (3, 5, 4, 10000),
    (4, 6, 2, 35000),
    (5, 7, 3, 40000);

-- Dữ liệu mẫu cho bảng HoaDon
INSERT INTO HoaDon (MaDH, NgayLapHD, SoTienThanhToan, MaNV)
VALUES 
    (3, '2025-03-04', 100000, 1),
    (5, '2025-03-06', 120000, 2);

-- Dữ liệu mẫu cho bảng ChiTietHoaDon
INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong)
VALUES 
    (1, 5, 4),
    (2, 7, 3);

-- Dữ liệu mẫu cho bảng GiaoHang
INSERT INTO GiaoHang (MaDH, MaNV, ThoiGianGiao, TrangThai, GhiChu)
VALUES 
    (2, 3, '2025-03-03', N'Chưa giao', N'Khách yêu cầu giao buổi chiều'),
    (3, 2, '2025-03-04', N'Đã giao', N'Giao đúng giờ'),
    (5, 4, '2025-03-06', N'Đã giao', N'Khách hài lòng');

