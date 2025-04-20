/*Trung*/
package UI;

import DAO.NguyenLieuDAO;
import DAO.NhaCungCapDAO;
import DAO.QLKhoDAO;
import Entity.NguyenLieuE;
import Entity.NhaCungCapE;
import Utils.Auth;
import Utils.MsgBox;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class QuanLyKhoD extends javax.swing.JDialog {

    QLKhoDAO qlKho = new QLKhoDAO();
    NguyenLieuDAO nldao = new NguyenLieuDAO();

    public QuanLyKhoD(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        filltbNguyenLieu();
        loadTbNhaCungCap();
        phanquyen();
        loadCboNhaCungCap();
        txtSLDaBan.setEnabled(false);
        txtSLConLai.setEnabled(false);
        timThoiGianThuc();
    }

    void timThoiGianThuc() {
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                timKiem();
            }

            public void removeUpdate(DocumentEvent e) {
                timKiem();
            }

            public void changedUpdate(DocumentEvent e) {
                timKiem();
            }

            private void timKiem() {
                String keyword = txtTimKiem.getText().trim();
                List<Object[]> ds = new QLKhoDAO().searchByKeyword(keyword);
                DefaultTableModel model = (DefaultTableModel) tbNguyenLieu.getModel();
                model.setRowCount(0);
                for (Object[] row : ds) {
                    model.addRow(row);
                }
            }
        });
    }

    void filltbNguyenLieu() {
        DefaultTableModel model = (DefaultTableModel) tbNguyenLieu.getModel();
        model.setRowCount(0);
        List<Object[]> list = qlKho.getqlKho();
        for (Object[] row : list) {
            model.addRow(row);
        }
    }

    void phanquyen() {
    boolean isManager = Auth.isManager();
    if (!isManager) {
        btnThem.setEnabled(true);
        btnXoa.setEnabled(false);
        btnSua.setEnabled(false);
        btnLamMoi.setEnabled(true);

        btnThemNCC.setEnabled(true);
        btnSuaNCC.setEnabled(false);
        btnXoaNCC.setEnabled(false);
        btnMoi.setEnabled(true);
    }
}


    private void fillFormNguyenLieu(String maNL) {
        QLKhoDAO dao = new QLKhoDAO();
        List<Object[]> list = dao.getqlKho();

        for (Object[] row : list) {
            String ma = String.valueOf(row[0]);
            if (ma.equalsIgnoreCase(maNL)) {
                // Gán dữ liệu vào các field text
                txtMaNL.setText(String.valueOf(row[0])); // MaNL
                txtTenNL.setText(String.valueOf(row[1])); // TenNL
                txtSLNhap.setText(String.valueOf(row[2])); // SoLuongNhap
                txtSLDaBan.setText(String.valueOf(row[3])); // SoLuongDaBan
                txtSLConLai.setText(String.valueOf(row[4])); // SoLuongConLai
                txtDVT.setText(String.valueOf(row[5])); // DonViTinh

                txtNgayNhap.setText(String.valueOf(row[6]));
                txtHanSuDung.setText(String.valueOf(row[7])); // HanSuDung

                // Gán nhà cung cấp
                cboNhaCungCap.setSelectedItem(String.valueOf(row[8])); // NhaCungCap

                // Gán giá nhập và giá xuất
                txtGiaNhap.setText((row[9] != null) ? String.valueOf(row[9]) : "");
                txtGiaBan.setText((row[10] != null) ? String.valueOf(row[10]) : "");
                break;
            }
        }
    }

    private void insertNguyenLieu() {
        try {
            String maNL = txtMaNL.getText().trim();
            String tenNL = txtTenNL.getText().trim();
            String maNCC = cboNhaCungCap.getSelectedItem().toString().trim();
            Date ngayNhap = java.sql.Date.valueOf(txtNgayNhap.getText().trim()); // YYYY-MM-DD
            Date hanSuDung = java.sql.Date.valueOf(txtHanSuDung.getText().trim()); // YYYY-MM-DD
            int soLuongTon = Integer.parseInt(txtSLConLai.getText().trim());
            String donViTinh = txtDVT.getText().trim();
            double giaNhap = Double.parseDouble(txtGiaNhap.getText().trim());
            double giaXuat = Double.parseDouble(txtGiaBan.getText().trim());

            QLKhoDAO dao = new QLKhoDAO();
            dao.insertNguyenLieu(maNL, tenNL, maNCC, ngayNhap, hanSuDung, soLuongTon, donViTinh, giaNhap, giaXuat);

            JOptionPane.showMessageDialog(this, "Thêm nguyên liệu thành công");
            filltbNguyenLieu();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Thêm nguyên liệu thất bại: " + e.getMessage());
        }
    }

    private void deleteNguyenLieu() {
        String maNL = txtMaNL.getText().trim();
        if (maNL.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã nguyên liệu cần xóa");
            return;
        }

        QLKhoDAO dao = new QLKhoDAO();
        if (dao.isNguyenLieuDaDuocBan(maNL)) {
            JOptionPane.showMessageDialog(this, "Không thể xóa vì nguyên liệu đã được sử dụng trong hóa đơn");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nguyên liệu này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.deleteNguyenLieu(maNL);
                JOptionPane.showMessageDialog(this, "Xóa nguyên liệu thành công");
                lamMoiForm();
                filltbNguyenLieu();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Xóa thất bại: " + e.getMessage());
            }
        }
    }

    private void suaNguyenLieu() {
        try {
            String maNL = txtMaNL.getText();

            // Kiểm tra nguyên liệu đã từng được bán
            if (qlKho.isNguyenLieuDaDuocBan(maNL)) {
                JOptionPane.showMessageDialog(null, "Nguyên liệu này đã được sử dụng trong hóa đơn, không thể sửa!");
                return; // Dừng không cho sửa
            }

            String tenNL = txtTenNL.getText();
            String maNCC = cboNhaCungCap.getSelectedItem().toString().trim();
            String slNhap = txtSLNhap.getText();
            String dvt = txtDVT.getText();
            String ngayNhapStr = txtNgayNhap.getText();
            String hanSDStr = txtHanSuDung.getText();
            String giaNhap = txtGiaNhap.getText();
            String giaBan = txtGiaBan.getText();

            Date ngayNhap = java.sql.Date.valueOf(txtNgayNhap.getText().trim()); // yyyy-MM-dd
            Date hanSD = java.sql.Date.valueOf(txtHanSuDung.getText().trim()); // yyyy-MM-dd

            // Tạo đối tượng nguyên liệu và gán giá trị
            NguyenLieuE nl = new NguyenLieuE();
            nl.setMaNL(maNL);
            nl.setTenNL(tenNL);
            nl.setMaNCC(maNCC);
            nl.setSoLuongTon(Integer.parseInt(slNhap));
            nl.setDonViTinh(dvt);
            nl.setNgayNhap(ngayNhap);
            nl.setHanSuDung(hanSD);
            nl.setGiaNhap(Float.parseFloat(giaNhap));
            nl.setGiaXuat(Float.parseFloat(giaBan));

            // Cập nhật vào CSDL
            nldao.update(nl);
            JOptionPane.showMessageDialog(null, "Cập nhật nguyên liệu thành công!");
            filltbNguyenLieu();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật nguyên liệu: " + e.getMessage());
        }
    }

    private void loadCboNhaCungCap() {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel) cboNhaCungCap.getModel();
        model.removeAllElements();
        try {
            List<NhaCungCapE> listNCC = nccdao.selectAll();
            for (NhaCungCapE ncc : listNCC) {
                model.addElement(ncc.getMaNCC());
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi tải dữ liệu nhà cung cấp!");
            e.printStackTrace();
        }
    }

    private void lamMoiForm() {
        txtMaNL.setText("");
        txtTenNL.setText("");
        txtSLNhap.setText("");
        txtSLDaBan.setText("");
        txtSLConLai.setText("");
        txtDVT.setText("");
        txtNgayNhap.setText("");
        txtHanSuDung.setText("");
        txtGiaNhap.setText("");
        txtGiaBan.setText("");
        cboNhaCungCap.setSelectedIndex(-1);
    }

    NhaCungCapDAO nccdao = new NhaCungCapDAO();

    void loadTbNhaCungCap() {
        DefaultTableModel model = (DefaultTableModel) tbNhaCungCap.getModel();
        model.setRowCount(0);
        tbNhaCungCap.setDefaultEditor(Object.class, null);
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
            e.printStackTrace();
        }
    }

    private void themNCC() {
        String maNCC = txtMaNCC.getText().trim();
        String tenNCC = txtTenNCC.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (maNCC.isEmpty() || tenNCC.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
            MsgBox.alert(this, "Vui lòng nhập đầy đủ thông tin nhà cung cấp!");
            return;
        }

        if (nccdao.selectById(maNCC) != null) {
            MsgBox.alert(this, "Mã nhà cung cấp đã tồn tại!");
            return;
        }

        NhaCungCapE ncc = new NhaCungCapE(maNCC, tenNCC, diaChi, sdt);

        try {
            nccdao.insert(ncc);
            loadTbNhaCungCap();
            moiNCC();
            MsgBox.alert(this, "Thêm nhà cung cấp thành công!");
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi thêm nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void suaNCC() {
        String maNCC = txtMaNCC.getText(); // Mã NCC thường là khóa chính
        String tenNCC = txtTenNCC.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (tenNCC.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
            MsgBox.alert(this, "Vui lòng nhập đầy đủ thông tin nhà cung cấp!");
            return;
        }

        NhaCungCapE ncc = new NhaCungCapE(maNCC, tenNCC, diaChi, sdt);

        try {
            nccdao.update(ncc);
            loadTbNhaCungCap();
            MsgBox.alert(this, "Cập nhật nhà cung cấp thành công!");
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi cập nhật nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void xoaNCC() {
        String maNCC = txtMaNCC.getText();

        if (nccdao.isNCCInUse(maNCC)) {
            MsgBox.alert(this, "Không thể xóa. Nhà cung cấp này đang được sử dụng trong bảng Nguyên Liệu!");
            return;
        }

        if (MsgBox.confirm(this, "Bạn thực sự muốn xóa nhà cung cấp này?")) {
            try {
                nccdao.delete(maNCC);
                loadTbNhaCungCap();
                moiNCC();
                MsgBox.alert(this, "Xóa nhà cung cấp thành công!");
            } catch (Exception e) {
                MsgBox.alert(this, "Lỗi xóa nhà cung cấp: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    void moiNCC() {
        txtMaNCC.setText("");
        txtTenNCC.setText("");
        txtDiaChi.setText("");
        txtSDT.setText("");
    }

    void fillFormNCC() {
        int row = tbNhaCungCap.getSelectedRow();
        String maNCC = (String) tbNhaCungCap.getValueAt(row, 0);
        NhaCungCapE ncc = nccdao.selectById(maNCC);
        txtMaNCC.setText(ncc.getMaNCC().toString());
        txtTenNCC.setText(ncc.getTenNCC().toString());
        txtDiaChi.setText(ncc.getDiaChi().toString());
        txtSDT.setText(ncc.getSDT().toString());
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
        tbNguyenLieu = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtMaNL = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtGiaNhap = new javax.swing.JTextField();
        txtTenNL = new javax.swing.JTextField();
        txtSLConLai = new javax.swing.JTextField();
        txtSLDaBan = new javax.swing.JTextField();
        txtSLNhap = new javax.swing.JTextField();
        txtDVT = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cboNhaCungCap = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        txtGiaBan = new javax.swing.JTextField();
        txtHanSuDung = new javax.swing.JTextField();
        txtNgayNhap = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
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

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 51, 102));
        jLabel6.setText("Tìm kiếm:");

        tbNguyenLieu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã nguyên liệu", "Tên nguyên liệu", "Số lượng nhập", "Số lượng đã bán", "Số lượng còn lại kho", "Đơn vị tính", "Ngày nhập", "Hạn sử dụng", "Nhà cung cấp", "Giá nhập", "Giá bán"
            }
        ));
        tbNguyenLieu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbNguyenLieuMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbNguyenLieuMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(tbNguyenLieu);

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

        jLabel7.setText("Hạn sử dụng:");

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

        jLabel11.setText("Nhà cung cấp:");

        jLabel12.setText("Số lượng nhập:");

        jLabel13.setText("Đơn vị tính:");

        jLabel9.setText("Giá nhập:");

        txtSLNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSLNhapActionPerformed(evt);
            }
        });

        jLabel8.setText("Số lượng còn lại:");

        jLabel10.setText("Giá bán:");

        jLabel14.setText("Ngày nhập:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnThem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnXoa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSua)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLamMoi)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtMaNL, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtSLDaBan, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cboNhaCungCap, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtNgayNhap)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel14)
                                    .addGap(138, 138, 138))))
                        .addGap(62, 62, 62)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtHanSuDung)
                                    .addComponent(txtTenNL, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSLConLai, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtGiaNhap, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel9))
                                        .addGap(138, 138, 138)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(txtGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel12)
                                        .addComponent(jLabel13)
                                        .addComponent(txtSLNhap, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                                        .addComponent(txtDVT)))
                                .addGap(55, 55, 55))))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel13)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDVT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSLConLai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSLDaBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHanSuDung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNgayNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtGiaNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboNhaCungCap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnXoa)
                    .addComponent(btnSua)
                    .addComponent(btnLamMoi))
                .addContainerGap(27, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
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

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        lamMoiForm();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void txtMaNLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaNLActionPerformed
    }//GEN-LAST:event_txtMaNLActionPerformed

    private void txtSLNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSLNhapActionPerformed
    }//GEN-LAST:event_txtSLNhapActionPerformed

    private void btnMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoiActionPerformed
        moiNCC();
    }//GEN-LAST:event_btnMoiActionPerformed

    private void tbNhaCungCapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbNhaCungCapMouseClicked
        fillFormNCC();
    }//GEN-LAST:event_tbNhaCungCapMouseClicked

    private void btnSuaNCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaNCCActionPerformed
        suaNCC();
    }//GEN-LAST:event_btnSuaNCCActionPerformed

    private void btnXoaNCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaNCCActionPerformed
        xoaNCC();
    }//GEN-LAST:event_btnXoaNCCActionPerformed

    private void btnThemNCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemNCCActionPerformed
        themNCC();
    }//GEN-LAST:event_btnThemNCCActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        deleteNguyenLieu();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void tbNguyenLieuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbNguyenLieuMouseClicked
    }//GEN-LAST:event_tbNguyenLieuMouseClicked

    private void tbNguyenLieuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbNguyenLieuMousePressed
        if (evt.getClickCount() == 2) {
            int rowIndex = tbNguyenLieu.getSelectedRow();
            String maNL = (String) tbNguyenLieu.getValueAt(rowIndex, 0);
            fillFormNguyenLieu(maNL);
            tabs.setSelectedIndex(1);
        }
    }//GEN-LAST:event_tbNguyenLieuMousePressed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        insertNguyenLieu();
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        suaNguyenLieu();
    }//GEN-LAST:event_btnSuaActionPerformed

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
    private javax.swing.JComboBox<String> cboNhaCungCap;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
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
    private javax.swing.JTable tbNguyenLieu;
    private javax.swing.JTable tbNhaCungCap;
    private javax.swing.JTextField txtDVT;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtGiaBan;
    private javax.swing.JTextField txtGiaNhap;
    private javax.swing.JTextField txtHanSuDung;
    private javax.swing.JTextField txtMaNCC;
    private javax.swing.JTextField txtMaNL;
    private javax.swing.JTextField txtNgayNhap;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSLConLai;
    private javax.swing.JTextField txtSLDaBan;
    private javax.swing.JTextField txtSLNhap;
    private javax.swing.JTextField txtTenNCC;
    private javax.swing.JTextField txtTenNL;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables

}
