package DAO;

import Entity.NguyenLieuE;
import Utils.JdbcHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NguyenLieuDAO extends MainDAO<NguyenLieuE, String> { 
    String INSERT_SQL = "INSERT INTO NguyenLieu (MaNL, TenNL, SoLuongTon, DonViTinh, NgayNhap, HanSuDung, MaNCC, GiaNhap, GiaXuat) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String UPDATE_SQL = "UPDATE NguyenLieu SET TenNL=?, MaNCC=?, NgayNhap=?, HanSuDung=?, SoLuongTon=?, DonViTinh=?, GiaNhap=?, GiaXuat=? WHERE MaNL=?";
    String DELETE_SQL = "DELETE FROM NguyenLieu WHERE MaNL=?";
    String SELECT_ALL_SQL = "SELECT * FROM NguyenLieu";
    String SELECT_BY_ID_SQL = "SELECT * FROM NguyenLieu WHERE MaNL=?";

    @Override
    public void insert(NguyenLieuE e) {
        JdbcHelper.execUpdate(INSERT_SQL,
                e.getMaNL(),
                e.getTenNL(),
                e.getSoLuongTon(),
                e.getDonViTinh(),
                e.getNgayNhap(),
                e.getHanSuDung(),
                e.getMaNCC(),
                e.getGiaNhap(),
                e.getGiaXuat()
        );
    }

    @Override
    public void update(NguyenLieuE e) {
        JdbcHelper.execUpdate(UPDATE_SQL,
                e.getTenNL(),
                e.getMaNCC(),
                e.getNgayNhap(),
                e.getHanSuDung(),
                e.getSoLuongTon(),
                e.getDonViTinh(),
                e.getGiaNhap(),  
                e.getGiaXuat(),
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
    public List<NguyenLieuE> selectBySql(String sql, Object...args){
        List<NguyenLieuE> ds = new ArrayList<>();
        ResultSet rs = JdbcHelper.execQuery(sql, args);
        try {
            while(rs.next()){
                NguyenLieuE e = new NguyenLieuE();
                e.setMaNL(rs.getString("MaNL"));
                e.setTenNL(rs.getString("TenNL"));
                e.setMaNCC(rs.getString("MaNCC"));
                e.setNgayNhap(rs.getDate("NgayNhap"));
                e.setHanSuDung(rs.getDate("HanSuDung"));
                e.setSoLuongTon(rs.getInt("SoLuongTon"));
                e.setDonViTinh(rs.getString("DonViTinh"));
                e.setGiaNhap(rs.getFloat("GiaNhap"));
                e.setGiaXuat(rs.getFloat("GiaXuat"));
                ds.add(e);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        };
        return ds;
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
    
    // Hải thêm vào
    // 1. Hàm phục vụ ComboBox: chỉ lấy MaNL và TenNL
public List<NguyenLieuE> selectTenVaMaNL() {
    List<NguyenLieuE> list = new ArrayList<>();
    String sql = "SELECT MaNL, TenNL FROM NguyenLieu";

    try (ResultSet rs = JdbcHelper.execQuery(sql)) {
        while (rs.next()) {
            NguyenLieuE nl = new NguyenLieuE();
            nl.setMaNL(rs.getString("MaNL"));
            nl.setTenNL(rs.getString("TenNL"));
            list.add(nl);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

// 2. Hàm lấy MaNL từ TenNL
public String getMaNLByTen(String tenNL) {
    String sql = "SELECT MaNL FROM NguyenLieu WHERE TenNL = ?";
    try (ResultSet rs = JdbcHelper.execQuery(sql, tenNL)) {
        if (rs.next()) {
            return rs.getString("MaNL");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

}
