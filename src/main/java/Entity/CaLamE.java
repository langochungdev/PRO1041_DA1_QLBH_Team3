// M.Kha
package Entity;

import java.time.LocalDate;

public class CaLamE {
    private String maNV;
    private String maCa;
    private String tenNV;
    private LocalDate ngayLam;
    private String caLam; // Chỉ nhận giá trị 1, 2, 3

    public CaLamE(){
        
    }

    public CaLamE(String maNV, String maCa, String tenNV, LocalDate ngayLam, String caLam) {
        this.maNV = maNV;
        this.maCa = maCa;
        this.tenNV = tenNV;
        this.ngayLam = ngayLam;
        this.caLam = caLam;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaCa() {
        return maCa;
    }

    public void setMaCa(String maCa) {
        this.maCa = maCa;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public LocalDate getNgayLam() {
        return ngayLam;
    }

    public void setNgayLam(LocalDate ngayLam) {
        this.ngayLam = ngayLam;
    }

    public String getCaLam() {
        return caLam;
    }

    public void setCaLam(String caLam) {
        this.caLam = caLam;
    }
    
    

    
}

