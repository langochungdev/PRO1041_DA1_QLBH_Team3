create database QLBH 
use QLBH
-- Chạy toàn bộ 


CREATE TABLE NhanVien (
    MaNV NVARCHAR(50) PRIMARY KEY,
    MatKhau NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100) NULL,
    HoTen NVARCHAR(50) NOT NULL,
    VaiTro BIT NOT NULL,
	Hinh NVARCHAR(255) NULL,
	TrangThai BIT NOT NULL
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
	GiaXuat DECIMAL(18,2) NOT NULL
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC)
);

CREATE TABLE KhachHang (
    MaKH NVARCHAR(50) PRIMARY KEY,
    TenKH NVARCHAR(100) NOT NULL,
    SoDienThoai NVARCHAR(15) NULL,
    DiaChi NVARCHAR(255) NULL,
	Email NVARCHAR(50) NULL
);

CREATE TABLE HoaDon (
    MaHD NVARCHAR(50) PRIMARY KEY,
    MaKH NVARCHAR(50) NOT NULL,
    NgayDatHang DATE NOT NULL,
    TrangThai NVARCHAR(20) CHECK (TrangThai IN (N'Chờ xử lý', N'Đã giao')) NOT NULL,
    NgayThanhToan DATE NULL,
    MaNV NVARCHAR(50) NOT NULL,
	TongTien DECIMAL(18,2) NULL,
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);

CREATE TABLE ChiTietHoaDon (
    MaHD NVARCHAR(50) NOT NULL,
    MaNL NVARCHAR(50) NOT NULL,
    SoLuong INT CHECK (SoLuong > 0) NOT NULL,
    ThanhTien DECIMAL(18,2) NULL,
    PRIMARY KEY (MaHD, MaNL),
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD),
    FOREIGN KEY (MaNL) REFERENCES NguyenLieu(MaNL)
);
GO 


-- TRIGGER 
CREATE TRIGGER trg_CapNhatThanhTien
ON ChiTietHoaDon
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON
    UPDATE cthd
    SET ThanhTien = nl.GiaXuat * cthd.SoLuong
    FROM ChiTietHoaDon cthd
    JOIN inserted i ON cthd.MaHD = i.MaHD AND cthd.MaNL = i.MaNL
    JOIN NguyenLieu nl ON cthd.MaNL = nl.MaNL
END
GO 


CREATE TRIGGER trg_CapNhatTongTien ON ChiTietHoaDon
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    UPDATE HoaDon
    SET TongTien = (SELECT SUM(ThanhTien)
                    FROM ChiTietHoaDon 
                    WHERE ChiTietHoaDon.MaHD = HoaDon.MaHD)
    WHERE MaHD IN (SELECT DISTINCT MaHD FROM inserted UNION SELECT DISTINCT MaHD FROM deleted);
END;
GO 


-- DATA 
INSERT INTO NhanVien (MaNV, MatKhau, Email, HoTen, VaiTro, Hinh, TrangThai) VALUES 
    ('hung', '123', 'langochungse23@gmail.com', N'Hùng', 1, '', 1),
    ('kha', '123', '', N'Kha', 1, '', 1),
    ('trung', '123', '', N'Trung', 1, '', 1),
    ('hai', '123', '', N'Hải', 1, '', 1),
	('nv', '123', '', N'nhan vien', 0, '', 1),
	('nv2', '123', '', N'nhan vien 2', 0, '', 0);

INSERT INTO CaLam (MaCa, MaNV, NgayLam, CaLam) VALUES
('CA001', 'hung', '2025-04-01', 'Ca 1'),
('CA002', 'kha', '2025-04-01', 'Ca 2'),
('CA003', 'trung', '2025-04-01', 'Ca 3'),
('CA004', 'hai', '2025-04-02', 'Ca 1'),
('CA005', 'hung', '2025-04-02', 'Ca 2'),
('CA006', 'kha', '2025-04-02', 'Ca 3'),
('CA007', 'trung', '2025-04-03', 'Ca 1');

INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, SoDienThoai) VALUES
('NCC01', N'Công ty A', N'123 Đường A, TP.HCM', '0909123456'),
('NCC02', N'Công ty B', N'456 Đường B, Hà Nội', '0912345678'),
('NCC03', N'Công ty C', N'789 Đường C, Đà Nẵng', '0923456789');

INSERT INTO NguyenLieu (MaNL, TenNL, MaNCC, NgayNhap, HanSuDung, SoLuongTon, DonViTinh, GiaNhap, GiaXuat) VALUES
('NL01', N'Bột mì','NCC01', '2025-01-10', '2025-06-10', 2, N'kg', 10000, 20000),
('NL02', N'Đường','NCC02', '2025-02-05', '2025-07-05', 7, N'kg', 10000, 20000),
('NL03', N'Sữa','NCC03', '2025-03-15', '2025-05-15', 6, N'lít', 10000, 20000),
('NL04', N'Trứng gà','NCC01', '2025-04-01', '2025-05-01', 17, N'quả', 10000, 20000),
('NL05', N'Bơ','NCC02', '2025-01-20', '2025-07-20', 15, N'kg', 10000, 20000),
('NL06', N'Muối','NCC03', '2025-02-25', '2025-08-25', 7, N'kg', 10000, 20000);

INSERT INTO KhachHang (MaKH,TenKH, SoDienThoai, Email, DiaChi) VALUES
('KH01', N'Nguyễn Văn A', '0987654321', 'langochungse23@gmail.com', N'789 Đường C, TP.HCM'),
('KH02', N'Trần Thị B', '0976543210', 'langochungse23@gmail.com', N'321 Đường D, Hà Nội'),
('KH03', N'Lê Thị C', '0965432109', 'langochungse23@gmail.com', N'654 Đường E, Đà Nẵng'),
('KH04', N'Phạm Văn D', '0954321098', 'langochungse23@gmail.com', N'987 Đường F, Cần Thơ');

