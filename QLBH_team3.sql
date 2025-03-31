create database QLBH 
use QLBH

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
	GiaNhap DECIMAL(18,2) NOT NULL,
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

-- Thêm nhà cung cấp
INSERT INTO NhaCungCap (TenNCC, DiaChi, SoDienThoai) VALUES
(N'Công ty A', N'123 Đường A, TP.HCM', N'0909123456'),
(N'Công ty B', N'456 Đường B, Hà Nội', N'0912345678'),
(N'Công ty C', N'789 Đường C, Đà Nẵng', N'0923456789');

-- Thêm nguyên liệu
INSERT INTO NguyenLieu (TenNL, MaNCC, NgayNhap, HanSuDung, SoLuongTon, DonViTinh, GiaNhap) VALUES
(N'Bột mì', 1, '2025-01-10', '2025-06-10', 100, N'kg', 20000),
(N'Đường', 1, '2025-02-05', '2025-07-05', 200, N'kg', 15000),
(N'Sữa', 2, '2025-03-15', '2025-05-15', 150, N'lít', 25000),
(N'Trứng gà', 2, '2025-04-01', '2025-05-01', 300, N'quả', 5000),
(N'Bơ', 3, '2025-01-20', '2025-07-20', 120, N'kg', 30000),
(N'Muối', 3, '2025-02-25', '2025-08-25', 250, N'kg', 10000);

-- Thêm khách hàng
INSERT INTO KhachHang (TenKH, SoDienThoai, DiaChi) VALUES
(N'Nguyễn Văn A', N'0987654321', N'789 Đường C, TP.HCM'),
(N'Trần Thị B', N'0976543210', N'321 Đường D, Hà Nội'),
(N'Lê Thị C', N'0965432109', N'654 Đường E, Đà Nẵng'),
(N'Phạm Văn D', N'0954321098', N'987 Đường F, Cần Thơ');

-- Thêm hóa đơn
INSERT INTO HoaDon (MaHD, MaKH, NgayDatHang, TrangThai, NgayThanhToan, SoTienThanhToan, MaNV, SoDienThoai) VALUES
(1, 1, '2025-01-20', N'Đã giao', '2025-01-21', 5000000, 1, '0987654321'),
(2, 2, '2025-02-18', N'Đã giao', '2025-02-19', 7500000, 2, '0976543210'),
(3, 3, '2025-03-25', N'Đã giao', '2025-03-26', 5000000, 3, '0965432109'),
(4, 4, '2025-04-10', N'Đã giao', '2025-04-11', 10000000, 4, '0954321098'),
(5, 1, '2025-01-28', N'Đã giao', '2025-01-29', 6000000, 2, '0987654321'),
(6, 2, '2025-02-22', N'Đã giao', '2025-02-23', 8500000, 3, '0976543210'),
(7, 3, '2025-02-05', N'Đã giao', NULL, 5000000, 1, '0965432109'),
(8, 4, '2025-02-10', N'Đã giao', NULL, 7000000, 2, '0954321098'),
(9, 1, '2025-02-15', N'Đã giao', NULL, 4000000, 3, '0987654321'),
(10, 2, '2025-02-28', N'Đã giao', NULL, 9000000, 4, '0976543210');

-- Thêm chi tiết hóa đơn
INSERT INTO ChiTietHoaDon (MaHD, MaNL, SoLuong, GiaBan) VALUES
(1, 1, 2, 25000),
(2, 2, 5, 15000),
(3, 3, 3, 30000),
(4, 4, 10, 10000),
(5, 5, 4, 35000),
(6, 6, 6, 12000),
(1, 2, 1, 18000),
(2, 3, 2, 28000),
(3, 4, 5, 5000),
(4, 5, 2, 33000);



-- view khong chay 
SELECT * FROM View_CongNo
select*from View_DoanhThu

CREATE VIEW View_CongNo AS
SELECT 
    KH.MaKH, 
    KH.TenKH, 
    KH.SoDienThoai, 
    HD.SoTienThanhToan AS SoTien, 
    HD.NgayDatHang AS NgayNo, 
    ISNULL(CONVERT(NVARCHAR, HD.NgayThanhToan, 23), N'Chưa thanh toán') AS TrangThaiThanhToan
FROM HoaDon HD
JOIN KhachHang KH ON HD.MaKH = KH.MaKH;


CREATE VIEW View_DoanhThu AS
WITH Nhap AS (
    SELECT 
        MONTH(NgayNhap) AS Thang,
        SUM(SoLuongTon * GiaNhap) AS TongSoTienNhap
    FROM NguyenLieu
    GROUP BY MONTH(NgayNhap)
),
Xuat AS (
    SELECT 
        MONTH(NgayDatHang) AS Thang,
        SUM(CASE 
                WHEN NgayThanhToan IS NOT NULL THEN SoTienThanhToan 
                ELSE -SoTienThanhToan 
            END) AS TongSoTienXuat
    FROM HoaDon
    GROUP BY MONTH(NgayDatHang)
),
BanChay AS (
    SELECT 
        MONTH(hd.NgayDatHang) AS Thang, 
        nl.TenNL AS SanPhamBanChay,
        SUM(cthd.SoLuong) AS TongSoLuong
    FROM ChiTietHoaDon cthd
    JOIN HoaDon hd ON cthd.MaHD = hd.MaHD
    JOIN NguyenLieu nl ON cthd.MaNL = nl.MaNL
    GROUP BY MONTH(hd.NgayDatHang), nl.TenNL
),
BanIt AS (
    SELECT 
        MONTH(hd.NgayDatHang) AS Thang, 
        nl.TenNL AS SanPhamBanIt,
        SUM(cthd.SoLuong) AS TongSoLuong
    FROM ChiTietHoaDon cthd
    JOIN HoaDon hd ON cthd.MaHD = hd.MaHD
    JOIN NguyenLieu nl ON cthd.MaNL = nl.MaNL
    GROUP BY MONTH(hd.NgayDatHang), nl.TenNL
),
BanChayRanked AS (
    SELECT Thang, SanPhamBanChay
    FROM (
        SELECT Thang, SanPhamBanChay, RANK() OVER (PARTITION BY Thang ORDER BY TongSoLuong DESC) AS rnk
        FROM BanChay
    ) AS Ranked WHERE rnk = 1
),
BanItRanked AS (
    SELECT Thang, SanPhamBanIt
    FROM (
        SELECT Thang, SanPhamBanIt, RANK() OVER (PARTITION BY Thang ORDER BY TongSoLuong ASC) AS rnk
        FROM BanIt
    ) AS Ranked WHERE rnk = 1
)
SELECT 
    COALESCE(n.Thang, x.Thang, bc.Thang, bi.Thang) AS Thang,
    COALESCE(n.TongSoTienNhap, 0) AS TongSoTienNhap,
    COALESCE(x.TongSoTienXuat, 0) AS TongSoTienXuat,
    COALESCE(bc.SanPhamBanChay, N'Không có') AS SanPhamBanChay,
    COALESCE(bi.SanPhamBanIt, N'Không có') AS SanPhamBanIt,
    COALESCE(x.TongSoTienXuat, 0) - COALESCE(n.TongSoTienNhap, 0) AS TongDoanhThu
FROM Nhap n
FULL JOIN Xuat x ON n.Thang = x.Thang
FULL JOIN BanChayRanked bc ON COALESCE(n.Thang, x.Thang) = bc.Thang
FULL JOIN BanItRanked bi ON COALESCE(n.Thang, x.Thang) = bi.Thang;













