package Entity;

public class NhanVienE {
    private String maNV;
    private String tenTK;
    private String matKhau;
    private boolean vaiTro;
    private String email;
    private String hoTen;
    public NhanVienE(){
    }

    public NhanVienE(String maNV, String tenTK, String matKhau, boolean vaiTro, String email, String hoTen) {
        this.maNV = maNV;
        this.tenTK = tenTK;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.email = email;
        this.hoTen = hoTen;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenTK() {
        return tenTK;
    }

    public void setTenTK(String tenTK) {
        this.tenTK = tenTK;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public boolean isVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(boolean vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

   
    @Override
    public String toString() {
        return this.hoTen;
    }
    
}
