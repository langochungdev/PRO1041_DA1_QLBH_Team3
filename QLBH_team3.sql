create database QLBH 
use QLBH
-- CHẠY TỪNG CỤM 1 


-- CỤM 1 
CREATE TABLE NhanVien (
    MaNV NVARCHAR(50) PRIMARY KEY,
    MatKhau NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100) NULL,
    HoTen NVARCHAR(50) NOT NULL,
    VaiTro BIT NOT NULL,
	LinkAnh NVARCHAR(255) NULL
);

CREATE TABLE CaLam (
    MaCa NVARCHAR(50) PRIMARY KEY,
    MaNV NVARCHAR(50) NOT NULL,
    NgayLam DATE NOT NULL,
    CaLam NVARCHAR(10) CHECK (CaLam IN (N'Ca 1', N'Ca 2', N'Ca 3')) NOT NULL,
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);

CREATE TABLE NhaCungCap (
    MaNCC NVARCHAR(50) PRIMARY KEY,
    TenNCC NVARCHAR(100) NOT NULL,
    DiaChi NVARCHAR(255) NOT NULL,
    SoDienThoai NVARCHAR(15) NOT NULL
);

CREATE TABLE NguyenLieu (
    MaNL NVARCHAR(50) PRIMARY KEY,
    TenNL NVARCHAR(100) NOT NULL,
    MaNCC NVARCHAR(50) NOT NULL,
    NgayNhap DATE NOT NULL,
    HanSuDung DATE NOT NULL,
    SoLuongTon INT CHECK (SoLuongTon >= 0) NOT NULL,
    DonViTinh NVARCHAR(20) NOT NULL,
	GiaNhap DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC)
);

CREATE TABLE KhachHang (
    MaKH NVARCHAR(50) PRIMARY KEY,
    TenKH NVARCHAR(100) NOT NULL,
    SoDienThoai NVARCHAR(15) NOT NULL,
    DiaChi NVARCHAR(255) NOT NULL
);

CREATE TABLE HoaDon (
    MaHD NVARCHAR(50) PRIMARY KEY,
    MaKH NVARCHAR(50) NOT NULL,
    NgayDatHang DATE NOT NULL,
    TrangThai NVARCHAR(20) CHECK (TrangThai IN (N'Chờ xử lý', N'Đang giao', N'Đã giao')) NOT NULL,
    NgayThanhToan DATE NULL,
    SoTienThanhToan DECIMAL(18,2) NOT NULL,
    MaNV NVARCHAR(50) NOT NULL,
    SoDienThoai NVARCHAR(15) NOT NULL,
	TongTien DECIMAL(18,2) NULL,
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);

CREATE TABLE ChiTietHoaDon (
    MaHD NVARCHAR(50) NOT NULL,
    MaNL NVARCHAR(50) NOT NULL,
    SoLuong INT CHECK (SoLuong > 0) NOT NULL,
    GiaBan DECIMAL(18,2) NOT NULL,
    PRIMARY KEY (MaHD, MaNL),
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD),
    FOREIGN KEY (MaNL) REFERENCES NguyenLieu(MaNL)
);




-- CỤM 2 HẢI 
CREATE TRIGGER trg_CapNhatTongTien ON ChiTietHoaDon
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    UPDATE HoaDon
    SET TongTien = (SELECT SUM(SoLuong * GiaBan) 
                    FROM ChiTietHoaDon 
                    WHERE ChiTietHoaDon.MaHD = HoaDon.MaHD)
    WHERE MaHD IN (SELECT DISTINCT MaHD FROM inserted UNION SELECT DISTINCT MaHD FROM deleted);
END;




-- CỤM 3 
INSERT INTO NhanVien (MaNV, MatKhau, Email, HoTen, VaiTro, LinkAnh) VALUES 
    ('hung', '123', 'langochungse23@gmail.com', N'Hùng', 1, ''),
    ('kha', '123', '', N'Kha', 1, ''),
    ('trung', '123', '', N'Trung', 1, ''),
    ('hai', '123', '', N'Hải', 1, '');

INSERT INTO CaLam (MaCa, MaNV, NgayLam, CaLam) VALUES
('c1', 'hung', '2025-04-01', N'Ca 1'),
('c2', 'kha', '2025-04-01', N'Ca 2'),
('c3', 'trung', '2025-04-01', N'Ca 3'),
('c4', 'hai', '2025-04-02', N'Ca 1'),
('c5', 'hung', '2025-04-02', N'Ca 2'),
('c6', 'kha', '2025-04-02', N'Ca 3'),
('c7', 'trung', '2025-04-03', N'Ca 1');

INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, SoDienThoai) VALUES
('NCC01', N'Công ty A', N'123 Đường A, TP.HCM', N'0909123456'),
('NCC02', N'Công ty B', N'456 Đường B, Hà Nội', N'0912345678'),
('NCC03', N'Công ty C', N'789 Đường C, Đà Nẵng', N'0923456789');

