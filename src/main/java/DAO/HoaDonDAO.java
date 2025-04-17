package DAO;

import Entity.HoaDonE;
import Utils.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO extends MainDAO<HoaDonE, String> {
    final String INSERT_SQL = "INSERT INTO HoaDon (MaHD, MaKH, NgayDatHang, NgayThanhToan, TrangThai, MaNV) VALUES (?, ?, ?, ?, ?, ?)";
    final String UPDATE_SQL = "UPDATE HoaDon SET MaKH=?, NgayDatHang=?, NgayThanhToan=?, TrangThai=?, MaNV=? WHERE MaHD=?";
    final String DELETE_SQL = "DELETE FROM HoaDon WHERE MaHD=?";
    final String SELECT_ALL_SQL = "SELECT * FROM HoaDon";
    final String SELECT_BY_ID_SQL = "SELECT * FROM HoaDon WHERE MaHD=?";

    @Override
    public void insert(HoaDonE e) {
        JdbcHelper.execUpdate(INSERT_SQL,
                e.getMaHD(), e.getMaKH(), e.getNgayDatHang(), e.getNgayThanhToan(),
                e.getTrangThai(), e.getMaNV());
    }

    @Override
    public void update(HoaDonE e) {
        JdbcHelper.execUpdate(UPDATE_SQL,
                e.getMaKH(), e.getNgayDatHang(), e.getNgayThanhToan(),
                e.getTrangThai(), e.getMaNV(), e.getMaHD());
    }

    @Override
    public void delete(String maHD) {
        // Xóa chi tiết hóa đơn trước (nếu cần)
        JdbcHelper.execUpdate("DELETE FROM ChiTietHoaDon WHERE MaHD=?", maHD);
        JdbcHelper.execUpdate(DELETE_SQL, maHD);
    }

    @Override
    public HoaDonE selectById(String maHD) {
        List<HoaDonE> list = selectBySql(SELECT_BY_ID_SQL, maHD);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<HoaDonE> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public List<HoaDonE> selectBySql(String sql, Object... args) {
        List<HoaDonE> list = new ArrayList<>();
        try (ResultSet rs = JdbcHelper.execQuery(sql, args)) {
            while (rs.next()) {
                HoaDonE e = new HoaDonE();
                e.setMaHD(rs.getString("MaHD"));
                e.setMaKH(rs.getString("MaKH"));
                e.setMaNV(rs.getString("MaNV"));
                e.setNgayDatHang(rs.getDate("NgayDatHang"));
                e.setNgayThanhToan(rs.getDate("NgayThanhToan"));
                e.setTrangThai(rs.getString("TrangThai"));
                e.setTongTien(rs.getBigDecimal("TongTien")); // Trigger xử lý
                list.add(e);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return list;
    }

    // ✅ Hàm lọc theo trạng thái
    public List<HoaDonE> selectByTrangThai(String trangThai) {
        String sql = "SELECT * FROM HoaDon WHERE TrangThai = ?";
        return selectBySql(sql, trangThai);
    }

    // ✅ Lấy view hóa đơn hiển thị cùng tên KH và SDT
    public List<Object[]> selectHoaDonView() {
        String sql = """
            SELECT h.MaHD, kh.TenKH, kh.SoDienThoai, kh.Email, h.NgayDatHang, h.NgayThanhToan, h.TrangThai, h.TongTien
            FROM HoaDon h
            JOIN KhachHang kh ON h.MaKH = kh.MaKH
        """;
        List<Object[]> list = new ArrayList<>();
        try (ResultSet rs = JdbcHelper.execQuery(sql)) {
            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaHD"),
                    rs.getString("TenKH"),
                    rs.getString("SoDienThoai"),
                    rs.getString("Email"),
                    rs.getDate("NgayDatHang"),
                    rs.getDate("NgayThanhToan"),
                    rs.getString("TrangThai"),
                    rs.getBigDecimal("TongTien")
                };
                list.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}