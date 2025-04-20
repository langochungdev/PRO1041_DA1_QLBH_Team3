/*FORM của HẢI*/
package UI;

import DAO.ChiTietDonHangDAO;
import DAO.HoaDonDAO;
import DAO.KhachHangDAO;
import DAO.NguyenLieuDAO;
import DAO.QLKhoDAO;
import Entity.ChiTietDonHangE;
import Entity.HoaDonE;
import Entity.KhachHangE;
import Entity.NguyenLieuE;
import Utils.Auth;
//import Utils.FilePDF;
import Utils.MailSender;
import Utils.MsgBox;
import Utils.PdfUtils;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class QLDonHangD extends javax.swing.JDialog {

    private String maNLSelected = null; // nguyên liệu đang chọn từ bảng
    DefaultTableModel modelHD;
    HoaDonDAO hoaDonDAO = new HoaDonDAO();
    ChiTietDonHangDAO chiTietDAO = new ChiTietDonHangDAO();
    KhachHangDAO khachHangDAO = new KhachHangDAO();
    NguyenLieuDAO nguyenLieuDAO = new NguyenLieuDAO();
    private String maHD;

    public QLDonHangD(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        modelHD = (DefaultTableModel) tblHoaDon.getModel();
        initTrangThaiCombo(); // Gán trạng thái
        fillTableHoaDon();    // Load bảng lần đầu
        phanQuyenNguoiDung();
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                locVaTimHoaDon();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                locVaTimHoaDon();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                locVaTimHoaDon();
            }
        });
        init();
    }
