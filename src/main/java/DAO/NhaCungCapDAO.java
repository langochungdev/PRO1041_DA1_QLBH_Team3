package DAO;

import Entity.NhaCungCapE;
import Utils.JdbcHelper;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO extends MainDAO<NhaCungCapE, String> {
    
    String INSERT_SQL = "INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, SoDienThoai) VALUES (?, ?, ?, ?)";
    String UPDATE_SQL = "UPDATE NhaCungCap SET TenNCC=?, DiaChi=?, SoDienThoai=? WHERE MaNCC=?";
    String DELETE_SQL = "DELETE FROM NhaCungCap WHERE MaNCC=?";
    String SELECT_ALL_SQL = "SELECT * FROM NhaCungCap";
    String SELECT_BY_ID_SQL = "SELECT * FROM NhaCungCap WHERE MaNCC=?";

    @Override
    public void insert(NhaCungCapE e) {
        JdbcHelper.execUpdate(INSERT_SQL,
                e.getMaNCC(),
                e.getTenNCC(),
                e.getDiaChi(),
                e.getSDT()
        );
    }

    @Override
    public void update(NhaCungCapE e) {
        JdbcHelper.execUpdate(UPDATE_SQL,
                e.getTenNCC(),
                e.getDiaChi(),
                e.getSDT(),
                e.getMaNCC()
        );
    }

    @Override
    public void delete(String id) {
        JdbcHelper.execUpdate(DELETE_SQL, id);
    }

    @Override
    public List<NhaCungCapE> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public NhaCungCapE selectById(String id) {
        List<NhaCungCapE> ds = selectBySql(SELECT_BY_ID_SQL, id);
        if (ds.isEmpty()) {
            return null;
        }
        return ds.get(0);
    }

    @Override
    public List<NhaCungCapE> selectBySql(String sql, Object... args) {
        List<NhaCungCapE> ds = new ArrayList<>();
        ResultSet rs = JdbcHelper.execQuery(sql, args);
        try {
            while (rs.next()) {
                NhaCungCapE e = new NhaCungCapE();
                e.setMaNCC(rs.getString("MaNCC"));
                e.setTenNCC(rs.getString("TenNCC"));
                e.setDiaChi(rs.getString("DiaChi"));
                e.setSDT(rs.getString("SoDienThoai"));
                ds.add(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        };
        return ds;
    }
    
    public boolean isNCCInUse(String maNCC) {
    String sql = "SELECT COUNT(*) FROM NguyenLieu WHERE MaNCC = ?";
    try {
        ResultSet rs = JdbcHelper.execQuery(sql, maNCC);
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}

}
