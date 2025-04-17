//M.Kha
package UI;

import DAO.NhanVienDAO;
import Entity.NhanVienE;
import Utils.Auth;
import Utils.MsgBox;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

public class NhanVienD extends javax.swing.JDialog {

    NhanVienDAO dao = new NhanVienDAO();
    String path;
    int row = 0;

    public NhanVienD(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Nhân viên");
        setLocationRelativeTo(null);
        fillTable();
        updateStatus();
        rdoNhanVien.setSelected(true);
    }

    public void fillTable() {
        DefaultTableModel model = (DefaultTableModel) tbDanhSach.getModel();
        model.setRowCount(0);
        tbDanhSach.setDefaultEditor(Object.class, null);
        try {
            // Lấy danh sách nhân viên với trạng thái = 1 (hoạt động)
            List<NhanVienE> list = dao.selectAll();
            for (NhanVienE nv : list) {
                Object[] row = {
                    nv.getMaNV(),
                    "*".repeat(nv.getMatKhau().length()),
                    hideEmail(nv.getEmail()), // Gọi hàm ẩn email
                    nv.getHoTen(),
                    nv.isVaiTro() ? "Quản Lý" : "Nhân viên",
                    nv.getHinh()};
                model.addRow(row);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu!");
            e.printStackTrace(); // Giúp debug lỗi nếu có
        }
    }

    public String hideEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex < 6) {  // Nếu email quá ngắn, không đủ 3 ký tự đầu và cuối
            return email;
        }