// hung 
    
    
    void init(){
        cboSanPham.addItemListener(new ItemListener() {
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            capNhatTonKho();
        }
    }
});
    }
    void capNhatTonKho() {
    NguyenLieuE nl = (NguyenLieuE) cboSanPham.getSelectedItem();
    QLKhoDAO khodao = new QLKhoDAO();
    if (nl != null) {
        Integer slCon = khodao.getSoLuongConLai(nl.getMaNL());
        if (slCon != null) {
            lbTonKho.setText("" + slCon);
        } else {
            lbTonKho.setText("Tồn kho: không xác định");
        }
    }
}

    
    
    
    
    
    public void selectTab(int index) {
        tabs.setSelectedIndex(index);
    }

    private void phanQuyenNguoiDung() {
        if (!Auth.isManager()) {
            btnXoaDH.setVisible(false);
        } else {
            btnXoaDH.setVisible(true);
        }
    }

    private String formatTien(BigDecimal soTien, boolean themVNĐ) {
        if (soTien == null) {
            return "0";
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat("#,##0.##", symbols);
        String tien = df.format(soTien);
        return themVNĐ ? tien + " VNĐ" : tien;
    }

    //xét đăng nhập
    boolean loGin() {
        if (Auth.user == null) {
            MsgBox.alert(this, "Bạn chưa đăng nhập. Vui lòng đăng nhập để thực hiện thao tác này.");
            return false;
        }
        return true;
    }

    private void capNhatTrangThaiNutTheoDonHang(String trangThai) {
        HoaDonE hd = hoaDonDAO.selectById(maHD);
        if (hd == null) {
            return;
        }
        if (hd.getTrangThai().equals("Đã giao")) {
            btnThem.setEnabled(false);
            btnXoa.setEnabled(false);
            btnCapNhat.setEnabled(false);
        } else {
            btnThem.setEnabled(true);
            btnXoa.setEnabled(true);
            btnCapNhat.setEnabled(true);
        }
    }

    private void initTrangThaiCombo() {
        cboTrangThai.removeAllItems();
        cboTrangThai.addItem("Tất cả");
        cboTrangThai.addItem("Chờ xử lý");
        cboTrangThai.addItem("Đã giao");
    }

    private void fillTableHoaDon() {
        modelHD.setRowCount(0); // clear bảng
        List<HoaDonE> list = hoaDonDAO.selectAll();
        for (HoaDonE hd : list) {
            KhachHangE kh = khachHangDAO.selectById(hd.getMaKH());

            Object[] row = new Object[]{
                hd.getMaHD(),
                kh.getTenKH(),
                kh.getSoDienThoai(),
                hd.getNgayDatHang(),
                hd.getNgayThanhToan(),
                hd.getTrangThai()
            };
            modelHD.addRow(row);
            tblHoaDon.setDefaultEditor(Object.class, null);  // Khóa chỉnh sửa trong bảng

        }
    }

    private void locVaTimHoaDon() {
        modelHD.setRowCount(0);
        String tim = txtSearch.getText().toLowerCase();
        String loc = cboTrangThai.getSelectedItem().toString();

        List<HoaDonE> list = hoaDonDAO.selectAll();
        for (HoaDonE hd : list) {
            KhachHangE kh = khachHangDAO.selectById(hd.getMaKH());
            boolean matchTim = tim.isEmpty()
                    || hd.getMaHD().toLowerCase().contains(tim)
                    || kh.getTenKH().toLowerCase().contains(tim)
                    || kh.getSoDienThoai().contains(tim);

            boolean matchTrangThai = loc.equals("Tất cả") || hd.getTrangThai().equalsIgnoreCase(loc);

            if (matchTim && matchTrangThai) {
                modelHD.addRow(new Object[]{
                    hd.getMaHD(),
                    kh.getTenKH(),
                    kh.getSoDienThoai(),
                    hd.getNgayDatHang(),
                    hd.getNgayThanhToan(),
                    hd.getTrangThai()
                });
            }
        }
    }
// Load chi tiết sản phẩm

    void fillChiTietHoaDon(String maHD) {
        DefaultTableModel modelCT = (DefaultTableModel) tblChiTietDonHang.getModel();
        modelCT.setRowCount(0);
        List<ChiTietDonHangE> list = chiTietDAO.selectByMaHD(maHD);
        BigDecimal tongTien = BigDecimal.ZERO;
        for (ChiTietDonHangE c : list) {
            BigDecimal tien = c.getGiaXuat().multiply(BigDecimal.valueOf(c.getSoLuong()));
            tongTien = tongTien.add(tien);
            modelCT.addRow(new Object[]{
                c.getTenSanPham(),
                c.getSoLuong(),
                c.getDonViTinh(),
                formatTien(c.getGiaXuat(), false),
                formatTien(c.getThanhTien(), false)
            });
        }
        tblChiTietDonHang.setDefaultEditor(Object.class, null);
        lblTongTien.setText(formatTien(tongTien, true));

        // Load thông tin hóa đơn
        HoaDonE hd = hoaDonDAO.selectById(maHD);
        if (hd != null && "Chờ xử lý".equalsIgnoreCase(hd.getTrangThai())) {
            btnCapNhat.setEnabled(true);
            btnXoa.setEnabled(true);
            btnThem.setEnabled(true); // Không cho thêm nếu đang chỉnh sửa
        } else {
            btnCapNhat.setEnabled(false);
            btnXoa.setEnabled(false);
        }

        KhachHangE kh = khachHangDAO.selectById(hd.getMaKH());
        if (kh == null) {
            return;
        }

        txtMaHD.setText(hd.getMaHD());
        txtMaHD.setEnabled(false);
        txtTenKH.setText(kh.getTenKH());
        txtSDT.setText(kh.getSoDienThoai());
        txtDiaChi.setText(kh.getDiaChi());
        dateNgayDH.setDate(hd.getNgayDatHang());
        dateNgayTT.setDate(hd.getNgayThanhToan());
        cbbTrangThai.setSelectedItem(hd.getTrangThai());

        // Reset chi tiết form
        txtSoLuong.setText("");

        // Gán biến toàn cục
        this.maHD = maHD;

        // Load lại combobox nguyên liệu
        loadComboNguyenLieu();

    }

    void clearFormHoaDon() {
        txtTenKH.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        dateNgayDH.setDate(null);
        dateNgayTT.setDate(null);
        cbbTrangThai.setSelectedIndex(0);

    }
    //------------------------ tab thông tin--------------

    private void fillTabThongTin(String maHD) {
        txtMaHD.setText(maHD);
        txtMaHD.setEditable(false); // Khóa không cho sửa
        fillChiTietHoaDon(maHD);
        loadComboNguyenLieu(); // Load combobox nguyên liệu
    }

    private void loadComboNguyenLieu() {
        cboSanPham.removeAllItems();
        List<NguyenLieuE> list = nguyenLieuDAO.selectTenVaMaNL();
        if (list == null || list.isEmpty()) {
            System.out.println("Không có nguyên liệu nào trong DB!");
            return;
        }
        for (NguyenLieuE nl : list) {
            cboSanPham.addItem(nl); // hiện TenNL nhờ toString()
        }
        // Kiểm tra lại size trước khi setSelectedIndex
        if (cboSanPham.getItemCount() > 0) {
            cboSanPham.setSelectedIndex(0); // Chọn item đầu tiên
            NguyenLieuE nl = (NguyenLieuE) cboSanPham.getSelectedItem();
            }
    }

    //themCTDH
    void insertChiTietDonHang() {
    if (maHD == null || maHD.isEmpty()) {
        MsgBox.alert(this, "Bạn chưa chọn hoặc tạo đơn hàng.");
        return;
    }

    NguyenLieuE nl = (NguyenLieuE) cboSanPham.getSelectedItem();
    if (nl == null) {
        MsgBox.alert(this, "Vui lòng chọn nguyên liệu.");
        return;
    }

    try {
        int soLuong = Integer.parseInt(txtSoLuong.getText());

        if (soLuong <= 0) {
            MsgBox.alert(this, "Số lượng phải lớn hơn 0.");
            return;
        }

        // Lấy số lượng còn lại trong kho từ DAO
        QLKhoDAO khodao = new QLKhoDAO();
        int soLuongConLai = khodao.getSoLuongConLai(nl.getMaNL());
        if (soLuong > soLuongConLai) {
            MsgBox.alert(this, "Số lượng vượt quá tồn kho (" + soLuongConLai + "). Không thể thêm!");
            return;
        }

        // Kiểm tra trạng thái đơn hàng
        HoaDonE hd = hoaDonDAO.selectById(maHD);
        if (!hd.getTrangThai().equals("Chờ xử lý")) {
            MsgBox.alert(this, "Chỉ có thể thêm chi tiết khi đơn hàng đang ở trạng thái 'Chờ xử lý'.");
            return;
        }

        chiTietDAO.insert(maHD, nl.getMaNL(), soLuong);
        fillChiTietHoaDon(maHD);
        txtSoLuong.setText("");
        MsgBox.alert(this, "Đã thêm sản phẩm vào đơn hàng.");
    } catch (NumberFormatException ex) {
        MsgBox.alert(this, "Số lượng không hợp lệ.");
    } catch (Exception e) {
        e.printStackTrace();
        MsgBox.alert(this, "Lỗi khi thêm chi tiết đơn hàng.");
    }
}


    private void lamMoiChiTiet() {
        cboSanPham.setSelectedIndex(0);
        txtSoLuong.setText("");
        lblTongTien.setText("0 VNĐ");
        maNLSelected = null;
        txtMaHD.setText("");
        btnThem.setEnabled(true);
        btnCapNhat.setEnabled(true);   // MỞ nút Sửa
        btnXoa.setEnabled(true);   // MỞ nút Xóa
        // Xóa bảng chi tiết đơn hàng
        DefaultTableModel model = (DefaultTableModel) tblChiTietDonHang.getModel();
        model.setRowCount(0);
    }

    void xoaChiTiet() {
        // Kiểm tra trạng thái
        String trangThai = cbbTrangThai.getSelectedItem().toString();
        if (!trangThai.equalsIgnoreCase("Chờ xử lý")) {
            MsgBox.alert(this, "Chỉ có thể xóa khi đơn hàng đang ở trạng thái 'Chờ xử lý'.");
            return;
        }
        int row = tblChiTietDonHang.getSelectedRow();
        if (row < 0) {
            MsgBox.alert(this, "Vui lòng chọn dòng sản phẩm cần xóa.");
            return;
        }
        String tenSanPham = tblChiTietDonHang.getValueAt(row, 0).toString();
        String maNL = nguyenLieuDAO.getMaNLByTen(tenSanPham);
        if (maNL == null) {
            MsgBox.alert(this, "Không tìm thấy mã nguyên liệu tương ứng.");
            return;
        }
        if (!MsgBox.confirm(this, "Bạn có chắc chắn muốn xóa sản phẩm này khỏi đơn hàng?")) {
            return;
        }
        chiTietDAO.delete(maHD, maNL);
        MsgBox.alert(this, "Xóa thành công!");
        fillChiTietHoaDon(maHD);
        cboSanPham.setSelectedIndex(0);
        txtSoLuong.setText("");
        btnXoa.setEnabled(false);
        btnCapNhat.setEnabled(false);
    }

    //update chitietDH
    void updateChiTietDonHang() {
    if (!loGin()) {
        return;
    }
    if (maHD == null || maHD.isEmpty()) {
        MsgBox.alert(this, "Bạn chưa chọn hóa đơn.");
        return;
    }

    NguyenLieuE nl = (NguyenLieuE) cboSanPham.getSelectedItem();
    if (nl == null) {
        MsgBox.alert(this, "Vui lòng chọn nguyên liệu.");
        return;
    }

    try {
        int soLuongMoi = Integer.parseInt(txtSoLuong.getText());
        if (soLuongMoi <= 0) {
            MsgBox.alert(this, "Số lượng phải là số nguyên dương.");
            return;
        }

        HoaDonE hd = hoaDonDAO.selectById(maHD);
        if (!hd.getTrangThai().equals("Chờ xử lý")) {
            MsgBox.alert(this, "Chỉ có thể cập nhật khi đơn hàng đang ở trạng thái 'Chờ xử lý'.");
            return;
        }

        // Lấy số lượng hiện tại trong chi tiết đơn hàng (nếu có)
        int soLuongCu = chiTietDAO.selectSoLuong(maHD, nl.getMaNL());

        // Lấy số lượng còn lại của nguyên liệu (trừ tất cả số đã bán)
        QLKhoDAO khodao = new QLKhoDAO();
        int soLuongConLaiTrongKho = khodao.getSoLuongConLai(nl.getMaNL());

        // Tính lại số lượng còn lại sau khi cập nhật
        int soLuongThayDoi = soLuongMoi - soLuongCu;
        int soLuongSauCapNhat = soLuongConLaiTrongKho - soLuongThayDoi;

        if (soLuongSauCapNhat < 0) {
            MsgBox.alert(this, "Cập nhật thất bại: số lượng vượt quá tồn kho (" + soLuongConLaiTrongKho + ").");
            return;
        }

        chiTietDAO.update(maHD, nl.getMaNL(), soLuongMoi);

        MsgBox.alert(this, "Cập nhật chi tiết thành công!");
        fillChiTietHoaDon(maHD);
        fillTableHoaDon();
        fillTabThongTin(maHD);
        txtSoLuong.setText("");

    } catch (NumberFormatException ex) {
        MsgBox.alert(this, "Vui lòng nhập số lượng hợp lệ.");
    } catch (Exception e) {
        e.printStackTrace();
        MsgBox.alert(this, "Cập nhật thất bại.");
    }
}

    
    // Xuất PDF
    private void xuatPDF_HoaDon() {
        try {
            String maHD = txtMaHD.getText();
            HoaDonE hd = hoaDonDAO.selectById(maHD);
            KhachHangE kh = khachHangDAO.selectById(hd.getMaKH());
            List<ChiTietDonHangE> listCT = chiTietDAO.selectByMaHD(maHD);

            // Đặt file chính xác
            String filePath = "D:/HoaDon_" + maHD + ".pdf";
            PdfUtils.xuatHoaDonPDF(hd, kh, listCT, filePath);

            File file = new File(filePath);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "Lỗi: File PDF chưa được tạo.");
                return;
            }

            JOptionPane.showMessageDialog(null, "Đã xuất PDF: " + filePath);

            // Gửi email
            String to = kh.getEmail();
            if (to == null || to.isBlank()) {
                JOptionPane.showMessageDialog(null, "Khách hàng chưa có email!");
                return;
            }

            String subject = "Hóa đơn #" + maHD;
            String content = "Xin chào " + kh.getTenKH() + ",\n\n"
                    + "Cảm ơn bạn đã mua hàng. Hóa đơn của bạn được đính kèm trong file PDF.\n\n"
                    + "Trân trọng,\nCửa hàng PUCA!";

            boolean success = MailSender.sendEmail(to, subject, content, file);
            if (success) {
                JOptionPane.showMessageDialog(null, "Đã gửi email hóa đơn!");
            } else {
                JOptionPane.showMessageDialog(null, "Không thể gửi email.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jSpinner1 = new javax.swing.JSpinner();
        jScrollBar1 = new javax.swing.JScrollBar();
        tabs = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHoaDon = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cboTrangThai = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtTenKH = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtDiaChi = new javax.swing.JTextPane();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        dateNgayDH = new com.toedter.calendar.JDateChooser();
        dateNgayTT = new com.toedter.calendar.JDateChooser();
        btnThemDH = new javax.swing.JButton();
        btnXoaDH = new javax.swing.JButton();
        btnCapNhatDH = new javax.swing.JButton();
        btnReSet = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cbbTrangThai = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtMaHD = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblChiTietDonHang = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnCapNhat = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnXuat = new javax.swing.JButton();
        cboSanPham = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtSoLuong = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        lblTongTien = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbTonKho = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã hóa đơn", "Tên khách hàng", "SĐT khách hàng", "Ngày đặt hàng", "Ngày thanh toán", "Trạng thái"
            }
        ));
        tblHoaDon.setEditingColumn(0);
        tblHoaDon.setEditingRow(0);
        tblHoaDon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHoaDonMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblHoaDon);
        if (tblHoaDon.getColumnModel().getColumnCount() > 0) {
            tblHoaDon.getColumnModel().getColumn(0).setHeaderValue("Mã hóa đơn");
            tblHoaDon.getColumnModel().getColumn(1).setHeaderValue("Tên khách hàng");
            tblHoaDon.getColumnModel().getColumn(2).setHeaderValue("SĐT khách hàng");
            tblHoaDon.getColumnModel().getColumn(3).setHeaderValue("Ngày đặt hàng");
            tblHoaDon.getColumnModel().getColumn(4).setHeaderValue("Ngày thanh toán");
            tblHoaDon.getColumnModel().getColumn(5).setHeaderValue("Trạng thái");
        }

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 51, 102));
        jLabel6.setText("Tìm kiếm:");

        cboTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrangThaiActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 51, 153));
        jLabel9.setText("Lọc trạng thái:");

        jLabel16.setText("Tên Khách hàng:");

        jLabel17.setText("Số điện thoại:");

        txtSDT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSDTActionPerformed(evt);
            }
        });

        jLabel18.setText("Địa chỉ:");

        jScrollPane4.setViewportView(txtDiaChi);

        jLabel19.setText("Ngày đặt hàng:");

        jLabel20.setText("Ngày thanh toán:");

        btnThemDH.setText("Thêm đơn hàng");
        btnThemDH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemDHActionPerformed(evt);
            }
        });

        btnXoaDH.setText("Xóa đơn hàng");
        btnXoaDH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaDHActionPerformed(evt);
            }
        });

        btnCapNhatDH.setText("Cập nhật đơn hàng");
        btnCapNhatDH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatDHActionPerformed(evt);
            }
        });

        btnReSet.setText("Làm mới");
        btnReSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReSetActionPerformed(evt);
            }
        });

        jLabel2.setText("Trạng thái:");

        cbbTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Chờ xử lý", "Đã giao" }));

        jLabel3.setText("Email:");

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel16)
                            .addGap(18, 18, 18))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(13, 13, 13)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel17))
                            .addGap(27, 27, 27)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)
                        .addGap(41, 41, 41)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane4)
                            .addComponent(txtSDT, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                            .addComponent(txtTenKH))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9)
                    .addComponent(txtEmail)
                    .addComponent(cbbTrangThai, 0, 229, Short.MAX_VALUE)
                    .addComponent(dateNgayTT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateNgayDH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboTrangThai, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(51, 51, 51))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnThemDH)
                .addGap(53, 53, 53)
                .addComponent(btnXoaDH)
                .addGap(55, 55, 55)
                .addComponent(btnCapNhatDH)
                .addGap(79, 79, 79)
                .addComponent(btnReSet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(343, 343, 343))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTrangThai)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtTenKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel19)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(dateNgayDH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(dateNgayTT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbbTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addComponent(jLabel20))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThemDH)
                    .addComponent(btnXoaDH)
                    .addComponent(btnCapNhatDH)
                    .addComponent(btnReSet))
                .addContainerGap())
        );

        tabs.addTab("DANH SÁCH", jPanel3);

        jLabel1.setText("Mã hóa đơn:");

        txtMaHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaHDActionPerformed(evt);
            }
        });

        tblChiTietDonHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Tên sản phẩm", "Số lượng", "Đơn vị tính", "Đơn giá", "Thành tiền"
            }
        ));
        tblChiTietDonHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblChiTietDonHangMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblChiTietDonHang);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 102, 255));
        jLabel10.setText("Chi tiết đơn hàng");

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnCapNhat.setText("Cập nhật");
        btnCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatActionPerformed(evt);
            }
        });

        btnLamMoi.setText("Làm mới");
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        btnXuat.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnXuat.setForeground(new java.awt.Color(255, 51, 51));
        btnXuat.setText("Xuất hóa đơn");
        btnXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatActionPerformed(evt);
            }
        });

        cboSanPham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSanPhamActionPerformed(evt);
            }
        });

        jLabel12.setText("Tên nguyên liệu:");

        jLabel13.setText("Số lượng:");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 0, 0));
        jLabel15.setText("Tổng hóa đơn:");

        lblTongTien.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTongTien.setText("0");

        jLabel5.setText("Số lượng còn trong kho:");

        lbTonKho.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnThem)
                                .addGap(36, 36, 36)
                                .addComponent(btnXoa)
                                .addGap(66, 66, 66)
                                .addComponent(btnCapNhat)
                                .addGap(51, 51, 51)
                                .addComponent(btnLamMoi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnXuat))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 899, Short.MAX_VALUE))
                        .addGap(55, 55, 55))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(lblTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel1))
                                .addGap(29, 29, 29)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMaHD, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(lbTonKho, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtMaHD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(73, 73, 73)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cboSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(lbTonKho))
                .addGap(30, 30, 30)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(lblTongTien))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnXoa)
                    .addComponent(btnCapNhat)
                    .addComponent(btnLamMoi)
                    .addComponent(btnXuat))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        tabs.addTab("THÔNG TIN", jPanel1);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 51, 255));
        jLabel4.setText("QUẢN LÝ ĐƠN HÀNG");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(286, 286, 286)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrangThaiActionPerformed
        locVaTimHoaDon();
    }//GEN-LAST:event_cboTrangThaiActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        locVaTimHoaDon();
    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblHoaDonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHoaDonMouseClicked
        int row = tblHoaDon.getSelectedRow();
        if (row < 0) {
            return;
        }

        String maHD = tblHoaDon.getValueAt(row, 0).toString();
        HoaDonE hd = hoaDonDAO.selectById(maHD);
        if (hd == null) {
            return;
        }

        // Gán thông tin khách hàng
        KhachHangE kh = khachHangDAO.selectById(hd.getMaKH());
        if (kh == null) {
            return;
        }

        txtTenKH.setText(kh.getTenKH());
        txtTenKH.setEnabled(false);
        txtSDT.setText(kh.getSoDienThoai());
        txtDiaChi.setText(kh.getDiaChi());
        cbbTrangThai.setSelectedItem(hd.getTrangThai());
        dateNgayDH.setDate(hd.getNgayDatHang());
        dateNgayTT.setDate(hd.getNgayThanhToan());
        txtEmail.setText(kh.getEmail());

        // 👇 Nếu double click thì chuyển sang tab Thông Tin và load
        if (evt.getClickCount() == 2) {
            fillChiTietHoaDon(maHD);             // Gọi hàm fill
            capNhatTrangThaiNutTheoDonHang(hd.getTrangThai()); // kiểm tra trạng thái để bật/tắt
            tabs.setSelectedIndex(1);     // Chuyển tab
        }
    }//GEN-LAST:event_tblHoaDonMouseClicked

    private void btnXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatActionPerformed
        xuatPDF_HoaDon();
    }//GEN-LAST:event_btnXuatActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        if (!loGin()) {
            return;
        }
        lamMoiChiTiet();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnCapNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatActionPerformed
        updateChiTietDonHang();

    }//GEN-LAST:event_btnCapNhatActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        if (!loGin()) {
            return;
        }
        insertChiTietDonHang();
    }//GEN-LAST:event_btnThemActionPerformed

    private void cboSanPhamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSanPhamActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboSanPhamActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        if (!loGin()) {
            return;
        }
        xoaChiTiet();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void txtMaHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaHDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaHDActionPerformed

    private void txtSDTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSDTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSDTActionPerformed

    private void btnThemDHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemDHActionPerformed
        if (Auth.user == null) {
            MsgBox.alert(this, "Bạn chưa đăng nhập.");
            return;
        }
        String maNV = Auth.user.getMaNV();

        // Lấy dữ liệu KH
        String tenKH = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String email = txtEmail.getText().trim();

        if (tenKH.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
            MsgBox.alert(this, "Vui lòng nhập đầy đủ thông tin khách hàng.");
            return;
        }

        // Tìm KH theo SDT
        KhachHangE kh = khachHangDAO.selectAll().stream()
                .filter(k -> k.getSoDienThoai().equals(sdt))
                .findFirst().orElse(null);

        // Nếu chưa có → thêm KH mới
        if (kh == null) {
            String maKH = "KH" + System.currentTimeMillis();
            kh = new KhachHangE();
            kh.setMaKH(maKH);
            kh.setTenKH(tenKH);
            kh.setSoDienThoai(sdt);
            kh.setDiaChi(diaChi);
            kh.setEmail(email);
            khachHangDAO.insert(kh);
            MsgBox.alert(this, "Đã thêm khách hàng mới: " + tenKH);
        }

        // Tạo mã hóa đơn
        String maHD = "HD" + System.currentTimeMillis();

        HoaDonE hd = new HoaDonE();
        hd.setMaHD(maHD);
        hd.setMaKH(kh.getMaKH());
        hd.setNgayDatHang(dateNgayDH.getDate());
        hd.setNgayThanhToan(dateNgayTT.getDate());
        hd.setTrangThai(cbbTrangThai.getSelectedItem().toString());
        hd.setTongTien(BigDecimal.ZERO);
        hd.setMaNV(Auth.user.getMaNV());

        // Lưu hóa đơn
        hoaDonDAO.insert(hd);
        MsgBox.alert(this, "Thêm hóa đơn thành công!");

        fillTableHoaDon();
        clearFormHoaDon();
    }//GEN-LAST:event_btnThemDHActionPerformed

    private void btnReSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReSetActionPerformed
        clearFormHoaDon();
        txtTenKH.setEnabled(true);

    }//GEN-LAST:event_btnReSetActionPerformed

    private void btnCapNhatDHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatDHActionPerformed
        if (Auth.user == null) {
            MsgBox.alert(this, "Bạn chưa đăng nhập.");
            return;
        }
        String maNV = Auth.user.getMaNV();

        int row = tblHoaDon.getSelectedRow();
        if (row < 0) {
            MsgBox.alert(this, "Vui lòng chọn một hóa đơn để cập nhật!");
            return;
        }

        String maHD = tblHoaDon.getValueAt(row, 0).toString();
        HoaDonE hd = hoaDonDAO.selectById(maHD);
        if (hd == null) {
            MsgBox.alert(this, "Không tìm thấy hóa đơn trong CSDL!");
            return;
        }

        boolean daThayDoi = false;

