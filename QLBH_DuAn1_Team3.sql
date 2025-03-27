CREATE DATABASE QLBH
USE QLBH 

-- Bảng quản lý nhân viên
CREATE TABLE NhanVien (
    MaNV INT IDENTITY(1,1) PRIMARY KEY,
    TenTaiKhoan NVARCHAR(50) UNIQUE NOT NULL,
    MatKhau NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100) NULL,
    HoTen NVARCHAR(50) NOT NULL,
    VaiTro BIT NOT NULL -- 1: Quản lý, 0: Nhân viên
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

-- Bảng hóa đơn (gộp với đơn hàng)
CREATE TABLE HoaDon (
    MaHD INT PRIMARY KEY,
    MaKH INT NOT NULL,
    NgayDatHang DATE NOT NULL,
    TrangThai NVARCHAR(20) CHECK (TrangThai IN (N'Chờ xử lý', N'Đang giao', N'Đã giao')) NOT NULL,
    CongNo DECIMAL(18,2) DEFAULT 0 NOT NULL,
    NgayThanhToan DATE NULL,
    SoTienThanhToan DECIMAL(18,2) NOT NULL,
    MaNV INT NOT NULL,
    SoDienThoai NVARCHAR(15) NOT NULL,
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);

-- Bảng chi tiết hóa đơn (gộp với chi tiết đơn hàng)
CREATE TABLE ChiTietHoaDon (
    MaHD INT NOT NULL,
    MaNL INT NOT NULL,
    SoLuong INT CHECK (SoLuong > 0) NOT NULL,
    GiaBan DECIMAL(18,2) NOT NULL,
    PRIMARY KEY (MaHD, MaNL),
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD),
    FOREIGN KEY (MaNL) REFERENCES NguyenLieu(MaNL)
);




-- DATA 
-- Dữ liệu mẫu cho bảng NhanVien
INSERT INTO NhanVien (TenTaiKhoan, MatKhau, Email, HoTen, VaiTro) VALUES 
    ('hung', '123', '', N'Hùng', 1),
    ('kha', '123', '', N'Kha', 1),
    ('trung', '123', '', N'Trung', 1),
    ('hai', '123', '', N'Hải', 1);

-- Dữ liệu mẫu cho bảng NhaCungCap
INSERT INTO NhaCungCap (TenNCC, DiaChi, SoDienThoai) VALUES 
    (N'Công ty A', N'Hà Nội', '0123456789'),
    (N'Công ty B', N'TP.HCM', '0987654321'),
    (N'Công ty C', N'Đà Nẵng', '0345678901'),
	(N'Công ty D', N'Bình Dương', '0367890123'),
    (N'Công ty E', N'Cần Thơ', '0378901234'),
    (N'Công ty F', N'Quảng Ninh', '0389012345');

-- Dữ liệu mẫu cho bảng NguyenLieu
INSERT INTO NguyenLieu (TenNL, MaNCC, NgayNhap, HanSuDung, SoLuongTon, DonViTinh) VALUES 
    (N'Bột mì', 1, '2025-01-01', '2025-06-01', 100, 'kg'),
    (N'Đường', 2, '2025-02-01', '2025-07-01', 80, 'kg'),
    (N'Muối', 3, '2025-03-01', '2025-08-01', 60, 'kg'),
	(N'Sữa tươi', 1, '2025-02-05', '2025-06-30', 50, 'lit'),
    (N'Trứng gà', 2, '2025-03-10', '2025-04-10', 200, N'quả'),
    (N'Bơ', 3, '2025-01-15', '2025-07-15', 40, 'kg'),
    (N'Socola', 4, '2025-02-25', '2025-08-25', 30, 'kg'),
    (N'Kem tươi', 5, '2025-03-01', '2025-06-01', 20, 'lit'),
    (N'Nước ép trái cây', 6, '2025-03-05', '2025-06-05', 60, 'chai');

-- Dữ liệu mẫu cho bảng KhachHang
INSERT INTO KhachHang (TenKH, SoDienThoai, DiaChi) VALUES 
    (N'Nguyễn Văn A', '0901122334', N'Hà Nội'),
    (N'Trần Thị B', '0911223344', N'Hải Phòng'),
    (N'Lê Văn C', '0922334455', N'TP.HCM'),
	(N'Phạm Thị D', '0933445566', N'Bắc Ninh'),
    (N'Ngô Văn E', '0944556677', N'Nghệ An'),
    (N'Đặng Thị F', '0955667788', N'Huế'),
    (N'Hồ Văn G', '0966778899', N'Đồng Nai'),
    (N'Vũ Thị H', '0977889900', N'An Giang');


-- Dữ liệu mẫu cho bảng HoaDon
INSERT INTO HoaDon (MaHD, MaKH, NgayDatHang, TrangThai, CongNo, NgayThanhToan, SoTienThanhToan, MaNV, SoDienThoai) VALUES 
    (1, 1, '2025-03-01', N'Chờ xử lý', 0, NULL, 150000, 1, '0901122334'),
    (2, 2, '2025-03-02', N'Đang giao', 50000, NULL, 200000, 2, '0911223344'),
	(3, 3, '2025-03-05', N'Đã giao', 0, '2025-03-07', 500000, 3, '0922334455'),
    (4, 4, '2025-03-06', N'Chờ xử lý', 300000, NULL, 800000, 4, '0933445566'),
    (5, 5, '2025-03-07', N'Đang giao', 0, NULL, 250000, 1, '0944556677'),
    (6, 6, '2025-03-08', N'Đã giao', 0, '2025-03-09', 400000, 2, '0955667788'),
    (7, 7, '2025-03-09', N'Chờ xử lý', 200000, NULL, 600000, 3, '0966778899');

