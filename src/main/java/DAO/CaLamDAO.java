// M.Kha
package DAO;

import Entity.CaLamE;
import Utils.JdbcHelper;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CaLamDAO extends MainDAO<CaLamE, String> {

    final String INSERT_SQL = "INSERT INTO CaLam (MaCa, MaNV, NgayLam, CaLam) VALUES (?, ?, ?, ?)";
    final String UPDATE_SQL = "UPDATE CaLam SET MaNV = ?, TenNV = ?, NgayLam = ?, CaLam = ? WHERE MaCa = ?";
    final String DELETE_SQL = "DELETE FROM CaLam WHERE MaCa = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM v_CaLam";
    final String SELECT_BY_ID_SQL = "SELECT * FROM v_CaLam WHERE MaCa = ?";
    final String SELECT_BY_MANV_SQL = "SELECT * FROM v_CaLam WHERE MaNV = ?";
    final String SELECT_BY_DATE_SQL = "SELECT * FROM v_CaLam WHERE NgayLam = ?";
    final String SELECT_BY_DATE_MANV_SQL = "SELECT * FROM v_CaLam WHERE NgayLam = ? AND MaNV = ?";

    public void insert(CaLamE e) {
        JdbcHelper.execUpdate(INSERT_SQL,
                e.getMaCa(),
                e.getMaNV(),
                java.sql.Date.valueOf(e.getNgayLam()), // Đúng cách chuyển từ LocalDate sang SQL Date
                e.getCaLam()
        );
    }

    public void update(CaLamE e) {
        JdbcHelper.execUpdate(UPDATE_SQL,
                e.getMaNV(),
                e.getTenNV(),
                java.sql.Date.valueOf(e.getNgayLam()),
                e.getCaLam(),
                e.getMaCa()
        );
    }

    @Override
    public void delete(String maCa) {
        JdbcHelper.execUpdate(DELETE_SQL, maCa);
    }

    @Override
    public List<CaLamE> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public CaLamE selectById(String maCa) {
        List<CaLamE> list = selectBySql(SELECT_BY_ID_SQL, maCa);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<CaLamE> selectByMaNV(String maNV) {
        return selectBySql(SELECT_BY_MANV_SQL, maNV);
    }

    public List<CaLamE> selectByNgay(Date ngay) {
        return selectBySql(SELECT_BY_DATE_SQL, ngay);
    }

    public List<CaLamE> selectByNgayVaMaNV(Date ngay, String maNV) {
        return selectBySql(SELECT_BY_DATE_MANV_SQL, ngay, maNV);
    }

// Kiểm tra đã đăng ký bất kỳ ca nào trong ngày chưa
    public boolean kiemTraDaDangKyNgay(String maNV, LocalDate ngayLam) {
        String sql = "SELECT COUNT(*) FROM CaLam WHERE MaNV = ? AND NgayLam = ?";
        try (ResultSet rs = JdbcHelper.execQuery(sql, maNV, java.sql.Date.valueOf(ngayLam))) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void xoaTatCaCaLam() {
        String sql = "DELETE FROM CaLam"; // Xóa tất cả các bản ghi trong bảng CaLam
        JdbcHelper.execUpdate(sql); // Thực thi câu lệnh SQL
    }

    @Override
    public List<CaLamE> selectBySql(String sql, Object... args) {
        List<CaLamE> list = new ArrayList<>();
        try (ResultSet rs = JdbcHelper.execQuery(sql, args)) {
            while (rs.next()) {
                CaLamE e = new CaLamE();
                e.setMaCa(rs.getString("MaCa"));
                e.setMaNV(rs.getString("MaNV"));
                e.setTenNV(rs.getString("TenNhanVien"));
                e.setNgayLam(rs.getDate("NgayLam").toLocalDate());
                e.setCaLam(rs.getString("CaLam"));
                list.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Lỗi truy vấn dữ liệu CaLam: " + ex.getMessage(), ex);
        }
        return list;
    }
}
