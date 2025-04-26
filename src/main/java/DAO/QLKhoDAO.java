package DAO;

import Utils.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QLKhoDAO {

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

    public List<Object[]> getqlKho() {
        String sql = "select*from qlkho";
        String[] tenCot = {"MaNL", "TenNL", "SoLuongNhap", "SoLuongDaBan", "SoLuongConLai", "DonViTinh", "NgayNhap", "HanSuDung", "NhaCungCap", "GiaNhap", "GiaXuat"};
        return this.getListOfArray(sql, tenCot);
    }

    public void deleteNguyenLieu(String maNL) {
        String sql = "DELETE FROM NguyenLieu WHERE MaNL = ?";
        try {
            JdbcHelper.execUpdate(sql, maNL);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa nguyên liệu: " + e.getMessage(), e);
        }
    }

    public boolean isNguyenLieuDaDuocBan(String maNL) {
        String sql = "SELECT COUNT(*) FROM ChiTietHoaDon WHERE MaNL = ?";
        try {
            ResultSet rs = JdbcHelper.execQuery(sql, maNL);
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Nếu count > 0 nghĩa là đã có bán
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi kiểm tra nguyên liệu đã bán: " + e.getMessage(), e);
        }
        return false;
    }

    public void insertNguyenLieu(String maNL, String tenNL, String maNCC, Date ngayNhap, Date hanSuDung,
            int soLuongTon, String donViTinh, double giaNhap, double giaXuat) {
        String sql = "INSERT INTO NguyenLieu (MaNL, TenNL, MaNCC, NgayNhap, HanSuDung, SoLuongTon, DonViTinh, GiaNhap, GiaXuat) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            JdbcHelper.execUpdate(sql, maNL, tenNL, maNCC, ngayNhap, hanSuDung, soLuongTon, donViTinh, giaNhap, giaXuat);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm nguyên liệu: " + e.getMessage(), e);
        }
    }

    public Integer getSoLuongConLai(String maNL) {
        String sql = "SELECT SoLuongConLai FROM qlKho WHERE MaNL = ?";
        try (
                ResultSet rs = JdbcHelper.execQuery(sql, maNL)) {
            if (rs.next()) {
                return rs.getInt("SoLuongConLai");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi lấy số lượng còn lại: " + e.getMessage(), e);
        }
        return null; // hoặc trả về 0 nếu muốn mặc định
    }

    public List<Object[]> searchByKeyword(String keyword) {
        String sql = "SELECT * FROM qlKho WHERE TenNL LIKE ? OR MaNL LIKE ? OR NhaCungCap LIKE ?";
        String[] cols = {"MaNL", "TenNL", "SoLuongNhap", "SoLuongDaBan", "SoLuongConLai", "DonViTinh", "NgayNhap", "HanSuDung", "NhaCungCap", "GiaNhap", "GiaXuat"};
        String like = "%" + keyword + "%";
        return getListOfArray(sql, cols, like, like, like);
    }

}