INSERT INTO HoaDon (MaHD, MaKH, NgayDatHang, TrangThai, NgayThanhToan, MaNV, TongTien) VALUES
('HD01', 'KH01', '2025-01-20', N'Đã giao', '2025-01-21', 'hung', 60000),
('HD02', 'KH02', '2025-02-18', N'Đã giao', '2025-02-19', 'hung', 140000),
('HD03', 'KH03', '2025-03-25', N'Đã giao', '2025-03-26', 'hung', 160000),
('HD04', 'KH04', '2025-04-10', N'Đã giao', '2025-04-11', 'hung', 240000),
('HD05', 'KH01', '2025-01-28', N'Đã giao', '2025-01-29', 'hung', 80000),
('HD06', 'KH02', '2025-02-22', N'Đã giao', '2025-02-23', 'hung', 120000),
('HD07', 'KH03', '2025-02-05', N'Chờ xử lý', NULL, 'hung', 40000),
('HD08', 'KH04', '2025-02-10', N'Chờ xử lý', NULL, 'hung', 40000),
('HD09', 'KH01', '2025-02-15', N'Chờ xử lý', NULL, 'hung', 40000),
('HD10', 'KH02', '2025-02-28', N'Chờ xử lý', NULL, 'hung', 40000);

INSERT INTO ChiTietHoaDon (MaHD, MaNL, SoLuong, ThanhTien) VALUES
('HD01', 'NL01', 2, 40000),
('HD02', 'NL02', 5, 100000),
('HD03', 'NL03', 3, 60000),
('HD04', 'NL04', 10, 200000),
('HD05', 'NL05', 4, 80000),
('HD06', 'NL06', 6, 120000),
('HD01', 'NL02', 1, 20000),
('HD02', 'NL03', 2, 40000),
('HD03', 'NL04', 5, 100000),
('HD04', 'NL05', 2, 40000),
('HD07', 'NL05', 2, 40000),
('HD08', 'NL05', 2, 40000),
('HD09', 'NL05', 2, 40000),
('HD10', 'NL05', 2, 40000);
GO 


-- VIEW 
-- kha 
CREATE VIEW v_CaLam AS
SELECT 
    c.MaCa, 
    c.MaNV, 
    n.HoTen AS TenNhanVien, 
    c.NgayLam, 
    c.CaLam
FROM CaLam c
JOIN NhanVien n ON c.MaNV = n.MaNV;
GO 

-- hung 
CREATE VIEW CongNo AS
SELECT 
    hd.MaHD,
    kh.MaKH,
    kh.TenKH,
    kh.SoDienThoai,
	hd.NgayDatHang,
    hd.TongTien,
    TrangThaiThanhToan = CASE 
        WHEN hd.NgayThanhToan IS NOT NULL THEN N'Đã thanh toán'
        ELSE N'Chưa thanh toán'
    END
FROM HoaDon hd
JOIN KhachHang kh ON hd.MaKH = kh.MaKH
GO 

CREATE VIEW DoanhThu_Thang AS
SELECT 
    FORMAT(ISNULL(hd.NgayThanhToan, hd.NgayDatHang), 'yyyy-MM') AS Thang,

    SUM(CASE 
        WHEN hd.NgayThanhToan IS NULL THEN hd.TongTien
        ELSE 0
    END) AS DuNo,
    (
        SELECT ISNULL(SUM(SoLuongTon * GiaNhap), 0)
        FROM NguyenLieu nl
        WHERE FORMAT(nl.NgayNhap, 'yyyy-MM') = FORMAT(ISNULL(hd.NgayThanhToan, hd.NgayDatHang), 'yyyy-MM')
    ) AS TongTienNhapHang,
    SUM(CASE 
        WHEN hd.NgayThanhToan IS NOT NULL THEN hd.TongTien
        ELSE 0
    END) AS TongTienDaThanhToan,
    (
        SUM(hd.TongTien) -
        (
            SELECT ISNULL(SUM(SoLuongTon * GiaNhap), 0)
            FROM NguyenLieu nl
            WHERE FORMAT(nl.NgayNhap, 'yyyy-MM') = FORMAT(ISNULL(hd.NgayThanhToan, hd.NgayDatHang), 'yyyy-MM')
        )
    ) AS TongDoanhThu

FROM HoaDon hd
GROUP BY FORMAT(ISNULL(hd.NgayThanhToan, hd.NgayDatHang), 'yyyy-MM')
GO


CREATE VIEW qlKho AS
SELECT 
    nl.MaNL,
    nl.TenNL,
    nl.SoLuongTon AS SoLuongNhap,
    ISNULL((
        SELECT SUM(cthd.SoLuong)
        FROM ChiTietHoaDon cthd
        WHERE cthd.MaNL = nl.MaNL
    ), 0) AS SoLuongDaBan,
    (nl.SoLuongTon - ISNULL((
        SELECT SUM(cthd.SoLuong)
        FROM ChiTietHoaDon cthd
        WHERE cthd.MaNL = nl.MaNL
    ), 0)) AS SoLuongConLai,
    nl.DonViTinh,
    nl.NgayNhap,
    nl.HanSuDung,
    ncc.TenNCC AS NhaCungCap,
    nl.GiaNhap,
    nl.GiaXuat
FROM NguyenLieu nl
JOIN NhaCungCap ncc ON nl.MaNCC = ncc.MaNCC
GO 

-- KHÔNG CHẠY 
SELECT * FROM CongNo
SELECT * FROM DoanhThu_Thang
SELECT * FROM v_CaLam
SELECT * FROM qlkho