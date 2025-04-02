package DAO;

import Entity.NguyenLieuE;
import Utils.JdbcHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NguyenLieuDAO extends MainDAO<NguyenLieuE, String> { // Thay Integer thành String

    String INSERT_SQL = "INSERT INTO NguyenLieu (MaNL, TenNL, SoLuongNhap, SoLuongDaBan, SoLuongConLai, DonViTinh, NgayNhap, HanSuDung, NhaCungCap, GiaNhap) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String UPDATE_SQL = "UPDATE NguyenLieu SET TenNL=?, SoLuongNhap=?, SoLuongDaBan=?, SoLuongConLai=?, DonViTinh=?, NgayNhap=?, HanSuDung=?, NhaCungCap=?, GiaNhap=? WHERE MaNL=?";
    String DELETE_SQL = "DELETE FROM NguyenLieu WHERE MaNL=?";
    String SELECT_ALL_SQL = "SELECT * FROM View_NguyenLieu";
    String SELECT_BY_ID_SQL = "SELECT * FROM View_NguyenLieu WHERE MaNL=?";

    @Override
    public void insert(NguyenLieuE e) {
        JdbcHelper.execUpdate(INSERT_SQL,
                e.getMaNL(),
                e.getTenNL(),
                e.getSoLuongTon(),
                e.getSoLuongDaBan(),
                e.getSoLuongConLai(),
                e.getDonViTinh(),
                e.getNgayNhap(),
                e.getHanSuDung(),
                e.getNhaCungCap(),
                e.getGiaNhap()
        );
    }

    @Override
    public void update(NguyenLieuE e) {
        JdbcHelper.execUpdate(UPDATE_SQL,
                e.getTenNL(),
                e.getSoLuongTon(),
                e.getSoLuongDaBan(),
                e.getSoLuongConLai(),
                e.getDonViTinh(),
                e.getNgayNhap(),
                e.getHanSuDung(),
                e.getNhaCungCap(),
                e.getGiaNhap(),  // GiaNhap đứng trước MaNL
                e.getMaNL()      // MaNL để cuối cùng
        );
    }

    @Override
    public void delete(String id) { // Thay Integer thành String
        JdbcHelper.execUpdate(DELETE_SQL, id);
    }

    @Override
    public NguyenLieuE selectById(String id) { // Thay Integer thành String
        List<NguyenLieuE> list = this.selectBySql(SELECT_BY_ID_SQL, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<NguyenLieuE> selectAll() {
        return this.selectBySql(SELECT_ALL_SQL);
    }


    @Override
    public List<NguyenLieuE> selectBySql(String sql, Object... args) {
        List<NguyenLieuE> list = new ArrayList<>();
        try (ResultSet rs = JdbcHelper.execQuery(sql, args)) {
            while (rs.next()) {
                NguyenLieuE nl = new NguyenLieuE(
                        rs.getString("MaNL"),
                        rs.getString("TenNL"),
                        rs.getInt("SoLuongNhap"),
                        rs.getInt("SoLuongDaBan"),
                        rs.getInt("SoLuongConLai"),
                        rs.getString("DonViTinh"),
                        rs.getDate("NgayNhap"),
                        rs.getDate("HanSuDung"),
                        rs.getString("NhaCungCap"),
                        rs.getBigDecimal("GiaNhap")  // Lấy dữ liệu kiểu BigDecimal
                );
                list.add(nl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<NguyenLieuE> selectByKeyword(String keyword) {
        List<NguyenLieuE> list = new ArrayList<>();
        String sql = "SELECT * FROM View_NguyenLieu WHERE TenNL LIKE ?";

        System.out.println("Truy vấn SQL: " + sql + " với giá trị: %" + keyword + "%"); // Debug

        try (ResultSet rs = JdbcHelper.execQuery(sql, "%" + keyword + "%")) {
            while (rs.next()) {
                NguyenLieuE nl = new NguyenLieuE(
                        rs.getString("MaNL"),
                        rs.getString("TenNL"),
                        rs.getInt("SoLuongNhap"),
                        rs.getInt("SoLuongDaBan"),
                        rs.getInt("SoLuongConLai"),
                        rs.getString("DonViTinh"),
                        rs.getDate("NgayNhap"),
                        rs.getDate("HanSuDung"),
                        rs.getString("NhaCungCap"),
                        rs.getBigDecimal("GiaNhap")  // Thêm giá nhập
                );
                list.add(nl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    
    public NguyenLieuE getById(String maNL) {
        return this.selectById(maNL);
    }

  

    public List<NguyenLieuE> getAll() {
        return this.selectAll();
    }
    // Phương thức thêm nguyên liệu
    public void themNguyenLieu(NguyenLieuE nl) {
        try {
            insert(nl); // Gọi phương thức insert đã được định nghĩa
        } catch (Exception e) {
            System.out.println("Lỗi thêm nguyên liệu: " + e.getMessage());
            e.printStackTrace();
            // Xử lý lỗi theo nghiệp vụ của bạn (ví dụ: ném ngoại lệ tùy chỉnh)
        }
    }

    // Phương thức cập nhật nguyên liệu
    public void capNhatNguyenLieu(NguyenLieuE nl) {
        try {
            update(nl); // Gọi phương thức update đã được định nghĩa
        } catch (Exception e) {
            System.out.println("Lỗi cập nhật nguyên liệu: " + e.getMessage());
            e.printStackTrace();
            // Xử lý lỗi theo nghiệp vụ của bạn
        }
    }

    // Phương thức xóa nguyên liệu
    public void xoaNguyenLieu(String maNL) {
        try {
            delete(maNL); // Gọi phương thức delete đã được định nghĩa
        } catch (Exception e) {
            System.out.println("Lỗi xóa nguyên liệu: " + e.getMessage());
            e.printStackTrace();
            // Xử lý lỗi theo nghiệp vụ của bạn
        }
    }
}
