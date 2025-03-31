
package Entity;


public class KhachHangE {
     private String maKhachHang;
    private String diaChi;

    public KhachHangE() {
    }

    public KhachHangE(String maKhachHang, String diaChi) {
        this.maKhachHang = maKhachHang;
        this.diaChi = diaChi;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
    
    
}
