package DAO;

import Entity.KhachHangE;
import Utils.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO extends MainDAO<KhachHangE, String> {
        String INSERT_SQL = "INSERT INTO KhachHang (MaKH, TenKH, SoDienThoai, DiaChi, Email) VALUES (?, ?, ?, ?, ?)";
        String UPDATE_SQL = "UPDATE KhachHang SET TenKH=?, SoDienThoai=?, DiaChi=?, Email=? WHERE MaKH=?";
        String DELETE_SQL = "DELETE FROM KhachHang WHERE MaKH=?";
        String SELECT_ALL_SQL = "SELECT * FROM KhachHang";
        String SELECT_BY_ID_SQL = "SELECT * FROM KhachHang WHERE MaKH=?";

    @Override
    public void insert(KhachHangE e) {
    JdbcHelper.execUpdate(INSERT_SQL, e.getMaKH(), e.getTenKH(), e.getSoDienThoai(), e.getDiaChi(), e.getEmail());
    }

    @Override
    public void update(KhachHangE e) {
    JdbcHelper.execUpdate(UPDATE_SQL, e.getTenKH(), e.getSoDienThoai(), e.getDiaChi(), e.getEmail(), e.getMaKH());
    }

    @Override
    public void delete(String maKH) {
        JdbcHelper.execUpdate(DELETE_SQL, maKH);
    }

    @Override
    public KhachHangE selectById(String maKH) {
        List<KhachHangE> list = selectBySql(SELECT_BY_ID_SQL, maKH);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<KhachHangE> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public List<KhachHangE> selectBySql(String sql, Object... args) {
        List<KhachHangE> list = new ArrayList<>();
        try (ResultSet rs = JdbcHelper.execQuery(sql, args)) {
            while (rs.next()) {
                KhachHangE e = new KhachHangE();
                e.setMaKH(rs.getString("MaKH"));
                e.setTenKH(rs.getString("TenKH"));
                e.setSoDienThoai(rs.getString("SoDienThoai"));
                e.setDiaChi(rs.getString("DiaChi"));
                e.setEmail(rs.getString("Email"));
                list.add(e);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return list;
    }
}
