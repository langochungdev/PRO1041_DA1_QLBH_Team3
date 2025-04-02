package Entity;

public class NhaCungCapE {
    private String maNCC;    
    private String tenNCC;
    private String diaChi;
    private String SDT;

    public NhaCungCapE(String maNCC, String tenNCC, String diaChi, String SDT) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.diaChi = diaChi;
        this.SDT = SDT;
    }

    public NhaCungCapE() {
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }
    
    
}
