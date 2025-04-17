package UI;

import Utils.Auth;
import java.sql.Date;
import DAO.CaLamDAO;
import Entity.CaLamE;
import Utils.JdbcHelper;
import Utils.MsgBox;
import java.awt.Font;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class CaLamD extends javax.swing.JDialog {
// Các khai báo biến cho các trường nhập liệu

    CaLamDAO dao = new CaLamDAO();
    int row = 0;

    public CaLamD(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        fillTable();
        fillTableCaNhan();
        updateStatus();
        // Gọi sau tất cả phần khởi tạo
        SwingUtilities.invokeLater(() -> showUserInfo());
        SwingUtilities.invokeLater(() -> init());
    }

    void edit() {
        try {
            String maNV = (String) tbDanhSach.getValueAt(this.row, 0);
            CaLamE nv = dao.selectById(maNV);
            if (nv != null) {
                setForm(nv);
                updateStatus();
                tabs.setSelectedIndex(0);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu");
        }
    }

    void setForm(CaLamE cl) {
        txtMaNV.setText(cl.getMaNV());
        txtTen.setText(cl.getTenNV());
        // Convert LocalDate to java.util.Date
        java.util.Date date = java.sql.Date.valueOf(cl.getNgayLam());
        cldNgayLam.setDate(date); // Set ngày vào JCalendar
        cbbCaLam.setSelectedItem(cl.getCaLam()); // Set lựa chọn ca làm
    }

    void init() {
        setLocationRelativeTo(null); // căn giữa form nếu muốn
        setTitle("Quản lý ca làm");

        // Gọi fill dữ liệu bảng lần đầu
        fillTable();

        // Auto search khi gõ trong txtSearch
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                autoSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                autoSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                autoSearch();
            }
        });

        // Nếu cần phân quyền ở đây
        updateStatus();
    }

    public String getNextMaCa() {
        String sql = "SELECT MAX(SUBSTRING(MaCa, 3, 3)) FROM CaLam"; // Lấy số lớn nhất trong mã ca (CA001 -> 1)
        try {
            ResultSet rs = JdbcHelper.execQuery(sql);
            if (rs.next()) {
                int maxNumber = rs.getInt(1); // Lấy số lớn nhất
                return String.format("CA%03d", maxNumber + 1); // Tạo mã ca mới tăng 1
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "CA001"; // Nếu không có dữ liệu thì trả về mã ca đầu tiên
    }

    void insert() {
        CaLamE caLam = getForm();
        if (caLam == null) {
            return; // getForm() đã báo lỗi nếu thiếu thông tin
        }

        String maNV = caLam.getMaNV();
        LocalDate ngayLam = caLam.getNgayLam();
        String ca = caLam.getCaLam();

        // Kiểm tra ngày đã qua
        if (ngayLam.isBefore(LocalDate.now())) {
            MsgBox.alert(this, "Không thể đăng ký ngày đã qua!");
            return;
        }

// Kiểm tra đã đăng ký bất kỳ ca nào trong ngày chưa
        if (dao.kiemTraDaDangKyNgay(maNV, ngayLam)) {
            MsgBox.alert(this, "Bạn chỉ được đăng ký 1 ca trong mỗi ngày!");
            return;
        }

        try {
            dao.insert(caLam); // Thêm ca vào DB
            fillTable();
            fillTableCaNhan(); // Cập nhật lại bảng ca cá nhân
            MsgBox.alert(this, "Đăng ký ca làm thành công!");
        } catch (Exception e) {
            MsgBox.alert(this, "Đăng ký thất bại! " + e.getMessage());
            e.printStackTrace();
        }
    }

    CaLamE getForm() {
        CaLamE cl = new CaLamE();
        String maNV = txtMaNV.getText().trim();
        String tenNV = txtTen.getText().trim();
        java.util.Date utilDate = cldNgayLam.getDate(); // java.util.Date
        String caLam = (String) cbbCaLam.getSelectedItem();

        if (maNV.isEmpty() || tenNV.isEmpty() || utilDate == null || caLam == null) {
            MsgBox.alert(this, "Vui lòng nhập đầy đủ thông tin!");
            return null;
        }

        // Chuyển sang LocalDate để xử lý (nếu cần dùng)
        LocalDate localDate = utilDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Lấy mã ca tiếp theo
        String maCa = getNextMaCa();

        // Gán vào entity
        cl.setMaCa(maCa);
        cl.setMaNV(maNV);
        cl.setTenNV(tenNV);
        cl.setNgayLam(localDate); // LocalDate sẽ được xử lý bên DAO
        cl.setCaLam(caLam);

        return cl;
    }

    void fillTable() {
        DefaultTableModel model = (DefaultTableModel) tbDanhSach.getModel();
        model.setRowCount(0);
        tbDanhSach.setDefaultEditor(Object.class, null);
        try {
            List<CaLamE> list = dao.selectAll();
            for (CaLamE cl : list) {
                model.addRow(new Object[]{
                    cl.getMaCa(), cl.getMaNV(), cl.getTenNV(), cl.getNgayLam(), cl.getCaLam()
                });
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu!" + e.getMessage());
            e.printStackTrace();
        }
    }

    void fillTableCaNhan() {
        DefaultTableModel model = (DefaultTableModel) tbDSCaNhan.getModel();
        model.setRowCount(0);
        tbDSCaNhan.setDefaultEditor(Object.class, null);
        try {
            String maNV = Auth.user.getMaNV();
            List<CaLamE> list = dao.selectByMaNV(maNV); // Lấy danh sách theo mã nhân viên

            for (CaLamE cl : list) {
                model.addRow(new Object[]{
                    cl.getMaCa(),
                    cl.getMaNV(),
                    cl.getTenNV(),
                    cl.getNgayLam(),
                    cl.getCaLam()
                });
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn ca làm cá nhân!");
            e.printStackTrace();
        }
    }

    void autoSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            fillTable(); // Hiện toàn bộ nếu không nhập gì
            return;
        }

        List<CaLamE> list = dao.selectByMaNV(keyword); // Bạn cần DAO có hàm này
        DefaultTableModel model = (DefaultTableModel) tbDanhSach.getModel();
        model.setRowCount(0);
        for (CaLamE cl : list) {
            model.addRow(new Object[]{
                cl.getMaCa(), cl.getMaNV(), cl.getTenNV(),
                cl.getNgayLam(), cl.getCaLam()
            });
        }
    }

    

    void updateStatus() {
        boolean isManager = Auth.isManager();
        btnXoa.setVisible(isManager);
        btnXoaALL.setVisible(isManager);
    }

    // hủy đăng ký
    void deleteFrom() {
        int row = tbDSCaNhan.getSelectedRow();
        if (row < 0) {
            MsgBox.alert(this, "Vui lòng chọn ca làm cần hủy!");
            return;
        }

        String maCa = tbDSCaNhan.getValueAt(row, 0).toString();
        try {
            dao.delete(maCa);
            fillTable();         // cập nhật bảng tổng
            fillTableCaNhan();   // cập nhật bảng cá nhân
            MsgBox.alert(this, "Hủy đăng ký ca làm thành công!");
        } catch (Exception e) {
            MsgBox.alert(this, "Hủy thất bại!");
            e.printStackTrace();
        }
    }

// Phương thức hiển thị thông tin nhân viên khi vào form
    public void showUserInfo() {
        if (Auth.user != null) {
            if (txtMaNV != null && txtTen != null) {  // kiểm tra tránh NullPointer
                txtMaNV.setText(Auth.user.getMaNV());
                txtTen.setText(Auth.user.getHoTen());

                // Chặn chỉnh sửa
                txtMaNV.setEditable(false);
                txtTen.setEditable(false);
                txtMaNV.setFocusable(false);
                txtTen.setFocusable(false);

                
            }
        } else {
            System.out.println("Auth.user đang null lúc showUserInfo");
        }
    }


    void delete() {
        int row = tbDanhSach.getSelectedRow(); // Lấy chỉ số dòng được chọn trong bảng
        if (row < 0) {  // Kiểm tra nếu không có dòng nào được chọn
            MsgBox.alert(this, "Vui lòng chọn ca làm cần xóa!");
            return;
        }
        // Lấy mã ca từ cột đầu tiên của dòng được chọn
        String maCa = tbDanhSach.getValueAt(row, 0).toString();
        try {
            // Gọi phương thức xóa từ DAO để xóa bản ghi theo mã ca
            dao.delete(maCa);

            // Cập nhật lại bảng sau khi xóa
            fillTable();
            fillTableCaNhan();
            MsgBox.alert(this, "Xóa ca làm thành công!");
        } catch (Exception e) {
            MsgBox.alert(this, "Xóa thất bại!" + e.getMessage());
            e.printStackTrace();
        }
    }

    void deteleAll(){
        int confirm = JOptionPane.showConfirmDialog(this, 
        "Bạn có chắc chắn muốn xóa toàn bộ ca làm đã đăng ký không?",
        "Xác nhận",
        JOptionPane.YES_NO_OPTION);
    
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try {
        dao.xoaTatCaCaLam();
        MsgBox.alert(this, "Đã xóa toàn bộ đăng ký ca làm!");
        fillTable();
        fillTableCaNhan(); // Load lại bảng cá nhân rỗng
    } catch (Exception e) {
        e.printStackTrace();
        MsgBox.alert(this, "Xóa thất bại: " + e.getMessage());
    }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtMaNV = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTen = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cbbCaLam = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cldNgayLam = new com.toedter.calendar.JCalendar();
        btnDangKy = new javax.swing.JButton();
        btnHuyDangKy = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbDSCaNhan = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbDanhSach = new javax.swing.JTable();
        btnXoa = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnXoaALL = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 255));
        jLabel1.setText("QUẢN LÝ CA LÀM");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Mã Nhân Viên :");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Tên Nhân Viên :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Ngày Làm :");

        cbbCaLam.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cbbCaLam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ca 1", "Ca 2", "Ca 3" }));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Ca Làm :");

        btnDangKy.setForeground(new java.awt.Color(255, 51, 51));
        btnDangKy.setText("Đăng Ký");
        btnDangKy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDangKyActionPerformed(evt);
            }
        });

        btnHuyDangKy.setForeground(new java.awt.Color(255, 51, 51));
        btnHuyDangKy.setText("Hủy Đăng Ký");
        btnHuyDangKy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyDangKyActionPerformed(evt);
            }
        });

        tbDSCaNhan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã Ca", "Mã NV", "Tên NV", "Ngày Làm", "Ca Làm"
            }
        ));
        jScrollPane1.setViewportView(tbDSCaNhan);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 592, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cldNgayLam, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(btnDangKy, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(btnHuyDangKy, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbbCaLam, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel4))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cldNgayLam, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbbCaLam, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                        .addComponent(btnDangKy, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnHuyDangKy, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        tabs.addTab("Đăng Ký Ca Làm", jPanel2);

        tbDanhSach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã Ca", "Mã Nhân Viên", "Tên Nhân Viên", "Ngày Làm", "Ca Làm"
            }
        ));
        jScrollPane2.setViewportView(tbDanhSach);

        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnXoa.setForeground(new java.awt.Color(255, 51, 51));
        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 0, 51));
        jLabel8.setText("Tìm kiếm :");

        btnXoaALL.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnXoaALL.setForeground(new java.awt.Color(255, 51, 51));
        btnXoaALL.setText("Làm Mới");
        btnXoaALL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaALLActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnXoaALL, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa)
                    .addComponent(btnXoaALL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35))
        );

        tabs.addTab("Danh Sách", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Quản Lý Ca Làm");
        getAccessibleContext().setAccessibleDescription("Quản Lý Ca Làm");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDangKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangKyActionPerformed
        // TODO add your handling code here:
        insert();
    }//GEN-LAST:event_btnDangKyActionPerformed

    private void btnHuyDangKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyDangKyActionPerformed
        deleteFrom();
    }//GEN-LAST:event_btnHuyDangKyActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // (TODO add your handling code here:
        delete();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        autoSearch();
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnXoaALLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaALLActionPerformed
        // TODO add your handling code here:
        deteleAll();
    }//GEN-LAST:event_btnXoaALLActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
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
            java.util.logging.Logger.getLogger(CaLamD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CaLamD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CaLamD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CaLamD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CaLamD dialog = new CaLamD(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnDangKy;
    private javax.swing.JButton btnHuyDangKy;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXoaALL;
    private javax.swing.JComboBox<String> cbbCaLam;
    private com.toedter.calendar.JCalendar cldNgayLam;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tbDSCaNhan;
    private javax.swing.JTable tbDanhSach;
    private javax.swing.JTextField txtMaNV;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTen;
    // End of variables declaration//GEN-END:variables
}
