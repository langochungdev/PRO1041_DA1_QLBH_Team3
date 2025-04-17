
package Entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
public class HoaDonE {
    private String maHD;
    private String maKH;
    private String maNV;
    private Date ngayDatHang;
    private String trangThai;
    private Date ngayThanhToan;
    private BigDecimal tongTien;
    public HoaDonE() {
    }

    public HoaDonE(String maHD, String maKH, String maNV, Date ngayDatHang, String trangThai, Date ngayThanhToan, BigDecimal tongTien) {
        this.maHD = maHD;
        this.maKH = maKH;
        this.maNV = maNV;
        this.ngayDatHang = ngayDatHang;
        this.trangThai = trangThai;
        this.ngayThanhToan = ngayThanhToan;
        this.tongTien = tongTien;
        
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public Date getNgayDatHang() {
        return ngayDatHang;
    }

    public void setNgayDatHang(Date ngayDatHang) {
        this.ngayDatHang = ngayDatHang;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Date getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(Date ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    

    
}

