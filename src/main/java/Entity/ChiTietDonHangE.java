
package Entity;

import java.math.BigDecimal;


public class ChiTietDonHangE {
    private String tenSanPham;
    private String donViTinh;
    private int soLuong;
    private BigDecimal giaBan; // Kiểu Decimal

    public ChiTietDonHangE() {
    }


    public ChiTietDonHangE(String tenSanPham, String donViTinh, int soLuong, BigDecimal giaBan) {
        
        this.tenSanPham = tenSanPham;
        this.donViTinh = donViTinh;
        this.soLuong = soLuong;
        this.giaBan = giaBan;
    }

    

    

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    
}
