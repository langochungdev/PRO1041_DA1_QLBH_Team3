// M.Kha
package DAO;

import Entity.CaLamE;
import Entity.NhanVienE;
import Utils.JdbcHelper;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CaLamDAO extends MainDAO<CaLamE, String>{

    String INSERT_SQL = "INSERT INTO CaLam (MaNV, TenNV, NgayLam, CaLam) VALUES (?, ?, ?, ?)";
    String UPDATE_SQL = "UPDATE CaLam SET TenNV=?, NgayLam=?, CaLam=? WHERE MaNV=? AND NgayLam=?";
    String DELETE_SQL = "DELETE FROM CaLam WHERE MaNV=? AND NgayLam=? AND CaLam=?";
    String SELECT_ALL_SQL = "SELECT * FROM CaLam";
    String SELECT_BY_TenNV_SQL = "SELECT * FROM CaLam WHERE TenNhanVien=?";

    public void insert(CaLamE e) {
        JdbcHelper.execUpdate(INSERT_SQL,
                e.getMaNV(),
                e.getTenNV(),
                e.getNgayLam(),
                e.getCaLam()
        );
    }

    public void update(CaLamE e) {
        JdbcHelper.execUpdate(UPDATE_SQL,
                e.getTenNV(),
                e.getNgayLam(),
                e.getCaLam(),
                e.getMaNV()
        );
    }
    
    
    public void delete(int maNV, Date ngayLam) {
        try {
            JdbcHelper.execUpdate(DELETE_SQL, maNV, new java.sql.Date(ngayLam.getTime()));
            System.out.println("Xóa thành công!");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Xóa thất bại!");
        }

    }

    public List<CaLamE> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    public CaLamE selectByTenTK(String tenNV) {
        List<CaLamE> ds = selectBySql(SELECT_BY_TenNV_SQL, tenNV);
        if (ds.isEmpty()) {
            return null;
        }
        return ds.get(0);
    }

    public List<CaLamE> selectBySql(String sql, Object... args) {
        List<CaLamE> ds = new ArrayList<>();
        ResultSet rs = JdbcHelper.execQuery(sql, args);
        try {
            while (rs.next()) {
                CaLamE e = new CaLamE();
                e.setMaNV(rs.getString("MaNV"));
                e.setTenNV(rs.getString("TenNV"));
                Date sqlDate = rs.getDate("NgayLam");
                e.setCaLam(rs.getInt("CaLam"));
                ds.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return ds;
    }

    @Override
    public void delete(String e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CaLamE selectById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
