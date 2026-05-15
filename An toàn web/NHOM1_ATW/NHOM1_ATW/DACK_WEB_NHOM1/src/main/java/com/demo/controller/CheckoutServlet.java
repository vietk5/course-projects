package com.demo.controller;

import com.demo.enums.TrangThaiDonHang;
import com.demo.model.KhachHang;
import com.demo.model.SanPham;
import com.demo.model.cart.GioHangItem;
import com.demo.model.order.ChiTietDonHang;
import com.demo.model.order.DonHang;
import com.demo.model.session.PendingOrder;
import com.demo.model.session.SessionUser;
import com.demo.persistence.DonHangDAO;
import com.demo.persistence.KhachHangDAO;
import com.demo.persistence.SanPhamDAO;
import com.demo.util.VNPayConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    private final DonHangDAO donHangDAO = new DonHangDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null || user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<GioHangItem> cart = (List<GioHangItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // TỰ ĐỘNG ĐIỀN THÔNG TIN TỪ PROFILE
        try {
            KhachHang khachHang = khachHangDAO.find(user.getId());
            if (khachHang != null) {
                // Set thông tin vào request để hiển thị trên form
                req.setAttribute("fullName", khachHang.getTen());
                req.setAttribute("phone", khachHang.getSdt());
                req.setAttribute("email", khachHang.getEmail());
                req.setAttribute("address", khachHang.getDiaChi());

                System.out.println("Auto-filled profile for user: " + khachHang.getEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu lỗi thì vẫn cho phép user nhập thủ công
        }

        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null || user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String action = req.getParameter("action");

        if ("buy_now".equals(action)) {
            Long productId = Long.valueOf(req.getParameter("productId").trim());
            SanPham product = sanPhamDAO.find(productId);
            int soLuong = parseInt(req.getParameter("qty"));
            List<GioHangItem> buyNowCartNew = new ArrayList<>();
            buyNowCartNew.add(new GioHangItem(
                    "SP-" + productId,
                    product.getTenSanPham(),
                    "assets/img/products/" + productId + ".jpg",
                    product.getGia().longValue(),
                    soLuong
            ));
            session.setAttribute("buyNowCart", buyNowCartNew);
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
            return;
        }

        List<GioHangItem> cart = (List<GioHangItem>) session.getAttribute("cart");
        List<GioHangItem> buyNowCart = (List<GioHangItem>) session.getAttribute("buyNowCart");
        if (buyNowCart != null) {
            cart = buyNowCart;
        }
        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        if ("checkoutSelected".equals(action)) {
            String[] selectedItems = req.getParameterValues("selectedItems");
            if (selectedItems == null || selectedItems.length == 0) {
                req.setAttribute("error", "Vui lòng chọn ít nhất một sản phẩm để thanh toán.");
                req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
                return;
            }

            List<GioHangItem> selectedCart = new ArrayList<>();

            for (String s : selectedItems) {
                final String skuFinal = s;
                cart.stream()
                        .filter(i -> i.getSku().equals(skuFinal))
                        .findFirst()
                        .ifPresent(selectedCart::add);
            }
            session.setAttribute("selectedCart", selectedCart);
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }

        if ("applyVoucher".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
            return;
        }
        // payment
        String paymentMethod = req.getParameter("paymentMethod");
        if ("placeOrder".equals(action)) {
            if ("cod".equals(paymentMethod)) {
                processCOD(req, resp);
            } else if ("vnpay".equals(paymentMethod)) {
                processVNPAY(req, resp);
            } else {
                req.setAttribute("error", "Vui lòng chọn phương thức thanh toán");
                req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
            }
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    /**
     * Xử lý thanh toán COD
     */
    @SuppressWarnings("unchecked")
    private void processCOD(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession session = req.getSession();
        SessionUser user = (SessionUser) session.getAttribute("user");

        List<GioHangItem> selectedCart = (List<GioHangItem>) session.getAttribute("selectedCart");
        List<GioHangItem> buyNowCart = (List<GioHangItem>) session.getAttribute("buyNowCart");
        if (buyNowCart != null) {
            selectedCart = buyNowCart;
        }
        if (selectedCart == null || selectedCart.isEmpty()) {
            selectedCart = (List<GioHangItem>) session.getAttribute("cart");
        }

        try {
            KhachHang khachHang = khachHangDAO.find(user.getId());
            if (khachHang == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            DonHang donHang = new DonHang();
            donHang.setKhachHang(khachHang);
            donHang.setNgayDatHang(LocalDateTime.now());
            donHang.setTrangThai(TrangThaiDonHang.MOI);

            List<ChiTietDonHang> chiTietList = new ArrayList<>();
            long totalAmount = 0;

            for (GioHangItem item : selectedCart) {
                long sanPhamId = Long.valueOf(item.getSku().split("-")[1]);
                SanPham sanPham = sanPhamDAO.find(sanPhamId);

                if (sanPham == null || sanPham.getSoLuongTon() < item.getSoLuong()) {
                    req.setAttribute("error", "Sản phẩm '" + item.getTen() + "' không đủ số lượng trong kho.");
                    req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
                    return;
                }

                ChiTietDonHang chiTiet = new ChiTietDonHang();
                chiTiet.setDonHang(donHang);
                chiTiet.setSanPham(sanPham);
                chiTiet.setSoLuong(item.getSoLuong());
                chiTiet.setDonGia(sanPham.getGia());
                chiTietList.add(chiTiet);

                sanPham.setSoLuongTon(sanPham.getSoLuongTon() - item.getSoLuong());
                sanPhamDAO.update(sanPham);

                totalAmount += item.getGia() * item.getSoLuong();
            }

            donHang.setChiTiet(chiTietList);
            donHangDAO.save(donHang);

            try {
                CheckoutService.sendOrderConfirmation(donHang, "cod");
            } catch (Exception ignored) {
            }

            // ✅ Xóa sản phẩm đã thanh toán khỏi giỏ hàng
            List<GioHangItem> cart = (List<GioHangItem>) session.getAttribute("cart");
            if (cart != null && selectedCart != null) {
                final List<GioHangItem> selectedFinal = selectedCart;
                cart.removeIf(i -> selectedFinal.stream().anyMatch(sel -> sel.getSku().equals(i.getSku())));
                session.setAttribute("cart", cart);
            }

            session.removeAttribute("selectedCart");
            session.removeAttribute("buyNowCart");

            resp.sendRedirect(req.getContextPath() + "/orders?checkout_success=true");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Đã có lỗi xảy ra trong quá trình tạo đơn hàng.");
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
        }
    }

    /**
     * Xử lý thanh toán qua VNPAY
     */
    @SuppressWarnings("unchecked")
    private void processVNPAY(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();
        SessionUser user = (SessionUser) session.getAttribute("user");

        List<GioHangItem> selectedCart = (List<GioHangItem>) session.getAttribute("selectedCart");
        List<GioHangItem> buyNowCart = (List<GioHangItem>) session.getAttribute("buyNowCart");
        if (buyNowCart != null) {
            selectedCart = buyNowCart;
        }
        if (selectedCart == null || selectedCart.isEmpty()) {
            selectedCart = (List<GioHangItem>) session.getAttribute("cart");
        }

        long totalAmount = 0;
        for (GioHangItem item : selectedCart) {
            totalAmount += item.getGia() * item.getSoLuong();
        }

        String amountStr = String.valueOf(totalAmount * 100);
        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        String vnp_IpAddr = VNPayConfig.getIpAddress(req);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", amountStr);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        String hashData = VNPayConfig.hashAllFields(vnp_Params);
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData);

        String queryUrl = hashData + "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        List<GioHangItem> cartCopy = new ArrayList<>();
        for (GioHangItem item : selectedCart) {
            GioHangItem cloned = new GioHangItem();
            cloned.setSku(item.getSku());
            cloned.setTen(item.getTen());
            cloned.setGia(item.getGia());
            cloned.setSoLuong(item.getSoLuong());
            cloned.setHinh(item.getHinh());
            cartCopy.add(cloned);
        }

        PendingOrder pendingOrder = new PendingOrder(
                vnp_TxnRef,
                user.getId(),
                cartCopy,
                totalAmount
        );
        session.setAttribute("pendingOrder", pendingOrder);
        session.removeAttribute("buyNowCart");

        resp.sendRedirect(paymentUrl);
    }
}
