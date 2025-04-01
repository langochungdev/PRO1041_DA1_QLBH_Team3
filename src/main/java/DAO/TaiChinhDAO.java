//Hung 
package DAO;

import Utils.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaiChinhDAO {

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

    public List<Object[]> getDoanhThu() {
        String sql = "select*from View_DoanhThu";
        String[] tenCot = {"Thang", "TongSoTienNhap", "TongSoTienXuat", "SanPhamBanChay", "SanPhamBanIt", "TongDoanhThu"};
        return this.getListOfArray(sql, tenCot);
    }

    public List<Object[]> searchCongNo(String keyword) {
        String sql = "SELECT * FROM View_CongNo WHERE MaHD LIKE ? OR MaKH LIKE ? OR TenKH LIKE ? OR SoDienThoai LIKE ?";
        String[] tenCot = {"MaHD", "MaKH", "TenKH", "SoDienThoai", "SoTien", "NgayNo", "TrangThaiThanhToan"};

        String searchPattern = "%" + keyword + "%"; // Tìm kiếm theo từ khóa chứa trong chuỗi

        return this.getListOfArray(sql, tenCot, searchPattern, searchPattern, searchPattern, searchPattern);
    }

    public void capNhatTrangThaiThanhToan(String maHD, boolean daThanhToan) {
        String sql;
        if (daThanhToan) {
            // Cập nhật hóa đơn thành đã thanh toán, ghi nhận ngày thanh toán
            sql = "UPDATE HoaDon SET NgayThanhToan = GETDATE() WHERE MaHD = ?";
        } else {
            // Cập nhật hóa đơn thành chưa thanh toán, xóa ngày thanh toán
            sql = "UPDATE HoaDon SET NgayThanhToan = NULL WHERE MaHD = ?";
        }
        JdbcHelper.execUpdate(sql, maHD);
    }

    public List<Object[]> getCongNo(String trangThai) {
        String sql;
        String[] tenCot = {"MaHD", "MaKH", "TenKH", "SoDienThoai", "SoTien", "NgayNo", "TrangThaiThanhToan"};

        if ("Đã thanh toán".equals(trangThai)) {
            sql = "SELECT * FROM View_CongNo WHERE TrangThaiThanhToan NOT LIKE N'Chưa thanh toán'";
        } else if ("Chưa thanh toán".equals(trangThai)) {
            sql = "SELECT * FROM View_CongNo WHERE TrangThaiThanhToan = N'Chưa thanh toán'";
        } else {
            sql = "SELECT * FROM View_CongNo"; // Nếu không lọc, lấy tất cả
        }
        return this.getListOfArray(sql, tenCot);
    }

}