// ==== Kiểm tra và cập nhật Khách hàng ====
        KhachHangE kh = khachHangDAO.selectById(hd.getMaKH());
        if (kh != null) {
            String sdtMoi = txtSDT.getText().trim();
            String diaChiMoi = txtDiaChi.getText().trim();
            String emailMoi = txtEmail.getText().trim();

            if (!sdtMoi.equals(kh.getSoDienThoai())
                    || !diaChiMoi.equals(kh.getDiaChi())
                    || !emailMoi.equals(kh.getEmail())) {

                kh.setSoDienThoai(sdtMoi);
                kh.setDiaChi(diaChiMoi);
                kh.setEmail(emailMoi);
                khachHangDAO.update(kh);
                daThayDoi = true;
            }
        }

// ==== Kiểm tra và cập nhật Hóa đơn ====
        String trangThaiMoi = cbbTrangThai.getSelectedItem().toString();
        Date ngayDatMoi = dateNgayDH.getDate();
        Date ngayTTMoi = dateNgayTT.getDate();

        boolean thayDoiHD = false;
        if (!trangThaiMoi.equals(hd.getTrangThai())) {
            thayDoiHD = true;
        } else if (!ngayDatMoi.equals(hd.getNgayDatHang())) {
            thayDoiHD = true;
        } else if (hd.getNgayThanhToan() == null && ngayTTMoi != null) {
            thayDoiHD = true;
        } else if (hd.getNgayThanhToan() != null && !hd.getNgayThanhToan().equals(ngayTTMoi)) {
            thayDoiHD = true;
        }

        if (thayDoiHD) {
            hd.setTrangThai(trangThaiMoi);
            hd.setNgayDatHang(ngayDatMoi);
            hd.setNgayThanhToan(ngayTTMoi);
            hoaDonDAO.update(hd);
            daThayDoi = true;
        }

