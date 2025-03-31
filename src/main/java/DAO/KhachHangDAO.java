
package DAO;

import Utils.JdbcHelper;
import java.sql.ResultSet;


public class KhachHangDAO {
    public static String getDiaChiKhachHang(String maKhachHang) {
        String sql = "SELECT DiaChi FROM KhachHang WHERE MaKH = ?";
        try {
            ResultSet rs = JdbcHelper.execQuery(sql, maKhachHang);
            if (rs.next()) {
                return rs.getString("DiaChi");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Không tìm thấy địa chỉ";
    }
}
