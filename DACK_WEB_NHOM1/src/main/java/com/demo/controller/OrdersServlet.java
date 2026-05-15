//package com.demo.controller;
//
//import com.demo.enums.TrangThaiDonHang;
//import com.demo.model.order.DonHang;
//import com.demo.model.session.SessionUser;
//import com.demo.persistence.DonHangDAO;
//import com.demo.persistence.GenericDAO.Page;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.*;
//import java.io.IOException;
//
//@WebServlet(name = "OrdersServlet", urlPatterns = {"/orders"})
//public class OrdersServlet extends HttpServlet {
//
//    private final DonHangDAO donHangDAO = new DonHangDAO();
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//
//        req.setCharacterEncoding("UTF-8");
//        resp.setCharacterEncoding("UTF-8");
//
//        HttpSession session = req.getSession();
//        SessionUser user = (SessionUser) session.getAttribute("user");
//        if (user == null) {
//            resp.sendRedirect(req.getContextPath() + "/login");
//            return;
//        }
//
//        // --- Lấy trạng thái lọc từ query param ---
//        String statusParam = req.getParameter("status");
//        TrangThaiDonHang status = null;
//
//        if (statusParam != null && !statusParam.isEmpty()) {
//            switch (statusParam) {
//                case "HOAN_THANH":
//                    status = TrangThaiDonHang.HOAN_TAT;
//                    break;
//                case "HUY":
//                    status = TrangThaiDonHang.DA_HUY;
//                    break;
//                default:
//                    try {
//                        status = TrangThaiDonHang.valueOf(statusParam);
//                    } catch (IllegalArgumentException ignored) {}
//                    break;
//            }
//        }
//
//        // --- Phân trang ---
//        int page = 0;
//        int size = 5;
//        try {
//            String pageParam = req.getParameter("page");
//            if (pageParam != null)
//                page = Math.max(0, Integer.parseInt(pageParam));
//        } catch (Exception ignored) {}
//
//        // --- Lấy danh sách đơn hàng ---
//        Page<DonHang> orderPage = donHangDAO.byCustomer(user.getId(), status, page, size);
//
//        // --- Gửi dữ liệu sang JSP ---
//        req.setAttribute("orders", orderPage.getContent());
//        req.setAttribute("filterStatus", statusParam);
//        req.setAttribute("currentPage", page);
//        req.setAttribute("totalPages", orderPage.getTotalPages());
//        req.setAttribute("totalOrders", orderPage.getTotalElements());
//
//        req.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(req, resp);
//    }
//}


package com.demo.controller;

import com.demo.enums.TrangThaiDonHang;
import com.demo.model.order.DonHang;
import com.demo.model.session.SessionUser;
import com.demo.persistence.DonHangDAO;
import com.demo.persistence.GenericDAO.Page;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "OrdersServlet", urlPatterns = {"/orders"})
public class OrdersServlet extends HttpServlet {

    private final DonHangDAO donHangDAO = new DonHangDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // --- TOÀN BỘ CHỨC NĂNG CŨ CỦA BẠN ĐỂ XEM ĐƠN HÀNG - GIỮ NGUYÊN ---
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String statusParam = req.getParameter("status");
        TrangThaiDonHang status = null;

        if (statusParam != null && !statusParam.isEmpty()) {
            switch (statusParam) {
                case "HOAN_THANH":
                    status = TrangThaiDonHang.HOAN_TAT;
                    break;
                case "HUY":
                    status = TrangThaiDonHang.DA_HUY;
                    break;
                default:
                    try {
                        status = TrangThaiDonHang.valueOf(statusParam);
                    } catch (IllegalArgumentException ignored) {}
                    break;
            }
        }

        int page = 0;
        int size = 5;
        try {
            String pageParam = req.getParameter("page");
            if (pageParam != null)
                page = Math.max(0, Integer.parseInt(pageParam));
        } catch (Exception ignored) {}

        Page<DonHang> orderPage = donHangDAO.byCustomer(user.getId(), status, page, size);

        req.setAttribute("orders", orderPage.getContent());
        req.setAttribute("filterStatus", statusParam);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", orderPage.getTotalPages());
        req.setAttribute("totalOrders", orderPage.getTotalElements());

        req.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(req, resp);
    }
    
    /**
     * [HÀM MỚI ĐƯỢC THÊM VÀO]
     * Xử lý các yêu cầu POST, cụ thể là hành động hủy đơn hàng.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("cancel".equals(action)) {
            HttpSession session = req.getSession();
            SessionUser user = (SessionUser) session.getAttribute("user");
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            try {
                Long orderId = Long.valueOf(req.getParameter("orderId"));
                DonHang donHang = donHangDAO.find(orderId);

                // Kiểm tra an toàn: Đơn hàng phải tồn tại, thuộc về người dùng và có trạng thái MOI
                if (donHang != null && donHang.getKhachHang().getId().equals(user.getId()) && donHang.getTrangThai() == TrangThaiDonHang.MOI) {
                    donHang.setTrangThai(TrangThaiDonHang.DA_HUY);
                    donHangDAO.update(donHang);
                    resp.sendRedirect(req.getContextPath() + "/orders?cancel_success=true");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/orders?cancel_error=true");
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendRedirect(req.getContextPath() + "/orders?cancel_error=true");
            }
        }
    }
}