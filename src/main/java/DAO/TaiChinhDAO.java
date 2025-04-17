//Hung 
package DAO;

import Utils.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaiChinhDAO{

    private List<Object[]> getListOfArray(String sql, String[] tenCot, Object... mang) {
        List<Object[]> ds = new ArrayList<>();
        ResultSet rs = JdbcHelper.execQuery(sql, mang);
        try {
            while (rs.next()) {
                Object[] giaTri = new Object[tenCot.length];
                for (int i = 0; i < tenCot.length; i++) {
                    giaTri[i] = rs.getObject(tenCot[i]);
                }
                ds.add(giaTri);
            }
            return ds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<Object[]> getCongNo(String trangThai) {
        String sql;
        String[] tenCot = {"MaHD", "MaKH", "TenKH", "SoDienThoai", "NgayDatHang", "TongTien", "TrangThaiThanhToan"};

        if ("Đã thanh toán".equals(trangThai)) {
            sql = "SELECT * FROM CongNo WHERE TrangThaiThanhToan LIKE N'Đã thanh toán'";
        } else if ("Chưa thanh toán".equals(trangThai)) {
            sql = "SELECT * FROM CongNo WHERE TrangThaiThanhToan = N'Chưa thanh toán'";
        } else {
            sql = "SELECT * FROM CongNo";
        }
        return this.getListOfArray(sql, tenCot);
    }

    public List<Object[]> getDoanhThu() {
        String sql = "select*from DoanhThu_Thang";
        String[] tenCot = {"Thang", "DuNo", "TongTienNhapHang", "TongTienDaThanhToan", "TongDoanhThu"};
        return this.getListOfArray(sql, tenCot);
    }

    public List<Object[]> searchCongNo(String keyword) {
        String sql = "SELECT * FROM CongNo WHERE MaHD LIKE ? OR MaKH LIKE ? OR TenKH LIKE ? OR SoDienThoai LIKE ?";
        String[] tenCot = {"MaHD", "MaKH", "TenKH", "SoDienThoai", "NgayDatHang", "TongTien", "TrangThaiThanhToan"};
        String searchPattern = "%" + keyword + "%";
        return this.getListOfArray(sql, tenCot, searchPattern, searchPattern, searchPattern, searchPattern);
    }

    public void capNhatTrangThaiThanhToan(String maHD, boolean daThanhToan) {
        String sql;
        if (daThanhToan) {
            sql = "UPDATE HoaDon SET NgayThanhToan = GETDATE() WHERE MaHD = ?";
        } else {
            sql = "UPDATE HoaDon SET NgayThanhToan = NULL WHERE MaHD = ?";
        }
        JdbcHelper.execUpdate(sql, maHD);
    }

    

}