INSERT INTO NguyenLieu (MaNL, TenNL, MaNCC, NgayNhap, HanSuDung, SoLuongTon, DonViTinh, GiaNhap) VALUES
('NL01', N'Bột mì','NCC01', '2025-01-10', '2025-06-10', 100, N'kg', 20000),
('NL02', N'Đường','NCC02', '2025-02-05', '2025-07-05', 200, N'kg', 15000),
('NL03', N'Sữa','NCC03', '2025-03-15', '2025-05-15', 150, N'lít', 25000),
('NL04', N'Trứng gà','NCC01', '2025-04-01', '2025-05-01', 300, N'quả', 5000),
('NL05', N'Bơ','NCC02', '2025-01-20', '2025-07-20', 120, N'kg', 30000),
('NL06', N'Muối','NCC03', '2025-02-25', '2025-08-25', 250, N'kg', 10000);

INSERT INTO KhachHang (MaKH,TenKH, SoDienThoai, DiaChi) VALUES
('KH01', N'Nguyễn Văn A', N'0987654321', N'789 Đường C, TP.HCM'),
('KH02', N'Trần Thị B', N'0976543210', N'321 Đường D, Hà Nội'),
('KH03', N'Lê Thị C', N'0965432109', N'654 Đường E, Đà Nẵng'),
('KH04', N'Phạm Văn D', N'0954321098', N'987 Đường F, Cần Thơ');

INSERT INTO HoaDon (MaHD, MaKH, NgayDatHang, TrangThai, NgayThanhToan, SoTienThanhToan, MaNV, SoDienThoai) VALUES
('HD01', 'KH01', '2025-01-20', N'Đã giao', '2025-01-21', 5000000, 'hung', '0987654321'),
('HD02', 'KH02', '2025-02-18', N'Đã giao', '2025-02-19', 7500000, 'hung', '0976543210'),
('HD03', 'KH03', '2025-03-25', N'Đã giao', '2025-03-26', 5000000, 'hung', '0965432109'),
('HD04', 'KH04', '2025-04-10', N'Đã giao', '2025-04-11', 10000000, 'hung', '0954321098'),
('HD05', 'KH01', '2025-01-28', N'Đã giao', '2025-01-29', 6000000, 'hung', '0987654321'),
('HD06', 'KH02', '2025-02-22', N'Đã giao', '2025-02-23', 8500000, 'hung', '0976543210'),
('HD07', 'KH03', '2025-02-05', N'Chờ xử lý', NULL, 5000000, 'hung', '0965432109'),
('HD08', 'KH04', '2025-02-10', N'Chờ xử lý', NULL, 7000000, 'hung', '0954321098'),
('HD09', 'KH01', '2025-02-15', N'Đang giao', NULL, 4000000, 'hung', '0987654321'),
('HD20', 'KH02', '2025-02-28', N'Đang giao', NULL, 9000000, 'hung', '0976543210');

INSERT INTO ChiTietHoaDon (MaHD, MaNL, SoLuong, GiaBan) VALUES
('HD01', 'NL01', 2, 25000),
('HD02', 'NL02', 5, 15000),
('HD03', 'NL03', 3, 30000),
('HD04', 'NL04', 10, 10000),
('HD05', 'NL05', 4, 35000),
('HD06', 'NL06', 6, 12000),
('HD01', 'NL02', 1, 18000),
('HD02', 'NL03', 2, 28000),
('HD03', 'NL04', 5, 5000),
('HD04', 'NL05', 2, 33000);



-- CỤM 4 HÙNG 
CREATE VIEW View_CongNo AS
SELECT 
	HD.MaHD,
    KH.MaKH, 
    KH.TenKH, 
    KH.SoDienThoai, 
    HD.SoTienThanhToan AS SoTien, 
    HD.NgayDatHang AS NgayNo, 
    ISNULL(CONVERT(NVARCHAR, HD.NgayThanhToan, 23), N'Chưa thanh toán') AS TrangThaiThanhToan
FROM HoaDon HD
JOIN KhachHang KH ON HD.MaKH = KH.MaKH;



-- CỤM 5 HÙNG 
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



-- CỤM 6 TRUNG 
CREATE VIEW View_NguyenLieu AS
SELECT 
    nl.MaNL,
    nl.TenNL,
    nl.SoLuongTon AS SoLuongNhap,
    COALESCE(SUM(cthd.SoLuong), 0) AS SoLuongDaBan,
    nl.SoLuongTon - COALESCE(SUM(cthd.SoLuong), 0) AS SoLuongConLai,
    nl.DonViTinh,
    nl.NgayNhap,
    nl.HanSuDung,
    ncc.TenNCC AS NhaCungCap,
    nl.GiaNhap
FROM NguyenLieu nl
LEFT JOIN ChiTietHoaDon cthd ON nl.MaNL = cthd.MaNL
LEFT JOIN NhaCungCap ncc ON nl.MaNCC = ncc.MaNCC
GROUP BY nl.MaNL, nl.TenNL, nl.SoLuongTon, nl.DonViTinh, nl.NgayNhap, nl.HanSuDung, ncc.TenNCC, nl.GiaNhap;


-- Cụm 7 --
CREATE VIEW v_CaLam AS
SELECT 
    c.MaCa, 
    c.MaNV, 
    n.HoTen AS TenNhanVien, 
    c.NgayLam, 
    c.CaLam
FROM CaLam c
JOIN NhanVien n ON c.MaNV = n.MaNV;


-- KHÔNG CHẠY 
SELECT * FROM View_NguyenLieu
SELECT * FROM View_CongNo
SELECT * FROM View_DoanhThu
SELECT * FROM v_CaLam