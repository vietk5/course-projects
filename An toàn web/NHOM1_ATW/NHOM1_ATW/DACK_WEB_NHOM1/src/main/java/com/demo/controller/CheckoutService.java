package com.demo.controller;

import com.demo.model.order.ChiTietDonHang;
import com.demo.model.order.DonHang;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;

/**
 * Service để gửi email xác nhận đơn hàng - FIXED VERSION
 */
public class CheckoutService {

    // CẤU HÌNH EMAIL
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "shoplinhkien161025@gmail.com";
    private static final String EMAIL_PASSWORD = "avan kxwi pkjd mkrb";
    private static final String FROM_NAME = "ElectroMart";

    /**
     * Gửi email xác nhận đơn hàng
     */
    public static boolean sendOrderConfirmation(DonHang donHang, String paymentMethod) {
        try {
            System.out.println("=== SENDING EMAIL ===");
            System.out.println("To: " + donHang.getKhachHang().getEmail());
            System.out.println("Payment Method: " + paymentMethod);

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });

            // Bật debug để xem chi tiết lỗi
            session.setDebug(true);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(donHang.getKhachHang().getEmail()));
            message.setSubject("Xác nhận đơn hàng #" + donHang.getId() + " - ElectroMart");

            String htmlContent = buildEmailContent(donHang, paymentMethod);
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);

            System.out.println("✅ Email sent successfully!");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to send email: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            return false;
        }
    }

    /**
     * Tạo nội dung email HTML - FIXED VERSION
     */
    private static String buildEmailContent(DonHang donHang, String paymentMethod) {
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Tính tổng tiền - XỬ LÝ CẢ BigDecimal VÀ Long
        long totalAmount = 0;
        try {
            for (ChiTietDonHang ct : donHang.getChiTiet()) {
                // Kiểm tra kiểu dữ liệu của đơn giá
                Object donGia = ct.getDonGia();
                long price = 0;

                if (donGia instanceof BigDecimal) {
                    price = ((BigDecimal) donGia).longValue();
                } else if (donGia instanceof Long) {
                    price = (Long) donGia;
                } else if (donGia instanceof Integer) {
                    price = ((Integer) donGia).longValue();
                }

                totalAmount += price * ct.getSoLuong();
            }
        } catch (Exception e) {
            System.err.println("Error calculating total: " + e.getMessage());
        }

        String paymentMethodName = "cod".equals(paymentMethod)
                ? "Thanh toán khi nhận hàng (COD)" : "Thanh toán qua VNPAY";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='vi'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 0; }");
        html.append(".container { max-width: 600px; margin: 20px auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        html.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }");
        html.append(".header h1 { margin: 0; font-size: 24px; }");
        html.append(".content { padding: 30px; }");
        html.append(".info-box { background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0; }");
        html.append(".info-row { display: flex; justify-content: space-between; margin: 8px 0; }");
        html.append(".label { font-weight: bold; color: #555; }");
        html.append(".value { color: #333; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append("th { background: #667eea; color: white; padding: 12px; text-align: left; }");
        html.append("td { padding: 12px; border-bottom: 1px solid #ddd; }");
        html.append(".total-row { font-weight: bold; font-size: 18px; background: #f8f9fa; }");
        html.append(".footer { background: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 14px; }");
        html.append(".success-badge { background: #28a745; color: white; padding: 8px 16px; border-radius: 20px; display: inline-block; margin: 10px 0; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");

        html.append("<div class='container'>");

        // Header
        html.append("<div class='header'>");
        html.append("<h1>✅ Đặt hàng thành công!</h1>");
        html.append("<p style='margin: 10px 0 0 0;'>Cảm ơn bạn đã mua sắm tại ElectroMart</p>");
        html.append("</div>");

        // Content
        html.append("<div class='content'>");

        // Thông tin đơn hàng
        html.append("<div class='info-box'>");
        html.append("<h3 style='margin-top: 0; color: #667eea;'>📋 Thông tin đơn hàng</h3>");
        html.append("<div class='info-row'>");
        html.append("<span class='label'>Mã đơn hàng:</span>");
        html.append("<span class='value'>#").append(donHang.getId()).append("</span>");
        html.append("</div>");
        html.append("<div class='info-row'>");
        html.append("<span class='label'>Ngày đặt hàng:</span>");
        html.append("<span class='value'>").append(donHang.getNgayDatHang().format(dateFormatter)).append("</span>");
        html.append("</div>");
        html.append("<div class='info-row'>");
        html.append("<span class='label'>Phương thức thanh toán:</span>");
        html.append("<span class='value'>").append(paymentMethodName).append("</span>");
        html.append("</div>");
        html.append("<div class='info-row'>");
        html.append("<span class='label'>Trạng thái:</span>");
        html.append("<span class='success-badge'>").append(donHang.getTrangThai().getDisplayName()).append("</span>");
        html.append("</div>");
        html.append("</div>");

        // Thông tin người nhận
        html.append("<div class='info-box'>");
        html.append("<h3 style='margin-top: 0; color: #667eea;'>👤 Thông tin người nhận</h3>");
        html.append("<div class='info-row'>");
        html.append("<span class='label'>Họ tên:</span>");
        html.append("<span class='value'>").append(donHang.getKhachHang().getTen()).append("</span>");
        html.append("</div>");
        html.append("<div class='info-row'>");
        html.append("<span class='label'>Số điện thoại:</span>");

        // FIX: Xử lý cả 2 tên method
        try {
            html.append("<span class='value'>").append(donHang.getKhachHang().getSdt()).append("</span>");
        } catch (Exception e) {
            try {
                html.append("<span class='value'>").append(donHang.getKhachHang().getSdt()).append("</span>");
            } catch (Exception ex) {
                html.append("<span class='value'>N/A</span>");
            }
        }

        html.append("</div>");
        html.append("<div class='info-row'>");
        html.append("<span class='label'>Email:</span>");
        html.append("<span class='value'>").append(donHang.getKhachHang().getEmail()).append("</span>");
        html.append("</div>");
        html.append("<div class='info-row'>");
        html.append("<span class='label'>Địa chỉ:</span>");
        html.append("<span class='value'>").append(donHang.getKhachHang().getDiaChi() != null ? donHang.getKhachHang().getDiaChi() : "N/A").append("</span>");
        html.append("</div>");
        html.append("</div>");

        // Chi tiết sản phẩm
        html.append("<h3 style='color: #667eea;'>🛍️ Chi tiết sản phẩm</h3>");
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th>Sản phẩm</th>");
        html.append("<th style='text-align: center;'>SL</th>");
        html.append("<th style='text-align: right;'>Đơn giá</th>");
        html.append("<th style='text-align: right;'>Thành tiền</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");

        for (ChiTietDonHang ct : donHang.getChiTiet()) {
            // XỬ LÝ KIỂU DỮ LIỆU
            Object donGia = ct.getDonGia();
            long price = 0;

            if (donGia instanceof BigDecimal) {
                price = ((BigDecimal) donGia).longValue();
            } else if (donGia instanceof Long) {
                price = (Long) donGia;
            } else if (donGia instanceof Integer) {
                price = ((Integer) donGia).longValue();
            }

            long subtotal = price * ct.getSoLuong();

            html.append("<tr>");

            // FIX: Xử lý tên sản phẩm
            String tenSanPham = "N/A";
            try {
                tenSanPham = ct.getSanPham().getTenSanPham();
            } catch (Exception e) {
                try {
                    tenSanPham = ct.getSanPham().getTenSanPham();
                } catch (Exception ex) {
                    System.err.println("Cannot get product name");
                }
            }

            html.append("<td>").append(tenSanPham).append("</td>");
            html.append("<td style='text-align: center;'>").append(ct.getSoLuong()).append("</td>");
            html.append("<td style='text-align: right;'>").append(currencyFormat.format(price)).append(" đ</td>");
            html.append("<td style='text-align: right;'>").append(currencyFormat.format(subtotal)).append(" đ</td>");
            html.append("</tr>");
        }

        html.append("<tr class='total-row'>");
        html.append("<td colspan='3' style='text-align: right;'>TỔNG CỘNG:</td>");
        html.append("<td style='text-align: right; color: #667eea;'>").append(currencyFormat.format(totalAmount)).append(" đ</td>");
        html.append("</tr>");
        html.append("</tbody>");
        html.append("</table>");

        // Lưu ý
        html.append("<div style='background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin-top: 20px;'>");
        html.append("<p style='margin: 0;'><strong>📌 Lưu ý:</strong></p>");
        html.append("<ul style='margin: 10px 0 0 0; padding-left: 20px;'>");

        if ("cod".equals(paymentMethod)) {
            html.append("<li>Đơn hàng sẽ được giao trong vòng 2-3 ngày làm việc</li>");
            html.append("<li>Vui lòng chuẩn bị số tiền <strong>").append(currencyFormat.format(totalAmount)).append(" đ</strong> để thanh toán khi nhận hàng</li>");
        } else {
            html.append("<li>Bạn đã thanh toán thành công qua VNPAY</li>");
            html.append("<li>Đơn hàng sẽ được xử lý và giao trong vòng 2-3 ngày làm việc</li>");
        }

        html.append("<li>Bạn có thể theo dõi đơn hàng tại: <a href='http://localhost:8080/orders'>Đơn hàng của tôi</a></li>");
        html.append("</ul>");
        html.append("</div>");

        html.append("</div>"); // End content

        // Footer
        html.append("<div class='footer'>");
        html.append("<p>Cảm ơn bạn đã tin tưởng và mua sắm tại <strong>ElectroMart</strong></p>");
        html.append("<p>Hotline hỗ trợ: <strong>1900-xxxx</strong> | Email: <strong>support@electromart.vn</strong></p>");
        html.append("<p style='font-size: 12px; color: #999; margin-top: 15px;'>Đây là email tự động, vui lòng không trả lời email này.</p>");
        html.append("</div>");

        html.append("</div>"); // End container
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Test method - Dùng để kiểm tra cấu hình
     */
    public static void main(String[] args) {
        System.out.println("=== Email Configuration ===");
        System.out.println("SMTP Host: " + SMTP_HOST);
        System.out.println("SMTP Port: " + SMTP_PORT);
        System.out.println("Username: " + EMAIL_USERNAME);
        System.out.println("Password: " + (EMAIL_PASSWORD != null ? "****" : "NOT SET"));
        System.out.println("===========================");
    }
}
