package Entity;

import java.math.BigDecimal;
import java.util.Date;

public class NguyenLieuE {
    private String MaNL; // Đổi từ int thành String
    private String TenNL;
    private int SoLuongTon;
    private int SoLuongDaBan;
    private int SoLuongConLai;
    private String DonViTinh;
    private Date NgayNhap;
    private Date HanSuDung;
    private String NhaCungCap;
    private BigDecimal GiaNhap;
    
    // Constructor
    
    public NguyenLieuE() {
    }

    public NguyenLieuE(String MaNL, String TenNL, int SoLuongTon, int SoLuongDaBan, int SoLuongConLai, String DonViTinh, Date NgayNhap, Date HanSuDung, String NhaCungCap, BigDecimal GiaNhap) {
        this.MaNL = MaNL;
        this.TenNL = TenNL;
        this.SoLuongTon = SoLuongTon;
        this.SoLuongDaBan = SoLuongDaBan;
        this.SoLuongConLai = SoLuongConLai;
        this.DonViTinh = DonViTinh;
        this.NgayNhap = NgayNhap;
        this.HanSuDung = HanSuDung;
        this.NhaCungCap = NhaCungCap;
        this.GiaNhap = GiaNhap;
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

    public int getSoLuongTon() {
        return SoLuongTon;
    }

    public void setSoLuongTon(int SoLuongTon) {
        this.SoLuongTon = SoLuongTon;
    }

    public int getSoLuongDaBan() {
        return SoLuongDaBan;
    }

    public void setSoLuongDaBan(int SoLuongDaBan) {
        this.SoLuongDaBan = SoLuongDaBan;
    }

    public int getSoLuongConLai() {
        return SoLuongConLai;
    }

    public void setSoLuongConLai(int SoLuongConLai) {
        this.SoLuongConLai = SoLuongConLai;
    }

    public String getDonViTinh() {
        return DonViTinh;
    }

    public void setDonViTinh(String DonViTinh) {
        this.DonViTinh = DonViTinh;
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

    public String getNhaCungCap() {
        return NhaCungCap;
    }

    public void setNhaCungCap(String NhaCungCap) {
        this.NhaCungCap = NhaCungCap;
    }

    public BigDecimal getGiaNhap() {
        return GiaNhap;
    }

    public void setGiaNhap(BigDecimal GiaNhap) {
        this.GiaNhap = GiaNhap;
    }

    public void setGiaNhap(Double giaNhap) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    

}
