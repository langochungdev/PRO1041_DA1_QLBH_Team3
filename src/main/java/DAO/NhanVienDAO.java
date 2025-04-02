// Hung
package DAO;

import Entity.NhanVienE;
import Utils.JdbcHelper;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO extends MainDAO<NhanVienE, String> {

    String INSERT_SQL = "INSERT INTO NhanVien (MaNV, MatKhau, Email, VaiTro, HoTen) VALUES (?, ?, ?, ?, ?)";
    String UPDATE_SQL = "UPDATE NhanVien SET MatKhau=?, HoTen=?, VaiTro=?, Email=? WHERE MaNV=?";
    String DELETE_SQL = "DELETE FROM NhanVien WHERE MaNV=?";
    String SELECT_ALL_SQL = "SELECT * FROM NhanVien";
    String SELECT_BY_ID_SQL = "SELECT * FROM NhanVien WHERE MaNV=?";

    @Override
    public void insert(NhanVienE e) {
        JdbcHelper.execUpdate(INSERT_SQL,
                e.getMaNV(),
                e.getMatKhau(),
                e.getEmail(),
                e.isVaiTro(),
                e.getHoTen()
        );
    }

    @Override
    public void update(NhanVienE e) {
        JdbcHelper.execUpdate(UPDATE_SQL,
                e.getMatKhau(),
                e.getHoTen(),
                e.isVaiTro(),
                e.getEmail(),
                e.getMaNV()
        );
    }

    @Override
    public void delete(String id) {
        JdbcHelper.execUpdate(DELETE_SQL, id);
    }

    @Override
    public List<NhanVienE> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public NhanVienE selectById(String id) {
        List<NhanVienE> ds = selectBySql(SELECT_BY_ID_SQL, id);
        if (ds.isEmpty()) {
            return null;
        }
        return ds.get(0);
    }

    @Override
    public List<NhanVienE> selectBySql(String sql, Object... args) {
        List<NhanVienE> ds = new ArrayList<>();
        ResultSet rs = JdbcHelper.execQuery(sql, args);
        try {
            while (rs.next()) {
                NhanVienE e = new NhanVienE();
                e.setMaNV(rs.getString("MaNV"));
                e.setMatKhau(rs.getString("MatKhau"));
                e.setEmail(rs.getString("Email"));
                e.setHoTen(rs.getString("HoTen"));
                e.setVaiTro(rs.getBoolean("VaiTro"));
                ds.add(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        };
        return ds;
    }
}