-- Dữ liệu mẫu cho bảng ChiTietHoaDon
INSERT INTO ChiTietHoaDon (MaHD, MaNL, SoLuong, GiaBan) VALUES 
    (1, 1, 2, 50000),
    (1, 2, 3, 10000),
    (2, 3, 1, 20000),
	(3, 4, 5, 70000),
    (3, 5, 2, 45000),
    (4, 6, 3, 90000),
    (4, 1, 4, 50000),
    (5, 2, 1, 10000),
    (5, 3, 2, 20000),
    (6, 6, 1, 90000),
    (6, 4, 3, 70000),
    (7, 1, 5, 50000),
    (7, 3, 4, 20000);




-- VIEW  chỉ xem không chạy 
SELECT * FROM View_SoLuongDaBan;
SELECT * FROM View_CongNo;
SELECT * FROM View_DoanhThu;
SELECT * FROM View_DoanhThu ORDER BY Thang ASC;


-- View tính số lượng đã bán
CREATE VIEW View_SoLuongDaBan AS
SELECT nl.MaNL, nl.TenNL, SUM(cthd.SoLuong) AS TongSoLuongBan
FROM ChiTietHoaDon cthd
JOIN NguyenLieu nl ON cthd.MaNL = nl.MaNL
JOIN HoaDon hd ON cthd.MaHD = hd.MaHD
WHERE hd.TrangThai = N'Đã giao'
GROUP BY nl.MaNL, nl.TenNL;

-- Tạo View công nợ: makh, tenkh, sdt, số tiền, ngày nợ, hạn nộp
CREATE VIEW View_CongNo AS
SELECT 
    kh.MaKH,
    kh.TenKH,
    kh.SoDienThoai,
    hd.CongNo AS SoTienNo,
    hd.NgayDatHang AS NgayNo,
    DATEADD(DAY, 30, hd.NgayDatHang) AS HanNop -- Giả sử hạn nộp là 30 ngày sau ngày đặt hàng
FROM HoaDon hd
JOIN KhachHang kh ON hd.MaKH = kh.MaKH
WHERE hd.CongNo > 0; -- Chỉ lấy những hóa đơn còn nợ


-- Tạo View doanh thu
CREATE VIEW View_DoanhThu AS
WITH NhapHang AS (
    SELECT 
        MONTH(Ng.NgayNhap) AS Thang,
        SUM(Ng.SoLuongTon * Ng.MaNCC) AS TongTienNhap -- Giả sử MaNCC có giá trị liên quan đến giá nhập
    FROM NguyenLieu Ng
    GROUP BY MONTH(Ng.NgayNhap)
),
XuatHang AS (
    SELECT 
        MONTH(hd.NgayDatHang) AS Thang,
        SUM(cthd.SoLuong * cthd.GiaBan) AS TongTienXuat
    FROM ChiTietHoaDon cthd
    JOIN HoaDon hd ON cthd.MaHD = hd.MaHD
    WHERE hd.TrangThai = N'Đã giao'
    GROUP BY MONTH(hd.NgayDatHang)
),
CongNo AS (
    SELECT 
        MONTH(NgayDatHang) AS Thang,
        SUM(CongNo) AS SoTienChuaThanhToan
    FROM HoaDon
    GROUP BY MONTH(NgayDatHang)
),
SanPhamBanChay AS (
    SELECT TOP 1 WITH TIES 
        MONTH(hd.NgayDatHang) AS Thang,
        nl.TenNL AS SanPhamBanChay,
        SUM(cthd.SoLuong) AS SoLuongBan
    FROM ChiTietHoaDon cthd
    JOIN NguyenLieu nl ON cthd.MaNL = nl.MaNL
    JOIN HoaDon hd ON cthd.MaHD = hd.MaHD
    WHERE hd.TrangThai = N'Đã giao'
    GROUP BY MONTH(hd.NgayDatHang), nl.TenNL
    ORDER BY ROW_NUMBER() OVER (PARTITION BY MONTH(hd.NgayDatHang) ORDER BY SUM(cthd.SoLuong) DESC)
),
SanPhamBanCham AS (
    SELECT TOP 1 WITH TIES 
        MONTH(hd.NgayDatHang) AS Thang,
        nl.TenNL AS SanPhamBanCham,
        SUM(cthd.SoLuong) AS SoLuongBan
    FROM ChiTietHoaDon cthd
    JOIN NguyenLieu nl ON cthd.MaNL = nl.MaNL
    JOIN HoaDon hd ON cthd.MaHD = hd.MaHD
    WHERE hd.TrangThai = N'Đã giao'
    GROUP BY MONTH(hd.NgayDatHang), nl.TenNL
    ORDER BY ROW_NUMBER() OVER (PARTITION BY MONTH(hd.NgayDatHang) ORDER BY SUM(cthd.SoLuong) ASC)
)
SELECT 
    ISNULL(nh.Thang, xh.Thang) AS Thang,
    ISNULL(nh.TongTienNhap, 0) AS TongTienNhap,
    ISNULL(xh.TongTienXuat, 0) AS TongTienXuat,
    ISNULL(cn.SoTienChuaThanhToan, 0) AS SoTienChuaThanhToan,
    spbc.SanPhamBanChay,
    spbc.SoLuongBan AS SoLuongBanChay,
    spbm.SanPhamBanCham,
    spbm.SoLuongBan AS SoLuongBanCham,
    ISNULL(xh.TongTienXuat, 0) AS TongThu
FROM NhapHang nh
FULL JOIN XuatHang xh ON nh.Thang = xh.Thang
FULL JOIN CongNo cn ON nh.Thang = cn.Thang
LEFT JOIN SanPhamBanChay spbc ON nh.Thang = spbc.Thang
LEFT JOIN SanPhamBanCham spbm ON nh.Thang = spbm.Thang;