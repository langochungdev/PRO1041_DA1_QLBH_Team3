//La Ngoc Hung
package UI;

import DAO.TaiChinhDAO;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class TaiChinhD extends javax.swing.JDialog {

    TaiChinhDAO tcdao = new TaiChinhDAO();
    

    public TaiChinhD(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Thống kê");
        setLocationRelativeTo(null);
        fillTbDoanhThu();
        fillTbCongNo(null);
    }
    public void selectTab(int index) {
        tabs.setSelectedIndex(index);
    }

    void fillTbCongNo(String trangThai) {
        DefaultTableModel model = (DefaultTableModel) tbCongNo.getModel();
        model.setRowCount(0);
        List<Object[]> list = tcdao.getCongNo(trangThai);
        for (Object[] row : list) {
            model.addRow(row);
        }
    }
    
    void fillTbDoanhThu() {
        DefaultTableModel model = (DefaultTableModel) tbDoanhThu.getModel();
        model.setRowCount(0);
        List<Object[]> list = tcdao.getDoanhThu();
        for (Object[] row : list) {
            model.addRow(row);
        }
    }

    private void searchCongNo(String keyword) {
        DefaultTableModel model = (DefaultTableModel) tbCongNo.getModel();
        model.setRowCount(0);

        List<Object[]> list = tcdao.searchCongNo(keyword);
        for (Object[] row : list) {
            model.addRow(row);
        }
    }
    
    void setRdoTrangThai(int rowIndex){
        String trangThai = tbCongNo.getValueAt(rowIndex, 6).toString().trim();
            if (trangThai.equalsIgnoreCase("Chưa thanh toán") || trangThai.isEmpty()) {
                rdoChuaThanhToan.setSelected(true);
            } else {
                rdoDaThanhToan.setSelected(true);
            }
    }

    void capNhatTrangThaiThanhToan() {
        String selectedItem = cbbSortTrangThai.getSelectedItem().toString();
        int rowIndex = tbCongNo.getSelectedRow();
        int confirm = JOptionPane.showConfirmDialog(null,
                "Bạn có chắc muốn cập nhật trạng thái thanh toán không?",
                "Xác nhận",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.CANCEL_OPTION) {
            setRdoTrangThai(rowIndex);
            return;
        }
        if (rowIndex != -1) {
            String maHD = tbCongNo.getValueAt(rowIndex, 0).toString();
            tcdao.capNhatTrangThaiThanhToan(maHD, rdoDaThanhToan.isSelected() ? true : false);
            fillTbCongNo(selectedItem);
            fillTbDoanhThu();
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        rbtgThanhToan = new javax.swing.ButtonGroup();
        jRadioButton1 = new javax.swing.JRadioButton();
        rdobgThanhToan = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbCongNo = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rdoDaThanhToan = new javax.swing.JRadioButton();
        rdoChuaThanhToan = new javax.swing.JRadioButton();
        cbbSortTrangThai = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbDoanhThu = new javax.swing.JTable();

        jRadioButton1.setText("jRadioButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 255));
        jLabel1.setText("QUẢN LÍ TÀI CHÍNH");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbCongNo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MaHD", "Mã KH", "Tên KH", "SDT", "Ngày đặt", "Số tiền", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbCongNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCongNoMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tbCongNo);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Cập nhật:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Sort by:");

        rdobgThanhToan.add(rdoDaThanhToan);
        rdoDaThanhToan.setText("Đã thanh toán");
        rdoDaThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoDaThanhToanActionPerformed(evt);
            }
        });

        rdobgThanhToan.add(rdoChuaThanhToan);
        rdoChuaThanhToan.setText("Chưa thanh toán");
        rdoChuaThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoChuaThanhToanActionPerformed(evt);
            }
        });

        cbbSortTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Chưa thanh toán", "Đã thanh toán" }));
        cbbSortTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbSortTrangThaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbbSortTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdoDaThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdoChuaThanhToan))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cbbSortTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                        .addComponent(rdoDaThanhToan))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdoChuaThanhToan)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );

        tabs.addTab("CÔNG NỢ", jPanel3);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbDoanhThu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tháng", "Dư nợ", "Tổng nhập", "Tổng xuất", "Tổng doanh thu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tbDoanhThu);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab("DOANH THU", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        String keyword = txtSearch.getText().trim();
        searchCongNo(keyword);
    }//GEN-LAST:event_txtSearchKeyReleased

    private void cbbSortTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbSortTrangThaiActionPerformed
        String selectedItem = cbbSortTrangThai.getSelectedItem().toString();
        fillTbCongNo(selectedItem);
    }//GEN-LAST:event_cbbSortTrangThaiActionPerformed

    private void rdoDaThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoDaThanhToanActionPerformed
        capNhatTrangThaiThanhToan();
    }//GEN-LAST:event_rdoDaThanhToanActionPerformed

    private void rdoChuaThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoChuaThanhToanActionPerformed
        capNhatTrangThaiThanhToan();
    }//GEN-LAST:event_rdoChuaThanhToanActionPerformed

    private void tbCongNoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbCongNoMouseClicked
        int rowIndex = tbCongNo.getSelectedRow();
        if (rowIndex != -1) {
            setRdoTrangThai(rowIndex);
        }

        if (evt.getClickCount() == 2) {
            String MaHD = tbCongNo.getValueAt(rowIndex, 0).toString().trim();

            QLDonHangD dh = new QLDonHangD(null, false);
            dh.setVisible(true);
            dh.selectTab(1);
            dh.fillChiTietHoaDon(MaHD);
        }
    }//GEN-LAST:event_tbCongNoMouseClicked

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
            java.util.logging.Logger.getLogger(TaiChinhD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TaiChinhD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TaiChinhD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TaiChinhD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TaiChinhD dialog = new TaiChinhD(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbbSortTrangThai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.ButtonGroup rbtgThanhToan;
    private javax.swing.JRadioButton rdoChuaThanhToan;
    private javax.swing.JRadioButton rdoDaThanhToan;
    private javax.swing.ButtonGroup rdobgThanhToan;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tbCongNo;
    private javax.swing.JTable tbDoanhThu;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
