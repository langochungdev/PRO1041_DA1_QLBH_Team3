
package Utils;

import Entity.ChiTietDonHangE;
import Entity.HoaDonE;
import Entity.KhachHangE;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
public class PdfUtils {
    public static void xuatHoaDonPDF(HoaDonE hd, KhachHangE kh, List<ChiTietDonHangE> listCT, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Font hỗ trợ tiếng Việt
            BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(bf, 12);
            Font titleFont = new Font(bf, 16, Font.BOLD);

            // Tiêu đề
            Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Thông tin khách hàng
            document.add(new Paragraph("Mã hóa đơn: " + hd.getMaHD(), font));
            document.add(new Paragraph("Khách hàng: " + kh.getTenKH(), font));
            document.add(new Paragraph("SĐT: " + kh.getSoDienThoai(), font));
            document.add(new Paragraph("Địa chỉ: " + kh.getDiaChi(), font));
            document.add(new Paragraph("Ngày đặt hàng: " + hd.getNgayDatHang(), font));
            document.add(new Paragraph("Ngày thanh toán: " + hd.getNgayThanhToan(), font));
            document.add(new Paragraph("Trạng thái: " + hd.getTrangThai(), font));
            document.add(new Paragraph(" "));

            // Bảng chi tiết
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 1, 2, 2, 2});

            addCell(table, "Tên sản phẩm", font);
            addCell(table, "SL", font);
            addCell(table, "Đơn vị", font);
            addCell(table, "Đơn giá", font);
            addCell(table, "Thành tiền", font);

            BigDecimal tongTien = BigDecimal.ZERO;
            for (ChiTietDonHangE c : listCT) {
                BigDecimal thanhTien = c.getGiaXuat().multiply(BigDecimal.valueOf(c.getSoLuong()));
                tongTien = tongTien.add(thanhTien);

                addCell(table, c.getTenSanPham(), font);
                addCell(table, String.valueOf(c.getSoLuong()), font);
                addCell(table, c.getDonViTinh(), font);
                addCell(table, formatTien(c.getGiaXuat(), false), font);
                addCell(table, formatTien(thanhTien, false), font);
            }

            document.add(table);
            document.add(new Paragraph(" "));

            Paragraph tong = new Paragraph("TỔNG TIỀN: " + formatTien(tongTien, true), font);
            tong.setAlignment(Element.ALIGN_RIGHT);
            document.add(tong);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi xuất PDF: " + e.getMessage());
        }
    }

    private static void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private static String formatTien(BigDecimal value, boolean hasSuffix) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(value) + (hasSuffix ? " VNĐ" : "");
    }
}
