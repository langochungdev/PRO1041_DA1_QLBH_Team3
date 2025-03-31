//Trung
package UI;
import DAO.NguyenLieuDAO;
import Entity.NguyenLieuE;
import Utils.MsgBox;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.sql.SQLException;


public class QuanLyKhoD extends javax.swing.JDialog {

    public QuanLyKhoD(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    setTitle("Quản lý kho");
    setLocationRelativeTo(null);
    fillTable();
    updateStatus();
    // Vô hiệu hóa chỉnh sửa trực tiếp trên JTable
    tblNguyenLieu.setDefaultEditor(Object.class, null);
    // Thêm MouseListener cho tblNguyenLieu
    tblNguyenLieu.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tblNguyenLieuMouseClicked(evt);
        }
    });
}

    NguyenLieuDAO nldao = new NguyenLieuDAO();
    int row = -1;

    void fillTable(String keyword) {
    DefaultTableModel model = (DefaultTableModel) tblNguyenLieu.getModel();
    model.setRowCount(0);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng ngày

    try {
        List<NguyenLieuE> list = nldao.selectByKeyword(keyword); // Sử dụng từ khóa để tìm kiếm
        for (NguyenLieuE nl : list) {
            Object[] rows = {
                nl.getMaNL(),
                nl.getTenNL(),
                nl.getSoLuongNhap(),
                nl.getSoLuongDaBan(),
                nl.getSoLuongConLai(),
                nl.getDonViTinh(),
                (nl.getNgayNhap() != null) ? sdf.format(nl.getNgayNhap()) : "",
                (nl.getHanSuDung() != null) ? sdf.format(nl.getHanSuDung()) : "",
                nl.getNhaCungCap(),
                (nl.getGiaNhap() != null) ? nl.getGiaNhap().toPlainString() : "0" // Định dạng BigDecimal
            };
            model.addRow(rows);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


// Giữ lại phương thức fillTable() không tham số để có thể gọi khi cần hiển thị toàn bộ dữ liệu
void fillTable() {
    fillTable(""); // Gọi fillTable với từ khóa rỗng để hiển thị tất cả
}
    

    void setForm(NguyenLieuE nl) {
    if (nl != null) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        txtMaNL.setText(String.valueOf(nl.getMaNL()));
        txtTenNL.setText(nl.getTenNL());
        txtSLNhap.setText(String.valueOf(nl.getSoLuongNhap()));
        txtSLDaBan.setText(String.valueOf(nl.getSoLuongDaBan()));
        txtConLaiKho.setText(String.valueOf(nl.getSoLuongConLai()));
        txtDVT.setText(nl.getDonViTinh());
        txtNgayNhap.setText(nl.getNgayNhap() != null ? sdf.format(nl.getNgayNhap()) : "");
        txtHanSuDung.setText(nl.getHanSuDung() != null ? sdf.format(nl.getHanSuDung()) : "");
        txtNhaCungCap.setText(nl.getNhaCungCap());
        txtGia.setText(nl.getGiaNhap() != null ? nl.getGiaNhap().toPlainString() : "0"); // Hiển thị BigDecimal
    }
}


    NguyenLieuE getForm() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    NguyenLieuE nl = new NguyenLieuE();
    try {
        nl.setMaNL(Integer.parseInt(txtMaNL.getText().trim()));
        nl.setTenNL(txtTenNL.getText().trim());
        nl.setSoLuongNhap(Integer.parseInt(txtSLNhap.getText().trim()));
        nl.setSoLuongDaBan(Integer.parseInt(txtSLDaBan.getText().trim()));
        nl.setSoLuongConLai(Integer.parseInt(txtConLaiKho.getText().trim()));
        nl.setDonViTinh(txtDVT.getText().trim());
        nl.setNhaCungCap(txtNhaCungCap.getText().trim());
        nl.setNgayNhap(txtNgayNhap.getText().trim().isEmpty() ? null : sdf.parse(txtNgayNhap.getText().trim()));
        nl.setHanSuDung(txtHanSuDung.getText().trim().isEmpty() ? null : sdf.parse(txtHanSuDung.getText().trim()));
        nl.setGiaNhap(txtGia.getText().trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(txtGia.getText().trim()));

    } catch (NumberFormatException e) {
        throw new ParseException("Lỗi định dạng số: Vui lòng kiểm tra lại các trường số", 0);
    } catch (ParseException e) {
        throw new ParseException("Lỗi định dạng ngày: Vui lòng nhập đúng định dạng yyyy-MM-dd", 0);
    } catch (Exception e) {
        throw new ParseException("Lỗi nhập dữ liệu: " + e.getMessage(), 0);
    }
    return nl;
}


    void updateStatus() {
        boolean edit = row >= 0;
        btnThem.setEnabled(!edit);
        btnSua.setEnabled(edit);
        btnXoa.setEnabled(edit);
    }

    void clearForm() {
        this.setForm(new NguyenLieuE());
        this.updateStatus();
        row = -1;
    }
    void edit(){
        try{
            row = tblNguyenLieu.getSelectedRow();
            if(row >= 0){
                int manl = (int)tblNguyenLieu.getValueAt(row, 0);
                NguyenLieuE nl = nldao.selectById(manl);
                if(nl != null){
                this.setForm(nl);
                updateStatus();         
        }else{
                    MsgBox.alert(this, "Không tìm thấy nguyên liệu có mã:" + manl);                    
                }
    }else{
                    MsgBox.alert(this, "Vui lòng nhập nguyên liệu để sửa!" );                    
            }
        }catch(Exception e){
             MsgBox.alert(this, "Lỗi khi chọn nguyên liệu để sửa: " + e.getMessage());
        e.printStackTrace();
        }
    }
    void insert() {
    try {
        NguyenLieuE nl = getForm();

        // Kiểm tra dữ liệu nhập liệu (không cho phép bỏ trống)
        if (nl.getTenNL().isEmpty() || nl.getDonViTinh().isEmpty() || nl.getNhaCungCap().isEmpty() ||
            txtSLNhap.getText().trim().isEmpty() || txtSLDaBan.getText().trim().isEmpty() ||
            txtConLaiKho.getText().trim().isEmpty() || txtGia.getText().trim().isEmpty()) {
            MsgBox.alert(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Kiểm tra số lượng nhập, số lượng đã bán và giá nhập phải >= 0
        if (nl.getSoLuongNhap() < 0 || nl.getSoLuongDaBan() < 0 || nl.getSoLuongConLai() < 0 || nl.getGiaNhap().compareTo(BigDecimal.ZERO) < 0) {
            MsgBox.alert(this, "Số lượng và giá nhập phải lớn hơn hoặc bằng 0!");
            return;
        }

        // Kiểm tra mã nguyên liệu trùng lặp
        NguyenLieuE nlCheck = nldao.selectById(nl.getMaNL());
        if (nlCheck != null) {
            MsgBox.alert(this, "Mã nguyên liệu đã tồn tại!");
            return;
        }

        // Thêm vào cơ sở dữ liệu
        nldao.insert(nl);
        fillTable(""); // Làm mới bảng
        clearForm();
        MsgBox.alert(this, "Thêm mới thành công!");

    } catch (ParseException e) {
        MsgBox.alert(this, "Lỗi định dạng ngày! Vui lòng kiểm tra lại ngày nhập và hạn sử dụng.");
    } catch (NumberFormatException e) {
        MsgBox.alert(this, "Lỗi định dạng số! Vui lòng kiểm tra lại mã nguyên liệu, số lượng và giá nhập.");
    } catch (Exception e) {
        e.printStackTrace(); // In lỗi chi tiết ra console
        MsgBox.alert(this, "Thêm mới thất bại! Lỗi: " + e.getMessage());
    }
}


    void update() {
    try {
        NguyenLieuE nl = getForm();

        // Kiểm tra dữ liệu nhập liệu (không được bỏ trống)
        if (nl.getTenNL().isEmpty() || nl.getDonViTinh().isEmpty() || nl.getNhaCungCap().isEmpty() ||
            txtSLNhap.getText().trim().isEmpty() || txtSLDaBan.getText().trim().isEmpty() ||
            txtConLaiKho.getText().trim().isEmpty() || txtGia.getText().trim().isEmpty()) {
            MsgBox.alert(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Kiểm tra số lượng nhập, số lượng đã bán và giá nhập phải >= 0
        if (nl.getSoLuongNhap() < 0 || nl.getSoLuongDaBan() < 0 || nl.getSoLuongConLai() < 0 || nl.getGiaNhap().compareTo(BigDecimal.ZERO) < 0) {
            MsgBox.alert(this, "Số lượng và giá nhập phải lớn hơn hoặc bằng 0!");
            return;
        }

        // Kiểm tra xem nguyên liệu có tồn tại không trước khi cập nhật
        NguyenLieuE nlCheck = nldao.selectById(nl.getMaNL());
        if (nlCheck == null) {
            MsgBox.alert(this, "Không tìm thấy nguyên liệu cần cập nhật!");
            return;
        }

        // Cập nhật vào cơ sở dữ liệu
        nldao.update(nl);
        fillTable(""); // Làm mới bảng
        MsgBox.alert(this, "Cập nhật thành công!");

    } catch (ParseException e) {
        MsgBox.alert(this, "Lỗi định dạng ngày! Vui lòng kiểm tra lại ngày nhập và hạn sử dụng.");
    } catch (NumberFormatException e) {
        MsgBox.alert(this, "Lỗi định dạng số! Vui lòng kiểm tra lại mã nguyên liệu, số lượng và giá nhập.");
    } catch (Exception e) {
        e.printStackTrace(); // In lỗi chi tiết ra console
        MsgBox.alert(this, "Cập nhật thất bại! Lỗi: " + e.getMessage());
    }
}


    void delete() {
    try {
        // Kiểm tra nếu chưa nhập mã nguyên liệu
        if (txtMaNL.getText().trim().isEmpty()) {
            MsgBox.alert(this, "Vui lòng nhập mã nguyên liệu cần xóa!");
            return;
        }

        int manl = Integer.parseInt(txtMaNL.getText().trim());

        // Kiểm tra xem nguyên liệu có tồn tại không trước khi xóa
        NguyenLieuE nlCheck = nldao.selectById(manl);
        if (nlCheck == null) {
            MsgBox.alert(this, "Không tìm thấy nguyên liệu để xóa!");
            return;
        }

        // Xác nhận trước khi xóa
        if (MsgBox.confirm(this, "Bạn có chắc chắn muốn xoá nguyên liệu này?")) {
            try {
                nldao.delete(manl);
                fillTable(""); // Cập nhật bảng
                clearForm();   // Xóa thông tin trên form
                MsgBox.alert(this, "Xóa thành công!");
            } catch (RuntimeException ex) { // Bắt lỗi DAO
                String errorMsg = ex.getMessage();
                if (errorMsg.contains("foreign key")) {
                    MsgBox.alert(this, "Không thể xóa! Nguyên liệu này đang được sử dụng.");
                } else {
                    MsgBox.alert(this, "Xóa thất bại! Lỗi: " + errorMsg);
                }
                ex.printStackTrace();
            }
        }
    } catch (NumberFormatException e) {
        MsgBox.alert(this, "Mã nguyên liệu không hợp lệ! Vui lòng nhập số.");
    } catch (Exception e) {
        MsgBox.alert(this, "Xóa thất bại! Lỗi không xác định: " + e.getMessage());
        e.printStackTrace();
    }
}



    private void tblNguyenLieuMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) { // Kiểm tra xem có phải là double click hay không
            row = tblNguyenLieu.getSelectedRow();
            if (row >= 0) {
                edit();
            }
        }
    }

    void search() {
    String keyword = txtTimKiem.getText().trim(); // Loại bỏ khoảng trắng thừa

    if (keyword.isEmpty()) {
        MsgBox.alert(this, "Vui lòng nhập từ khóa tìm kiếm!");
        return;
    }

    fillTable(keyword); // Gọi fillTable với từ khóa
    clearForm();
    row = -1;
    updateStatus();
}

private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {
    search();
}

   

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        txtTimKiem = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtMaNL = new javax.swing.JTextField();
        txtTenNL = new javax.swing.JTextField();
        txtSLNhap = new javax.swing.JTextField();
        txtConLaiKho = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtNgayNhap = new javax.swing.JTextField();
        txtHanSuDung = new javax.swing.JTextField();
        txtGia = new javax.swing.JTextField();
        txtSLDaBan = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNguyenLieu = new javax.swing.JTable();
        btnThem = new javax.swing.JButton();
        btnMoi = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtDVT = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtNhaCungCap = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1050, 800));

        jPanel2.setPreferredSize(new java.awt.Dimension(550, 80));

        btnTimKiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Search.png"))); // NOI18N
        btnTimKiem.setText("Tìm kiếm");

        jLabel1.setText("Tìm kiếm");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(btnTimKiem))
                    .addComponent(jLabel1))
                .addContainerGap(291, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTimKiem)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel1.setPreferredSize(new java.awt.Dimension(400, 450));

        jLabel2.setText("Thông tin");

        jLabel3.setText("Mã nguyên liệu:");

        jLabel4.setText("Tên nguyên liệu:");

        jLabel5.setText("Số lượng nhập:");

        jLabel6.setText("Số lượng đã bán:");

        jLabel7.setText("Còn lại kho:");

        txtTenNL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenNLActionPerformed(evt);
            }
        });

        jLabel8.setText("Ngày nhập:");

        jLabel9.setText("Hạn sử dụng:");

        jLabel10.setText("Giá:");

        txtHanSuDung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHanSuDungActionPerformed(evt);
            }
        });

        tblNguyenLieu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã nguyên liệu", "Tên nguyên liệu", "Mã nhà cung cấp", "Ngày nhập", "Hạn sử dụng", "Số lượng đã bán", "Đơn vị tính", "Hạn sử dụng", "Nhà cung cấp", "Giá"
            }
        ));
        tblNguyenLieu.setPreferredSize(new java.awt.Dimension(900, 180));
        jScrollPane1.setViewportView(tblNguyenLieu);

        btnThem.setText("Thêm");
        btnThem.setPreferredSize(new java.awt.Dimension(80, 30));
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnMoi.setText("Mới");
        btnMoi.setPreferredSize(new java.awt.Dimension(80, 30));
        btnMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoiActionPerformed(evt);
            }
        });

        btnSua.setText("Sửa");
        btnSua.setPreferredSize(new java.awt.Dimension(80, 30));

        btnXoa.setText("Xóa");
        btnXoa.setPreferredSize(new java.awt.Dimension(80, 30));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 902, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(78, 78, 78)
                        .addComponent(btnMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(84, 84, 84)
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58)
                        .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(139, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(133, Short.MAX_VALUE))
        );

        jLabel11.setText("Đơn vị tính:");

        jLabel12.setText("Nhà cung cấp:");

        txtNhaCungCap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNhaCungCapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel5))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel4)
                                                .addGap(16, 16, 16))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addGap(18, 18, 18))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtMaNL, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(txtSLNhap)
                                                .addComponent(txtTenNL)
                                                .addComponent(txtConLaiKho, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtSLDaBan, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)))))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel10))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtNhaCungCap, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                                    .addComponent(txtGia)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel11))
                                .addGap(22, 22, 22)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtNgayNhap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                                    .addComponent(txtDVT, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtHanSuDung)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtMaNL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtDVT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTenNL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtNgayNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSLNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtHanSuDung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(34, 34, 34)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtConLaiKho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtSLDaBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6)
                                .addComponent(jLabel12)
                                .addComponent(txtNhaCungCap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel7)))
                    .addComponent(txtGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
private void btnTimKiemLActionPerformed(java.awt.event.ActionEvent evt) {                                         
        search();
    }    
    private void txtTenNLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenNLActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenNLActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        insert();
    }//GEN-LAST:event_btnThemActionPerformed
    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {                                       
        update();
    }     
    private void txtHanSuDungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHanSuDungActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHanSuDungActionPerformed

    private void txtNhaCungCapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNhaCungCapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNhaCungCapActionPerformed

    private void btnMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoiActionPerformed
         clearForm();
    }//GEN-LAST:event_btnMoiActionPerformed
    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {                                       
        delete();
    }   
    private void tblNguyenLieuMousePressed(java.awt.event.MouseEvent evt) {                                        
        if (evt.getClickCount() == 2) {
            this.row = tblNguyenLieu.rowAtPoint(evt.getPoint());
            edit();
        }
    }                                       

    
    public static void main(String args[]) {
        
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
    private javax.swing.JButton btnMoi;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblNguyenLieu;
    private javax.swing.JTextField txtConLaiKho;
    private javax.swing.JTextField txtDVT;
    private javax.swing.JTextField txtGia;
    private javax.swing.JTextField txtHanSuDung;
    private javax.swing.JTextField txtMaNL;
    private javax.swing.JTextField txtNgayNhap;
    private javax.swing.JTextField txtNhaCungCap;
    private javax.swing.JTextField txtSLDaBan;
    private javax.swing.JTextField txtSLNhap;
    private javax.swing.JTextField txtTenNL;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
 

}