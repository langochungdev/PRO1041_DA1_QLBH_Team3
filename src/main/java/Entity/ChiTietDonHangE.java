
package Entity;

import java.math.BigDecimal;


public class ChiTietDonHangE {
    private String maHD;
    private String maNL;
    private String tenSanPham;
    private int soLuong;
    private String donViTinh;
    private BigDecimal giaXuat;
    private BigDecimal thanhTien;
    public ChiTietDonHangE() {
    }

    public ChiTietDonHangE(String maHD, String maNL, String tenSanPham, int soLuong, String donViTinh, BigDecimal giaXuat, BigDecimal thanhTien) {
        this.maHD = maHD;
        this.maNL = maNL;
        this.tenSanPham = tenSanPham;
        this.soLuong = soLuong;
        this.donViTinh = donViTinh;
        this.giaXuat = giaXuat;
        this.thanhTien = thanhTien;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaNL() {
        return maNL;
    }

    public void setMaNL(String maNL) {
        this.maNL = maNL;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public BigDecimal getGiaXuat() {
        return giaXuat;
    }

    public void setGiaXuat(BigDecimal giaXuat) {
        this.giaXuat = giaXuat;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    
    
}
