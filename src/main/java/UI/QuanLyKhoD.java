/*FORM của BẠN*/
package UI;

import DAO.NguyenLieuDAO;
import DAO.NhaCungCapDAO;
import Entity.NguyenLieuE;
import Entity.NhaCungCapE;
import Utils.MsgBox;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class QuanLyKhoD extends javax.swing.JDialog {

    private NguyenLieuDAO nldao = new NguyenLieuDAO();

    public QuanLyKhoD(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        loadAllData(); // Tải dữ liệu vào bảng khi form hiển thị
        loadTbNhaCungCap();

        // Khai báo cho tìm kiếm theo chữ gõ
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timKiemTheoTuKhoa();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timKiemTheoTuKhoa();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timKiemTheoTuKhoa();
            }
        });

        // Thêm MouseListener cho tblNguyenLieu (vị trí này hoặc tương tự)
        tblNguyenLieu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tblNguyenLieu.getSelectedRow();
                    if (row != -1) {
                        String maNL = (String) tblNguyenLieu.getValueAt(row, 0);
                        hienThiThongTinNguyenLieu(maNL);
                        // Nếu có tabs, ví dụ:
                        tabs.setSelectedIndex(1);
                    }
                }
            }
        });
    }

    private void timKiemTheoTuKhoa() {
        String tuKhoa = txtTimKiem.getText().trim();
        if (!tuKhoa.isEmpty()) { // Kiểm tra ô tìm kiếm không trống
            List<NguyenLieuE> list = nldao.selectByKeyword(tuKhoa);
            loadDataToTable(list);
        } else {
            loadAllData(); // Khi ô tìm kiếm trống, tải lại tất cả dữ liệu
        }
    }

    //fill dữ liệu lên tblNguyenLieu
    private void loadDataToTable(List<NguyenLieuE> list) {
        DefaultTableModel model = (DefaultTableModel) tblNguyenLieu.getModel();
        model.setRowCount(0); // Xóa hết dữ liệu cũ trong bảng
        tblNguyenLieu.setDefaultEditor(Object.class, null); // Khóa chỉnh sửa trong bảng

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (NguyenLieuE nl : list) {
            Object[] rowData = {
                nl.getMaNL(),
                nl.getTenNL(),
                nl.getSoLuongTon(),
                nl.getSoLuongDaBan(),
                nl.getSoLuongConLai(),
                nl.getDonViTinh(),
                (nl.getNgayNhap() != null) ? sdf.format(nl.getNgayNhap()) : "",
                (nl.getHanSuDung() != null) ? sdf.format(nl.getHanSuDung()) : "",
                nl.getNhaCungCap(),
                (nl.getGiaNhap() != null) ? nl.getGiaNhap().toPlainString() : "0"
            };
            model.addRow(rowData);
        }

    }

    private void loadAllData() {
        List<NguyenLieuE> list = nldao.getAll();
        loadDataToTable(list);
    }

    private void hienThiThongTinNguyenLieu(String maNL) {
        NguyenLieuDAO dao = new NguyenLieuDAO();
        NguyenLieuE nl = dao.getById(maNL);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (nl != null) {
            txtMaNL.setText(nl.getMaNL());
            txtTenNL.setText(nl.getTenNL());
            txtSLNhap.setText(String.valueOf(nl.getSoLuongTon()));
            txtSLDaBan.setText(String.valueOf(nl.getSoLuongDaBan()));
            txtSLConLai.setText(String.valueOf(nl.getSoLuongConLai()));
            txtDVT.setText(nl.getDonViTinh());
            dateNgayNhap.setDate(nl.getNgayNhap());
            dateHanSuDung.setDate(nl.getHanSuDung());
            txtNhaCungCap.setText(nl.getNhaCungCap());
            txtGia.setText((nl.getGiaNhap() != null) ? nl.getGiaNhap().toPlainString() : "");
        }
    }
    // Chức năng

    private void themNguyenLieu() {
        // Lấy dữ liệu từ các trường nhập liệu
        String maNL = txtMaNL.getText();
        String tenNL = txtTenNL.getText();
        String donViTinh = txtDVT.getText();
        String nhaCungCap = txtNhaCungCap.getText();
        Date ngayNhap = dateNgayNhap.getDate();
        Date hanSuDung = dateHanSuDung.getDate();
        int soLuongTon;
        int soLuongDaBan;
        Double giaNhap;

        // Kiểm tra dữ liệu nhập vào (có thể thêm các kiểm tra khác)
        if (tenNL.isEmpty() || donViTinh.isEmpty() || nhaCungCap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            soLuongTon = Integer.parseInt(txtSLNhap.getText());
            soLuongDaBan = Integer.parseInt(txtSLDaBan.getText());
            giaNhap = Double.parseDouble(txtGia.getText());
            if (soLuongTon < 0 || soLuongDaBan < 0 || giaNhap < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng và giá nhập không được âm!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng tồn, số lượng đã bán và giá nhập phải là số!");
            return;
        }

        // Tạo đối tượng NguyenLieuE
        NguyenLieuE nl = new NguyenLieuE();
        nl.setMaNL(maNL);
        nl.setTenNL(tenNL);
        nl.setSoLuongTon(soLuongTon);
        nl.setSoLuongDaBan(soLuongDaBan);
        nl.setSoLuongConLai(soLuongTon - soLuongDaBan); // Tự động tính toán
        nl.setDonViTinh(donViTinh);
        nl.setNgayNhap(ngayNhap);
        nl.setHanSuDung(hanSuDung);
        nl.setNhaCungCap(nhaCungCap);
        nl.setGiaNhap(giaNhap);

        // Thêm nguyên liệu vào cơ sở dữ liệu
        try {
            nldao.insert(nl);
            JOptionPane.showMessageDialog(this, "Thêm nguyên liệu thành công!");
            loadAllData(); // Tải lại dữ liệu vào bảng
            lamMoiForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm nguyên liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void capNhatNguyenLieu() {
        // Lấy dữ liệu từ các trường nhập liệu
        String maNL = txtMaNL.getText();
        String tenNL = txtTenNL.getText();
        String donViTinh = txtDVT.getText();
        String nhaCungCap = txtNhaCungCap.getText();
        Date ngayNhap = dateNgayNhap.getDate();
        Date hanSuDung = dateHanSuDung.getDate();
        int soLuongTon;
        int soLuongDaBan;
        Double giaNhap;

        // Kiểm tra dữ liệu nhập vào
        if (tenNL.isEmpty() || donViTinh.isEmpty() || nhaCungCap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            soLuongTon = Integer.parseInt(txtSLNhap.getText());
            soLuongDaBan = Integer.parseInt(txtSLDaBan.getText());
            giaNhap = Double.parseDouble(txtGia.getText());
            if (soLuongTon < 0 || soLuongDaBan < 0 || giaNhap < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng và giá nhập không được âm!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng tồn, số lượng đã bán và giá nhập phải là số!");
            return;
        }

        // Tạo đối tượng NguyenLieuE
        NguyenLieuE nl = new NguyenLieuE();
        nl.setMaNL(maNL);
        nl.setTenNL(tenNL);
        nl.setSoLuongTon(soLuongTon);
        nl.setSoLuongDaBan(soLuongDaBan);
        nl.setSoLuongConLai(soLuongTon - soLuongDaBan); // Tự động tính toán
        nl.setDonViTinh(donViTinh);
        nl.setNgayNhap(ngayNhap);
        nl.setHanSuDung(hanSuDung);
        nl.setNhaCungCap(nhaCungCap);
        nl.setGiaNhap(giaNhap);

        // Cập nhật nguyên liệu trong cơ sở dữ liệu
        try {
            nldao.update(nl);
            JOptionPane.showMessageDialog(this, "Cập nhật nguyên liệu thành công!");
            loadAllData(); // Tải lại dữ liệu vào bảng
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật nguyên liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void xoaNguyenLieu() {
        // Không cần kiểm tra quyền quản lý ở đây vì đây là form quản lý nguyên liệu
        if (MsgBox.confirm(this, "Bạn thực sự muốn xóa nguyên liệu này?")) {
            String maNL = txtMaNL.getText();
            try {
                nldao.delete(maNL);
                loadAllData(); // Tải lại dữ liệu vào bảng
                lamMoiForm(); // Xóa trắng các trường nhập liệu
                MsgBox.alert(this, "Xóa nguyên liệu thành công!");
            } catch (Exception e) {
                MsgBox.alert(this, "Xóa nguyên liệu thất bại!");
                e.printStackTrace(); // In lỗi ra console để debug
            }
        }
    }

    private void lamMoiForm() {
        txtMaNL.setText("");
        txtTenNL.setText("");
        txtSLNhap.setText("");
        txtSLDaBan.setText("");
        txtSLConLai.setText("");
        txtDVT.setText("");
        dateNgayNhap.setDate(null);
        dateHanSuDung.setDate(null);
        txtNhaCungCap.setText("");
        txtGia.setText("");
    }
    
    NhaCungCapDAO nccdao = new NhaCungCapDAO();
    void loadTbNhaCungCap(){
        DefaultTableModel model = (DefaultTableModel) tbNhaCungCap.getModel();
        model.setRowCount(0);
        try {
            List<NhaCungCapE> list = nccdao.selectAll();
            for (NhaCungCapE ncc : list) {
                Object[] row = {
                    ncc.getMaNCC(),
                    ncc.getTenNCC(),
                    ncc.getDiaChi(),
                    ncc.getSDT()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }
    
    void moiNCC(){
        txtMaNCC.setText("");
        txtTenNCC.setText("");
        txtDiaChi.setText("");
        txtSDT.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jSpinner1 = new javax.swing.JSpinner();
        jScrollBar1 = new javax.swing.JScrollBar();
        tabs = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        txtTimKiem = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblNguyenLieu = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtMaNL = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        dateNgayNhap = new com.toedter.calendar.JDateChooser();
        dateHanSuDung = new com.toedter.calendar.JDateChooser();
        btnThem = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtNhaCungCap = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtGia = new javax.swing.JTextField();
        txtTenNL = new javax.swing.JTextField();
        txtSLConLai = new javax.swing.JTextField();
        txtSLDaBan = new javax.swing.JTextField();
        txtSLNhap = new javax.swing.JTextField();
        txtDVT = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbNhaCungCap = new javax.swing.JTable();
        btnThemNCC = new javax.swing.JButton();
        btnXoaNCC = new javax.swing.JButton();
        btnSuaNCC = new javax.swing.JButton();
        btnMoi = new javax.swing.JButton();
        txtSDT = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtDiaChi = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtTenNCC = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtMaNCC = new javax.swing.JTextField();
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
        setPreferredSize(new java.awt.Dimension(1000, 500));

        txtTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimKiemActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 51, 102));
        jLabel6.setText("Tìm kiếm:");

        tblNguyenLieu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã nguyên liệu", "Tên nguyên liệu", "Số lượng nhập", "Số lượng đã bán", "Số lượng còn lại kho", "Đơn vị tính", "Ngày nhập", "Hạn sử dụng", "Nhà cung cấp", "Giá"
            }
        ));
        jScrollPane2.setViewportView(tblNguyenLieu);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(0, 387, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabs.addTab("DANH SÁCH", jPanel3);

        jLabel1.setText("Mã nguyên liệu:");

        txtMaNL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaNLActionPerformed(evt);
            }
        });

        jLabel2.setText("Tên nguyên liệu");

        jLabel3.setText("Số lượng đã bán:");

        jLabel5.setText("Ngày nhập hàng:");

        jLabel7.setText("Hạn sử dụng:");

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnXoa.setText("Xóa");

        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnLamMoi.setText("Làm mới");
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        jLabel11.setText("Tên nhà cung cấp:");

        jLabel12.setText("Số lượng nhập:");

        jLabel13.setText("Đơn vị tính:");

        txtNhaCungCap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNhaCungCapActionPerformed(evt);
            }
        });

        jLabel9.setText("Giá");

        txtSLNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSLNhapActionPerformed(evt);
            }
        });

        txtDVT.setText("jTextField1");

        jLabel8.setText("Số lượng còn lại kho:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMaNL, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel11)
                            .addComponent(txtNhaCungCap)
                            .addComponent(dateNgayNhap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSLDaBan))
                        .addGap(62, 62, 62)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel2)
                                        .addComponent(dateHanSuDung, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                                        .addComponent(jLabel7)
                                        .addComponent(txtTenNL)
                                        .addComponent(txtSLConLai))
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel13)
                                    .addComponent(txtSLNhap, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                                    .addComponent(txtDVT)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(txtGia, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 350, Short.MAX_VALUE)))
                        .addGap(55, 55, 55))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnThem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnXoa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSua)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLamMoi)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMaNL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTenNL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSLNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel13))
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDVT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSLConLai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSLDaBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateHanSuDung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(dateNgayNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel9))))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNhaCungCap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnXoa)
                    .addComponent(btnSua)
                    .addComponent(btnLamMoi))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabs.addTab("THÔNG TIN", jPanel1);

        tbNhaCungCap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Ma", "Tên", "Địa chỉ", "SDT"
            }
        ));
        tbNhaCungCap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbNhaCungCapMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbNhaCungCap);

        btnThemNCC.setText("Thêm");
        btnThemNCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemNCCActionPerformed(evt);
            }
        });

        btnXoaNCC.setText("Xóa");
        btnXoaNCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaNCCActionPerformed(evt);
            }
        });

        btnSuaNCC.setText("Sửa");
        btnSuaNCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaNCCActionPerformed(evt);
            }
        });

        btnMoi.setText("Mới");
        btnMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoiActionPerformed(evt);
            }
        });

        jLabel16.setText("SDT");

        jLabel17.setText("Địa chỉ");

        jLabel18.setText("Tên");

        jLabel19.setText("Mã");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(btnThemNCC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnXoaNCC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSuaNCC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoi))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtMaNCC, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTenNCC, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtMaNCC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTenNCC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoaNCC)
                    .addComponent(btnSuaNCC)
                    .addComponent(btnMoi)
                    .addComponent(btnThemNCC))
                .addGap(41, 41, 41))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab("NHÀ CUNG CẤP ", jPanel4);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 51, 51));
        jLabel4.setText("QUẢN LÝ KHO HÀNG");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(286, 286, 286)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 960, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimKiemActionPerformed
        timKiemTheoTuKhoa();
    }//GEN-LAST:event_txtTimKiemActionPerformed
    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {
        xoaNguyenLieu();
    }
    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        lamMoiForm();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        capNhatNguyenLieu();
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        themNguyenLieu();
    }//GEN-LAST:event_btnThemActionPerformed

    private void txtMaNLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaNLActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaNLActionPerformed

    private void txtNhaCungCapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNhaCungCapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNhaCungCapActionPerformed

    private void txtSLNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSLNhapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSLNhapActionPerformed

    private void btnMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoiActionPerformed
        moiNCC();
    }//GEN-LAST:event_btnMoiActionPerformed

    private void tbNhaCungCapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbNhaCungCapMouseClicked
        int row = tbNhaCungCap.getSelectedRow();
        String maNCC = (String) tbNhaCungCap.getValueAt(row, 0);
        NhaCungCapE ncc = nccdao.selectById(maNCC);
        txtMaNCC.setText(ncc.getMaNCC().toString());
        txtTenNCC.setText(ncc.getTenNCC().toString());
        txtDiaChi.setText(ncc.getDiaChi().toString());
        txtSDT.setText(ncc.getSDT().toString());
    }//GEN-LAST:event_tbNhaCungCapMouseClicked

    private void btnSuaNCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaNCCActionPerformed
        NhaCungCapE ncc = new NhaCungCapE();
        ncc.setMaNCC(txtMaNCC.getText());
        ncc.setTenNCC(txtTenNCC.getText());
        ncc.setDiaChi(txtDiaChi.getText());
        ncc.setSDT(txtSDT.getText());
        nccdao.update(ncc);
        loadTbNhaCungCap();
    }//GEN-LAST:event_btnSuaNCCActionPerformed

    private void btnXoaNCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaNCCActionPerformed
        nccdao.delete(txtMaNCC.getText());
        moiNCC();
        loadTbNhaCungCap();
    }//GEN-LAST:event_btnXoaNCCActionPerformed

    private void btnThemNCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemNCCActionPerformed
        NhaCungCapE ncc = new NhaCungCapE();
        ncc.setMaNCC(txtMaNCC.getText());
        ncc.setTenNCC(txtTenNCC.getText());
        ncc.setDiaChi(txtDiaChi.getText());
        ncc.setSDT(txtSDT.getText());
        nccdao.insert(ncc);
        loadTbNhaCungCap();
    }//GEN-LAST:event_btnThemNCCActionPerformed

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
            java.util.logging.Logger.getLogger(QuanLyKhoD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QuanLyKhoD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QuanLyKhoD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QuanLyKhoD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                QuanLyKhoD dialog = new QuanLyKhoD(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnMoi;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnSuaNCC;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnThemNCC;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXoaNCC;
    private com.toedter.calendar.JDateChooser dateHanSuDung;
    private com.toedter.calendar.JDateChooser dateNgayNhap;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tbNhaCungCap;
    private javax.swing.JTable tblNguyenLieu;
    private javax.swing.JTextField txtDVT;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtGia;
    private javax.swing.JTextField txtMaNCC;
    private javax.swing.JTextField txtMaNL;
    private javax.swing.JTextField txtNhaCungCap;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSLConLai;
    private javax.swing.JTextField txtSLDaBan;
    private javax.swing.JTextField txtSLNhap;
    private javax.swing.JTextField txtTenNCC;
    private javax.swing.JTextField txtTenNL;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables

}
