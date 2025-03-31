
package Entity;

import java.util.Date;
import java.util.List;
public class DonHangE {
    private String maHD;
    private String tenKH;
    private String soDienThoai;
    private Date ngayDatHang;
    private Date ngayThanhToan;
    private String trangThai;
    private String diaChi;
    private String tenSP;
    public DonHangE() {
    }

    public DonHangE(String maHD, String tenKH, String soDienThoai, Date ngayDatHang, Date ngayThanhToan, String trangThai, String diaChi, String tenSP) {
        this.maHD = maHD;
        this.tenKH = tenKH;
        this.soDienThoai = soDienThoai;
        this.ngayDatHang = ngayDatHang;
        this.ngayThanhToan = ngayThanhToan;
        this.trangThai = trangThai;
        this.diaChi = diaChi;
        this.tenSP = tenSP;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public Date getNgayDatHang() {
        return ngayDatHang;
    }

    public void setNgayDatHang(Date ngayDatHang) {
        this.ngayDatHang = ngayDatHang;
    }

    public Date getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(Date ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }
    
    
}