// ==== Kết quả ====
        if (daThayDoi) {
            MsgBox.alert(this, "Cập nhật thành công!");
            fillTableHoaDon();
            clearFormHoaDon();
        } else {
            MsgBox.alert(this, "Bạn chưa thay đổi dữ liệu nào!");
        }

    }//GEN-LAST:event_btnCapNhatDHActionPerformed

    private void btnXoaDHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaDHActionPerformed

        int row = tblHoaDon.getSelectedRow();
        if (row < 0) {
            MsgBox.alert(this, "Vui lòng chọn đơn hàng cần xóa!");
            return;
        }

        String maHD = tblHoaDon.getValueAt(row, 0).toString();
        HoaDonE hd = hoaDonDAO.selectById(maHD);

        if (!hd.getTrangThai().equals("Chờ xử lý") && !hd.getTrangThai().equals("Đang giao")) {
            MsgBox.alert(this, "Chỉ được xóa đơn hàng ở trạng thái 'Chờ xử lý' hoặc 'Đang giao'.");
            return;
        }

        if (MsgBox.confirm(this, "Bạn chắc chắn muốn xóa đơn hàng này?")) {
            // Xóa chi tiết trước rồi mới xóa đơn hàng
            chiTietDAO.deleteByMaHD(maHD);
            hoaDonDAO.delete(maHD);
            MsgBox.alert(this, "Xóa thành công!");
            fillTableHoaDon(); // Load lại bảng
        }
    }//GEN-LAST:event_btnXoaDHActionPerformed

    private void tblChiTietDonHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblChiTietDonHangMouseClicked
        int row = tblChiTietDonHang.getSelectedRow();
        if (row < 0) {
            return;
        }

