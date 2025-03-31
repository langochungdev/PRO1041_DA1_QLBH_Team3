package DAO;

import Entity.DonHangE;
import Utils.JdbcHelper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DonHangDAO {
      // Lấy danh sách đơn hàng
    public List<DonHangE> getAll() {
        List<DonHangE> list = new ArrayList<>();
        String sql = "SELECT h.maHD, k.tenKH, k.soDienThoai, h.ngayDatHang, h.ngayThanhToan, h.trangThai " +
                     "FROM HoaDon h " +
                     "JOIN KhachHang k ON h.maKH = k.maKH";
        try (ResultSet rs = JdbcHelper.execQuery(sql)) {
            while (rs.next()) {
                DonHangE dh = new DonHangE();
                dh.setMaHD(rs.getString("maHD"));
                dh.setTenKH(rs.getString("tenKH"));
                dh.setSoDienThoai(rs.getString("soDienThoai"));
                dh.setNgayDatHang(rs.getDate("ngayDatHang"));
                dh.setNgayThanhToan(rs.getDate("ngayThanhToan"));
                dh.setTrangThai(rs.getString("trangThai"));
                list.add(dh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //khai báo để lọc theo trạng thái
    public List<DonHangE> getByTrangThai(String trangThai) {
    List<DonHangE> list = new ArrayList<>();
    String sql = "SELECT h.maHD, k.tenKH, k.soDienThoai, h.ngayDatHang, h.ngayThanhToan, h.trangThai " +
                 "FROM HoaDon h " +
                 "JOIN KhachHang k ON h.maKH = k.maKH " +
                 "WHERE h.trangThai = ?";

    try (ResultSet rs = JdbcHelper.execQuery(sql, trangThai)) {
        while (rs.next()) {
            DonHangE dh = new DonHangE();
            dh.setMaHD(rs.getString("maHD"));
            dh.setTenKH(rs.getString("tenKH"));
            dh.setSoDienThoai(rs.getString("soDienThoai"));
            dh.setNgayDatHang(rs.getDate("ngayDatHang"));
            dh.setNgayThanhToan(rs.getDate("ngayThanhToan"));
            dh.setTrangThai(rs.getString("trangThai")); 
            list.add(dh);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}
     
// Tìm kiếm theo từ khóa
   public List<DonHangE> timKiemTheoTuKhoa(String tuKhoa) {
    List<DonHangE> list = new ArrayList<>();
    String sql = "SELECT h.maHD, k.tenKH, k.soDienThoai, h.ngayDatHang, h.ngayThanhToan, h.trangThai " +
                 "FROM HoaDon h " +
                 "JOIN KhachHang k ON h.maKH = k.maKH " +
                 "WHERE h.maHD LIKE ? OR k.tenKH LIKE ? OR k.soDienThoai LIKE ? " +
                 "OR h.ngayDatHang LIKE ? OR h.ngayThanhToan LIKE ? OR h.trangThai LIKE ?";

    try (ResultSet rs = JdbcHelper.execQuery(sql, 
            "%" + tuKhoa + "%", "%" + tuKhoa + "%", "%" + tuKhoa + "%", 
            "%" + tuKhoa + "%", "%" + tuKhoa + "%", "%" + tuKhoa + "%")) {
        while (rs.next()) {
            DonHangE dh = new DonHangE();
            dh.setMaHD(rs.getString("maHD"));
            dh.setTenKH(rs.getString("tenKH"));
            dh.setSoDienThoai(rs.getString("soDienThoai"));
            dh.setNgayDatHang(rs.getDate("ngayDatHang"));
            dh.setNgayThanhToan(rs.getDate("ngayThanhToan"));
            dh.setTrangThai(rs.getString("trangThai"));
            list.add(dh);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}
    
    public DonHangE getDonHangById(String maHD) {
    String sql = "SELECT h.maHD, k.tenKH, k.soDienThoai, h.ngayDatHang, h.ngayThanhToan, h.trangThai, k.DiaChi " +
                 "FROM HoaDon h " +
                 "JOIN KhachHang k ON h.maKH = k.maKH " +
                 "WHERE h.maHD = ?";
    try (ResultSet rs = JdbcHelper.execQuery(sql, maHD)) {
        if (rs.next()) {
            DonHangE dh = new DonHangE();
            dh.setMaHD(rs.getString("maHD"));
            dh.setTenKH(rs.getString("tenKH"));
            dh.setSoDienThoai(rs.getString("soDienThoai"));
            dh.setDiaChi(rs.getString("DiaChi"));
            dh.setNgayDatHang(rs.getDate("ngayDatHang"));
            dh.setNgayThanhToan(rs.getDate("ngayThanhToan"));
            dh.setTrangThai(rs.getString("trangThai"));
            return dh;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
    
    public List<String> getAllSanPham() {
    List<String> list = new ArrayList<>();
    String sql = "SELECT TenNL FROM NguyenLieu"; // Lấy tất cả tên sản phẩm
    try (ResultSet rs = JdbcHelper.execQuery(sql)) {
        while (rs.next()) {
            list.add(rs.getString("TenNL"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}
   
    
}


