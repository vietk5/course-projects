package com.demo.controller;

import com.demo.enums.TrangThaiDonHang;
import com.demo.model.KhachHang;
import com.demo.model.SanPham;
import com.demo.model.cart.GioHangItem;
import com.demo.model.order.ChiTietDonHang;
import com.demo.model.order.DonHang;
import com.demo.model.session.PendingOrder;
import com.demo.persistence.DonHangDAO;
import com.demo.persistence.KhachHangDAO;
import com.demo.persistence.SanPhamDAO;
import com.demo.util.TransactionTracker;
import com.demo.util.VNPayConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet("/payment-return")
public class VNPayReturnServlet extends HttpServlet {

    private final DonHangDAO donHangDAO = new DonHangDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession();

        // DEBUG: Log session info
        System.out.println("=== VNPAY RETURN CALLBACK ===");
        System.out.println("Session ID: " + session.getId());

        // 1. Lấy tất cả tham số từ VNPAY
        Map<String, String> fields = new HashMap<>();
        for (String key : request.getParameterMap().keySet()) {
            String value = request.getParameter(key);
            if (value != null && !value.isEmpty()) {
                fields.put(key, value);
            }
        }

        // 2. Lấy chữ ký từ VNPAY
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");

        // 3. Loại bỏ các trường không cần để tính hash
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // 4. Tính toán hash để xác thực
        String hashData = VNPayConfig.hashAllFields(fields);
        String calculatedHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData);

        // 5. Lấy thông tin giao dịch
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_Amount = request.getParameter("vnp_Amount");
        String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");

        // 6. Kiểm tra chữ ký hợp lệ
        if (!calculatedHash.equals(vnp_SecureHash)) {
            request.setAttribute("message", "Lỗi: Chữ ký không hợp lệ!");
            request.setAttribute("success", false);
            request.getRequestDispatcher("/WEB-INF/views/payment_result.jsp").forward(request, response);
            return;
        }

        // 7. Lấy pending order từ session
        PendingOrder pendingOrder = (PendingOrder) session.getAttribute("pendingOrder");

        if (pendingOrder == null) {
            request.setAttribute("message", "Không tìm thấy thông tin đơn hàng!");
            request.setAttribute("success", false);
            request.getRequestDispatcher("/WEB-INF/views/payment_result.jsp").forward(request, response);
            return;
        }

        // 8. Kiểm tra mã giao dịch có khớp không
        if (!vnp_TxnRef.equals(pendingOrder.getTxnRef())) {
            request.setAttribute("message", "Mã giao dịch không khớp!");
            request.setAttribute("success", false);
            request.getRequestDispatcher("/WEB-INF/views/payment_result.jsp").forward(request, response);
            return;
        }

        // 9. Xử lý theo mã phản hồi
        if ("00".equals(vnp_ResponseCode)) {
            // KIỂM TRA DUPLICATE TRANSACTION
            if (TransactionTracker.isProcessed(vnp_TxnRef)) {
                request.setAttribute("success", false);
                request.setAttribute("message", "Giao dịch này đã được xử lý trước đó!");
                request.getRequestDispatcher("/WEB-INF/views/payment_result.jsp").forward(request, response);
                return;
            }

            // GIAO DỊCH THÀNH CÔNG - TẠO ĐƠN HÀNG
            try {
                KhachHang khachHang = khachHangDAO.find(pendingOrder.getUserId());

                if (khachHang == null) {
                    throw new Exception("Không tìm thấy thông tin khách hàng");
                }

                // Tạo đơn hàng
                DonHang donHang = new DonHang();
                donHang.setKhachHang(khachHang);
                donHang.setNgayDatHang(LocalDateTime.now());
                donHang.setTrangThai(TrangThaiDonHang.DA_THANH_TOAN); // Đã thanh toán online

                List<ChiTietDonHang> chiTietList = new ArrayList<>();
                List<GioHangItem> cart = pendingOrder.getCartItems();

                // Kiểm tra tồn kho và tạo chi tiết đơn hàng
                for (GioHangItem item : cart) {
                    long sanPhamId = Long.valueOf(item.getSku().split("-")[1]);
                    SanPham sanPham = sanPhamDAO.find(sanPhamId);

                    if (sanPham == null) {
                        throw new Exception("Sản phẩm '" + item.getTen() + "' không tồn tại");
                    }

                    if (sanPham.getSoLuongTon() < item.getSoLuong()) {
                        throw new Exception("Sản phẩm '" + item.getTen() + "' không đủ số lượng trong kho");
                    }

                    ChiTietDonHang chiTiet = new ChiTietDonHang();
                    chiTiet.setDonHang(donHang);
                    chiTiet.setSanPham(sanPham);
                    chiTiet.setSoLuong(item.getSoLuong());
                    chiTiet.setDonGia(sanPham.getGia());
                    chiTietList.add(chiTiet);

                    // Trừ kho
                    sanPham.setSoLuongTon(sanPham.getSoLuongTon() - item.getSoLuong());
                    sanPhamDAO.update(sanPham);
                }

                donHang.setChiTiet(chiTietList);
                donHangDAO.save(donHang);

                // Đánh dấu transaction đã xử lý
                TransactionTracker.markAsProcessed(vnp_TxnRef);

                // GỬI EMAIL XÁC NHẬN
                try {
                    boolean emailSent = CheckoutService.sendOrderConfirmation(donHang, "vnpay");
                    if (emailSent) {
                        System.out.println("Order confirmation email sent for order #" + donHang.getId());
                    }
                } catch (Exception emailEx) {
                    emailEx.printStackTrace();
                    // Không ảnh hưởng đến flow chính
                }

                // Xóa giỏ hàng và pending order
                session.removeAttribute("cart");
                session.removeAttribute("pendingOrder");

                // Set thông tin để hiển thị
                request.setAttribute("success", true);
                request.setAttribute("message", "Thanh toán thành công!");
                request.setAttribute("orderId", donHang.getId());
                request.setAttribute("transactionNo", vnp_TransactionNo);
                request.setAttribute("amount", Long.parseLong(vnp_Amount) / 100);

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("success", false);
                request.setAttribute("message", "Lỗi: " + e.getMessage());
                // Rollback: Xóa khỏi processed list
                TransactionTracker.remove(vnp_TxnRef);
                // Không xóa pendingOrder để có thể retry
            }

        } else {
            // GIAO DỊCH THẤT BẠI
            String errorMessage = getErrorMessage(vnp_ResponseCode);
            request.setAttribute("success", false);
            request.setAttribute("message", "Thanh toán thất bại: " + errorMessage);
            request.setAttribute("responseCode", vnp_ResponseCode);

            // Xóa pending order
            session.removeAttribute("pendingOrder");
        }

        // Forward đến trang kết quả
        request.getRequestDispatcher("/WEB-INF/views/payment_result.jsp").forward(request, response);
    }

    /**
     * Lấy thông báo lỗi theo mã response code của VNPAY
     */
    private String getErrorMessage(String responseCode) {
        switch (responseCode) {
            case "07":
                return "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
            case "09":
                return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.";
            case "10":
                return "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11":
                return "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "12":
                return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.";
            case "13":
                return "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP).";
            case "24":
                return "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "51":
                return "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.";
            case "65":
                return "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.";
            case "75":
                return "Ngân hàng thanh toán đang bảo trì.";
            case "79":
                return "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định.";
            default:
                return "Giao dịch thất bại";
        }
    }
}
