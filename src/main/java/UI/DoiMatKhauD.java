package UI;

import DAO.NhanVienDAO;
import Utils.Auth;
import Utils.MsgBox;

public class DoiMatKhauD extends javax.swing.JDialog {

    public DoiMatKhauD(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Đổi mật khẩu");
        setLocationRelativeTo(null);
    }

    NhanVienDAO dao = new NhanVienDAO();

    void doiMatKhau() {
        String TenTK = txtTenTK.getText();
        String mk = new String(txtMatKhau.getPassword());
        String mkMoi1 = new String(txtMatKhau1.getPassword());
        String mkMoi2 = new String(txtMatKhau2.getPassword());
        if (!TenTK.equalsIgnoreCase(Auth.user.getMaNV())){
            MsgBox.alert(this, "Sai tên đăng nhập");
        } else if (!mk.equals(Auth.user.getMatKhau())) {
            MsgBox.alert(this, "Sai mật khẩu");
        } else if (!mkMoi1.equals(mkMoi2)) {
            MsgBox.alert(this, "Mật khẩu không khớp!");
        } else {
            Auth.user.setMatKhau(mkMoi1);
            dao.update(Auth.user);
            MsgBox.alert(this, "Đổi mật khẩu thành công!");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtTenTK = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtMatKhau1 = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        txtMatKhau = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        txtMatKhau2 = new javax.swing.JPasswordField();
        jPanel1 = new javax.swing.JPanel();
        btnDongY = new javax.swing.JButton();
        btnHuyBo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("EduSys - Đổi mật khẩu");

        jLabel1.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 255));
        jLabel1.setText("ĐỔI MẬT KHẨU");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Text.png"))); // NOI18N
        jLabel2.setText("Tên đăng nhập");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Unlock.png"))); // NOI18N
        jLabel3.setText("Mật khẩu mới");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Lock.png"))); // NOI18N
        jLabel4.setText("Mật khẩu hiện tại");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Unlock.png"))); // NOI18N
        jLabel5.setText("Xác nhận mật khẩu mới");

        btnDongY.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Refresh.png"))); // NOI18N
        btnDongY.setText("Đồng ý");
        btnDongY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDongYActionPerformed(evt);
            }
        });
        jPanel1.add(btnDongY);

        btnHuyBo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/No.png"))); // NOI18N
        btnHuyBo.setText("Hủy bỏ");
        btnHuyBo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyBoActionPerformed(evt);
            }
        });
        jPanel1.add(btnHuyBo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTenTK)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMatKhau1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMatKhau2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTenTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMatKhau1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMatKhau2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHuyBoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyBoActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnHuyBoActionPerformed

    private void btnDongYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDongYActionPerformed
        doiMatKhau();
    }//GEN-LAST:event_btnDongYActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DoiMatKhauD dialog = new DoiMatKhauD(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnDongY;
    private javax.swing.JButton btnHuyBo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField txtMatKhau;
    private javax.swing.JPasswordField txtMatKhau1;
    private javax.swing.JPasswordField txtMatKhau2;
    private javax.swing.JTextField txtTenTK;
    // End of variables declaration//GEN-END:variables

//    NhanVienDAO dao = new NhanVienDAO();
//    
//    private void doiMatKhau(){
//        String manv = txtMaNV.getText();
//        String matKhau = new String(txtMatKhau.getPassword());
//        String matKhauMoi = new String(txtMatKhau1.getPassword());
//        String matKhauMoi2 = new String(txtMatKhau2.getPassword());
//        
//        if(!manv.equalsIgnoreCase(Auth.user.getMaNV())){
//            MsgBox.alert(this, "Sai tên đăng nhập!");
//        } else if(!matKhau.equals(Auth.user.getMatKhau())){
//            MsgBox.alert(this, "Sai mật khẩu!");
//        } else if(!matKhauMoi.equals(matKhauMoi2)){
//            MsgBox.alert(this, "Xác nhận mật khẩu không đúng!");
//        } else {
//            Auth.user.setMatKhau(matKhauMoi);
//            dao.update(Auth.user);
//            MsgBox.alert(this, "Đổi mật khẩu thành công!");
//        }
//        this.dispose();
//    }
    private void huyBo() {
        this.dispose();
    }
}
