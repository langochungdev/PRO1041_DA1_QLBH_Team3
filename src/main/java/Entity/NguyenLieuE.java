package Entity;

import java.math.BigDecimal;
import java.util.Date;

public class NguyenLieuE {
    private String MaNL; 
    private String TenNL;
    private String MaNCC;
    private Date NgayNhap;
    private Date HanSuDung;
    private int SoLuongTon;
    private String DonViTinh;
    private float GiaNhap;
    private float GiaXuat;

    public NguyenLieuE() {
    }

    public NguyenLieuE(String MaNL, String TenNL, String MaNCC, Date NgayNhap, Date HanSuDung, int SoLuongTon, String DonViTinh, float GiaNhap, float GiaXuat) {
        this.MaNL = MaNL;
        this.TenNL = TenNL;
        this.MaNCC = MaNCC;
        this.NgayNhap = NgayNhap;
        this.HanSuDung = HanSuDung;
        this.SoLuongTon = SoLuongTon;
        this.DonViTinh = DonViTinh;
        this.GiaNhap = GiaNhap;
        this.GiaXuat = GiaXuat;
    }

    
    
    public String getMaNL() {
        return MaNL;
    }

    public void setMaNL(String MaNL) {
        this.MaNL = MaNL;
    }

    public String getTenNL() {
        return TenNL;
    }

    public void setTenNL(String TenNL) {
        this.TenNL = TenNL;
    }

    public String getMaNCC() {
        return MaNCC;
    }

    public void setMaNCC(String MaNCC) {
        this.MaNCC = MaNCC;
    }

    public Date getNgayNhap() {
        return NgayNhap;
    }

    public void setNgayNhap(Date NgayNhap) {
        this.NgayNhap = NgayNhap;
    }

    public Date getHanSuDung() {
        return HanSuDung;
    }

    public void setHanSuDung(Date HanSuDung) {
        this.HanSuDung = HanSuDung;
    }

    public int getSoLuongTon() {
        return SoLuongTon;
    }

    public void setSoLuongTon(int SoLuongTon) {
        this.SoLuongTon = SoLuongTon;
    }

    public String getDonViTinh() {
        return DonViTinh;
    }

    public void setDonViTinh(String DonViTinh) {
        this.DonViTinh = DonViTinh;
    }

    public float getGiaNhap() {
        return GiaNhap;
    }

    public void setGiaNhap(float GiaNhap) {
        this.GiaNhap = GiaNhap;
    }

    public float getGiaXuat() {
        return GiaXuat;
    }

    public void setGiaXuat(float GiaXuat) {
        this.GiaXuat = GiaXuat;
    }
    
    @Override
    public String toString() {
        return TenNL;  // Trả về tên nguyên liệu để hiển thị trong ComboBox
    }
    
}