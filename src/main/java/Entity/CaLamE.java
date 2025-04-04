// M.Kha
package Entity;

import java.time.LocalDate;

public class CaLamE {
    private String maNV;
    private String tenNV;
    private LocalDate ngayLam;
    private int caLam; // Chỉ nhận giá trị 1, 2, 3

    public CaLamE() {
    }

    public CaLamE(String maNV, String tenNV, LocalDate ngayLam, int caLam) {
        this.maNV = maNV;
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

    public int getCaLam() {
        return caLam;
    }

    public void setCaLam(int caLam) {
        if (caLam >= 1 && caLam <= 3) {
            this.caLam = caLam;
        } else {
            throw new IllegalArgumentException("Ca làm phải là 1, 2 hoặc 3.");
        }
    }
}

