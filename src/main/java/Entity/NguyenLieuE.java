package Entity;

import java.math.BigDecimal;
import java.util.Date;

public class NguyenLieuE {
    private int MaNL;
    private String TenNL;
    private int SoLuongNhap;
    private int SoLuongDaBan;
    private int SoLuongConLai;
    private String DonViTinh;
    private Date NgayNhap;
    private Date HanSuDung;
    private String NhaCungCap;
    private BigDecimal GiaNhap;

    public NguyenLieuE() {
    }

    public NguyenLieuE(int MaNL, String TenNL, int SoLuongNhap, int SoLuongDaBan, int SoLuongConLai, String DonViTinh, Date NgayNhap, Date HanSuDung, String NhaCungCap, BigDecimal GiaNhap) {
        this.MaNL = MaNL;
        this.TenNL = TenNL;
        this.SoLuongNhap = SoLuongNhap;
        this.SoLuongDaBan = SoLuongDaBan;
        this.SoLuongConLai = SoLuongConLai;
        this.DonViTinh = DonViTinh;
        this.NgayNhap = NgayNhap;
        this.HanSuDung = HanSuDung;
        this.NhaCungCap = NhaCungCap;
        this.GiaNhap = GiaNhap;
    }

    

    public int getMaNL() {
        return MaNL;
    }

    public void setMaNL(int maNL) {
        MaNL = maNL;
    }

    public String getTenNL() {
        return TenNL;
    }

    public void setTenNL(String tenNL) {
        TenNL = tenNL;
    }

    public int getSoLuongNhap() {
        return SoLuongNhap;
    }

    public void setSoLuongNhap(int soLuongNhap) {
        SoLuongNhap = soLuongNhap;
    }

    public int getSoLuongDaBan() {
        return SoLuongDaBan;
    }

    public void setSoLuongDaBan(int soLuongDaBan) {
        SoLuongDaBan = soLuongDaBan;
    }

    public int getSoLuongConLai() {
        return SoLuongConLai;
    }

    public void setSoLuongConLai(int soLuongConLai) {
        SoLuongConLai = soLuongConLai;
    }

    public String getDonViTinh() {
        return DonViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        DonViTinh = donViTinh;
    }

    public Date getNgayNhap() {
        return NgayNhap;
    }

    public void setNgayNhap(Date ngayNhap) {
        NgayNhap = ngayNhap;
    }

    public Date getHanSuDung() {
        return HanSuDung;
    }

    public void setHanSuDung(Date hanSuDung) {
        HanSuDung = hanSuDung;
    }

    public String getNhaCungCap() {
        return NhaCungCap;
    }

    public void setNhaCungCap(String nhaCungCap) {
        NhaCungCap = nhaCungCap;
    }

    public BigDecimal getGiaNhap() {
        return GiaNhap;
    }

    public void setGiaNhap(BigDecimal GiaNhap) {
        this.GiaNhap = GiaNhap;
    }
    
}