        String start = email.substring(0, 3);  // Lấy 3 ký tự đầu
        String end = email.substring(atIndex - 3, email.length()); // Lấy 3 ký tự cuối
        return start + "***" + end;
    }

    void edit() {
        try {
            String maNV = (String) tbDanhSach.getValueAt(this.row, 0);
            NhanVienE nv = dao.selectById(maNV);
            if (nv != null) {
                setForm(nv);
                updateStatus();
                tabs.setSelectedIndex(0);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu");
            System.out.println(e);
        }
    }

    void setForm(NhanVienE nv) {
        txtMa.setText(nv.getMaNV());
        txtMK.setText(nv.getMatKhau());
        txtEmail.setText(nv.getEmail());
        txtTen.setText(nv.getHoTen());
        rdoQuanLy.setSelected(nv.isVaiTro());
        rdoNhanVien.setSelected(!nv.isVaiTro());

        if (nv.getHinh() != null && !nv.getHinh().isEmpty()) {
            path = nv.getHinh(); // ví dụ "avt/avatar1.jpg"
            File file = new File("src/main/resources/" + path);

            if (file.exists()) {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(lbPicture.getWidth(), lbPicture.getHeight(), Image.SCALE_SMOOTH);
                lbPicture.setIcon(new ImageIcon(img));
                lbPicture.setText("");
            } else {
                lbPicture.setIcon(null);
                lbPicture.setText("Không tìm thấy ảnh");
            }
        } else {
            lbPicture.setIcon(null);
            lbPicture.setText("No image");
        }
    }

    NhanVienE getForm() {
        NhanVienE nv = new NhanVienE();

// Kiểm tra rỗng
        if (txtMa.getText().trim().isEmpty()
                || txtMK.getPassword().length == 0
                || txtEmail.getText().trim().isEmpty()
                || txtTen.getText().trim().isEmpty()) {
            MsgBox.alert(this, "Vui lòng nhập đầy đủ thông tin!");
            return null;
        }

// Nếu người dùng không chọn lại ảnh, thì giữ ảnh cũ
        if (path == null && lbPicture.getIcon() != null) {
            path = this.dao.selectById(txtMa.getText()).getHinh();
        }

        nv.setMaNV(txtMa.getText());
        nv.setMatKhau(new String(txtMK.getPassword()).trim());
        nv.setEmail(txtEmail.getText().trim());
        nv.setHoTen(txtTen.getText().trim());
        nv.setVaiTro(rdoQuanLy.isSelected());
        nv.setHinh(path != null ? path.trim() : null); // gán ảnh đã chọn hoặc ảnh cũ
        nv.setTrangthai(1);

        return nv;

    }

    void updateStatus() {
        int rowCount = tbDanhSach.getRowCount(); // Số lượng dòng trong bảng
        boolean edit = this.row >= 0;
        boolean first = this.row == 0;
        boolean last = this.row == tbDanhSach.getRowCount() - 1;
        boolean hasData = (rowCount > 0); // Kiểm tra nếu danh sách có dữ liệu

        // trang thái form
        txtMa.setEditable(!edit);
        btnThem.setEnabled(!edit);
        btnSua.setEnabled(edit);
        btnXoa.setEnabled(edit);

        btnFirst.setEnabled(edit && !first);
        btnPrev.setEnabled(edit && !first);
        btnNext.setEnabled(edit && !last);
        btnLast.setEnabled(edit && !last);
    }

    void clearForm() {
        this.setForm(new NhanVienE());
        this.updateStatus();
        row = -1;
        updateStatus();
    }

    void insert() {
        NhanVienE nv = getForm();
        String mk2 = new String(txtMK2.getPassword());

        if (!mk2.equals(nv.getMatKhau())) {
            MsgBox.alert(this, "Xác nhận mật khẩu không đúng!");
            return;
        }

        try {
            dao.insert(nv);
            this.fillTable();
            this.clearForm();
            MsgBox.alert(this, "Thêm mới thành công!");
        } catch (Exception e) {
            MsgBox.alert(this, "Thêm mới thất bại!");
            e.printStackTrace(); // In lỗi ra console để debug
        }
    }

    void update() {
        NhanVienE nv = getForm();
        String confirm = new String(txtMK2.getPassword());
        if (!confirm.equals(nv.getMatKhau())) {
            MsgBox.alert(this, "Xác nhận mật khẩu không đúng!");
        } else {
            try {
                dao.update(nv);
                this.fillTable();
                this.edit(); // gọi lại edit() để load ảnh mới vào lbPicture
                MsgBox.alert(this, "Cập nhật thành công");
            } catch (Exception e) {
                e.printStackTrace();
                MsgBox.alert(this, "Cập nhật thất bại" + e.getMessage());
                // In lỗi ra console để debug
            }
        }
    }

    void delete() {
        if (!Auth.isManager()) {
            MsgBox.alert(this, "Bạn không có quyền xoá nhân viên");
        } else {
            if (MsgBox.confirm(this, "Bạn thực sự muốn xoá nhân viên này?")) {
                String manv = txtMa.getText();
                try {
                    dao.delete(manv);
                    this.fillTable();
                    this.clearForm();
                    MsgBox.alert(this, "Bạn xoá thành công!");
                } catch (Exception e) {
                    MsgBox.alert(this, "Xoá thất bại!");
                }
            }
        }
    }

    void selectImage() {

        JFileChooser fc = new JFileChooser();

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            String fileName = selectedFile.getName();

            // Thư mục lưu ảnh cố định
            File destDir = new File("src/main/resources/avt");
            if (!destDir.exists()) {
                destDir.mkdirs(); // tạo nếu chưa có
            }

            File dest = new File(destDir, fileName);

            try {
                Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                path = "avt/" + fileName; // lưu path tương đối trong DB

                // Hiển thị ảnh
                ImageIcon icon = new ImageIcon(dest.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(lbPicture.getWidth(), lbPicture.getHeight(), Image.SCALE_SMOOTH);
                lbPicture.setIcon(new ImageIcon(img));
                lbPicture.setText("");
            } catch (IOException ex) {
                MsgBox.alert(this, "Không thể chọn ảnh!");
                ex.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtMK1 = new javax.swing.JPasswordField();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        lblMatKhau7 = new javax.swing.JLabel();
        lblHoTen3 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblVaiTro3 = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnMoi = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        txtMK = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        txtTen = new javax.swing.JTextField();
        rdoQuanLy = new javax.swing.JRadioButton();
        rdoNhanVien = new javax.swing.JRadioButton();
        lblMatKhau8 = new javax.swing.JLabel();
        txtMK2 = new javax.swing.JPasswordField();
        txttaikhoan = new javax.swing.JLabel();
        txtMa = new javax.swing.JTextField();
        lbPicture = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbDanhSach = new javax.swing.JTable();

        txtMK1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMK1ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 255));
        jLabel1.setText("QUẢN LÝ NHÂN VIÊN QUẢN TRỊ");

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblMatKhau7.setText("Mật khẩu");

        lblHoTen3.setText("Email");

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        lblVaiTro3.setText("Họ Tên");

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnXoa.setText("Xoá");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnMoi.setText("Mới");
        btnMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoiActionPerformed(evt);
            }
        });

        btnFirst.setText("|<");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnPrev.setText("<<");
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });

        btnNext.setText(">>");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnLast.setText(">|");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        txtMK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMKActionPerformed(evt);
            }
        });

        jLabel5.setText("Vai Trò");

        txtTen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoQuanLy);
        rdoQuanLy.setText("Quản Lý");
        rdoQuanLy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoQuanLyActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoNhanVien);
        rdoNhanVien.setText("Nhân Viên");
        rdoNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoNhanVienActionPerformed(evt);
            }
        });

        lblMatKhau8.setText("Xác nhận Mật khẩu");

        txtMK2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMK2ActionPerformed(evt);
            }
        });

        txttaikhoan.setText("Mã Nhân Viên");

        txtMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaActionPerformed(evt);
            }
        });

        lbPicture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbPicture.setAlignmentX(10.0F);
        lbPicture.setAlignmentY(0.0F);
        lbPicture.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lbPicture.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbPicture.setIconTextGap(20);
        lbPicture.setMaximumSize(new java.awt.Dimension(2, 2));
        lbPicture.setMinimumSize(new java.awt.Dimension(2, 2));
        lbPicture.setPreferredSize(new java.awt.Dimension(2, 2));
        lbPicture.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbPictureMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblVaiTro3)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(btnThem)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnSua)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnXoa)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnMoi)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnPrev, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel5)
                        .addComponent(txtTen, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(rdoQuanLy)
                            .addGap(18, 18, 18)
                            .addComponent(rdoNhanVien))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                .addComponent(lblHoTen3, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtMK2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtMK, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtMa, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblMatKhau7, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblMatKhau8, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGap(18, 18, 18)
                            .addComponent(lbPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txttaikhoan))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(txttaikhoan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtMa, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMatKhau7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtMK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblMatKhau8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addComponent(txtMK2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblHoTen3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbPicture, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblVaiTro3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addGap(14, 14, 14)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoQuanLy)
                    .addComponent(rdoNhanVien))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnSua)
                    .addComponent(btnXoa)
                    .addComponent(btnMoi)
                    .addComponent(btnLast)
                    .addComponent(btnNext)
                    .addComponent(btnPrev)
                    .addComponent(btnFirst))
                .addContainerGap())
        );

        tabs.addTab("CẬP NHẬT", jPanel4);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbDanhSach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "MÃ NV", "MẬT KHẨU", "EMAIL", "HỌ TÊN", "VAI TRÒ"
            }
        ));
        tbDanhSach.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbDanhSachMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tbDanhSach);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab("DANH SÁCH", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        insert();
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        update();
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        delete();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoiActionPerformed
        clearForm();
    }//GEN-LAST:event_btnMoiActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        row = 0;
        edit();
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        if (row > 0) {
            row--;
            edit();
        }
    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        if (row < tbDanhSach.getRowCount() - 1) {
            row++;
            edit();
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        row = tbDanhSach.getRowCount() - 1;
        edit();
    }//GEN-LAST:event_btnLastActionPerformed

    private void tbDanhSachMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDanhSachMousePressed
        if (evt.getClickCount() == 2) {
            this.row = tbDanhSach.rowAtPoint(evt.getPoint());
            edit();
        }
    }//GEN-LAST:event_tbDanhSachMousePressed

    private void txtMK1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMK1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMK1ActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed

    }//GEN-LAST:event_txtEmailActionPerformed

    private void rdoNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoNhanVienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoNhanVienActionPerformed

    private void rdoQuanLyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoQuanLyActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_rdoQuanLyActionPerformed

    private void txtMK2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMK2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMK2ActionPerformed

    private void txtMKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMKActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMKActionPerformed

    private void txtTenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenActionPerformed

    private void txtMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaActionPerformed

    private void lbPictureMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbPictureMouseClicked
        selectImage();
    }//GEN-LAST:event_lbPictureMouseClicked

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NhanVienD dialog = new NhanVienD(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnMoi;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbPicture;
    private javax.swing.JLabel lblHoTen3;
    private javax.swing.JLabel lblMatKhau7;
    private javax.swing.JLabel lblMatKhau8;
    private javax.swing.JLabel lblVaiTro3;
    private javax.swing.JRadioButton rdoNhanVien;
    private javax.swing.JRadioButton rdoQuanLy;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tbDanhSach;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtMK;
    private javax.swing.JPasswordField txtMK1;
    private javax.swing.JPasswordField txtMK2;
    private javax.swing.JTextField txtMa;
    private javax.swing.JTextField txtTen;
    private javax.swing.JLabel txttaikhoan;
    // End of variables declaration//GEN-END:variables
}
