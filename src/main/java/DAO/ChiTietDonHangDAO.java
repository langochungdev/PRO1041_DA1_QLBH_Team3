package DAO;

import Entity.ChiTietDonHangE;
import Utils.JdbcHelper;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDonHangDAO {

    // Lấy danh sách chi tiết đơn hàng từ mã hóa đơn
    public List<ChiTietDonHangE> selectByMaHD(String maHD) {
        List<ChiTietDonHangE> list = new ArrayList<>();
        String sql = """
            SELECT 
                c.MaHD, 
                c.MaNL, 
                n.TenNL, 
                c.SoLuong, 
                n.DonViTinh, 
                n.GiaXuat, 
                c.ThanhTien
            FROM ChiTietHoaDon c
            JOIN NguyenLieu n ON c.MaNL = n.MaNL
            WHERE c.MaHD = ?
        """;
        try (ResultSet rs = JdbcHelper.execQuery(sql, maHD)) {
            while (rs.next()) {
                ChiTietDonHangE cthd = new ChiTietDonHangE();
                cthd.setMaHD(rs.getString("MaHD"));
                cthd.setMaNL(rs.getString("MaNL"));
                cthd.setTenSanPham(rs.getString("TenNL"));
                cthd.setSoLuong(rs.getInt("SoLuong"));
                cthd.setDonViTinh(rs.getString("DonViTinh"));
                cthd.setGiaXuat(rs.getBigDecimal("GiaXuat"));
                cthd.setThanhTien(rs.getBigDecimal("ThanhTien"));
                list.add(cthd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm sản phẩm vào hóa đơn
    public void insert(String maHD, String maNL, int soLuong) {
        String sql = "INSERT INTO ChiTietHoaDon (MaHD, MaNL, SoLuong) VALUES (?, ?, ?)";
        JdbcHelper.execUpdate(sql, maHD, maNL, soLuong);
    }

    // Cập nhật số lượng
    public void update(String maHD, String maNL, int soLuong) {
        String sql = "UPDATE ChiTietHoaDon SET SoLuong = ? WHERE MaHD = ? AND MaNL = ?";
        JdbcHelper.execUpdate(sql, soLuong, maHD, maNL);
    }

    // Xóa một chi tiết đơn hàng
    public void delete(String maHD, String maNL) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE MaHD = ? AND MaNL = ?";
        JdbcHelper.execUpdate(sql, maHD, maNL);
    }

    public void deleteByMaHD(String maHD) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE MaHD = ?";
        JdbcHelper.execUpdate(sql, maHD);
    }
    
    
    public int selectSoLuong(String maHD, String maNL) {
    String sql = "SELECT SoLuong FROM ChiTietHoaDon WHERE MaHD = ? AND MaNL = ?";
    try {
        ResultSet rs = JdbcHelper.execQuery(sql, maHD, maNL);
        if (rs.next()) {
            return rs.getInt("SoLuong");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0; // Trường hợp chưa có chi tiết
}

}