// Lấy dữ liệu từ bảng
        String tenSP = tblChiTietDonHang.getValueAt(row, 0).toString();
        int soLuong = Integer.parseInt(tblChiTietDonHang.getValueAt(row, 1).toString());

// Tìm và set sản phẩm trong ComboBox
        for (int i = 0; i < cboSanPham.getItemCount(); i++) {
            NguyenLieuE nl = (NguyenLieuE) cboSanPham.getItemAt(i);
            if (nl.getTenNL().equalsIgnoreCase(tenSP)) {
                cboSanPham.setSelectedIndex(i);
                break;
            }
        }

// Set số lượng
        txtSoLuong.setText(String.valueOf(soLuong));

// Cho phép/không cho phép sửa xóa
        HoaDonE hd = hoaDonDAO.selectById(maHD);
        if (hd != null && "Chờ xử lý".equalsIgnoreCase(hd.getTrangThai())) {
            btnCapNhat.setEnabled(true);
            btnXoa.setEnabled(true);
            btnThem.setEnabled(false); // Không cho thêm nếu đang sửa
        } else {
            btnCapNhat.setEnabled(false);
            btnXoa.setEnabled(false);
        }

    }//GEN-LAST:event_tblChiTietDonHangMouseClicked

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    public static void main(String args[]) {

        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QLDonHangD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QLDonHangD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QLDonHangD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QLDonHangD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                QLDonHangD dialog = new QLDonHangD(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnCapNhatDH;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnReSet;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnThemDH;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXoaDH;
    private javax.swing.JButton btnXuat;
    private javax.swing.JComboBox<String> cbbTrangThai;
    private javax.swing.JComboBox<NguyenLieuE> cboSanPham;
    private javax.swing.JComboBox<String> cboTrangThai;
    private com.toedter.calendar.JDateChooser dateNgayDH;
    private com.toedter.calendar.JDateChooser dateNgayTT;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JLabel lbTonKho;
    private javax.swing.JLabel lblTongTien;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblChiTietDonHang;
    private javax.swing.JTable tblHoaDon;
    private javax.swing.JTextPane txtDiaChi;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtMaHD;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSoLuong;
    private javax.swing.JTextField txtTenKH;
    // End of variables declaration//GEN-END:variables

}
