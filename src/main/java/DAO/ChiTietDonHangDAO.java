
package DAO;

import Entity.ChiTietDonHangE;
import Utils.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ChiTietDonHangDAO {
    // Lấy danh sách chi tiết hóa đơn theo mã hóa đơn
    public List<ChiTietDonHangE> getByMaHD(String maHD) {
        List<ChiTietDonHangE> list = new ArrayList<>();
        String sql = "SELECT n.TenNL,n.DonViTinh, c.SoLuong, c.GiaBan " + 
                 "FROM ChiTietHoaDon c "+
                 "JOIN NguyenLieu n ON c.MaNL = n.MaNL "+
                " WHERE c.MaHD = ?;";
        try (ResultSet rs = JdbcHelper.execQuery(sql, maHD)) {
            while (rs.next()) {
                ChiTietDonHangE c = new ChiTietDonHangE();
                c.setTenSanPham(rs.getString("TenNL"));
                c.setDonViTinh(rs.getString("DonViTinh"));
                c.setSoLuong(rs.getInt("SoLuong"));
                c.setGiaBan(rs.getBigDecimal("GiaBan"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
     
    
}
