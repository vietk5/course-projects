package com.demo.controller;

import com.demo.model.KhachHang;
import com.demo.persistence.KhachHangDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private final KhachHangDAO khDAO = new KhachHangDAO();
    
    // Lưu token reset password tạm (production nên dùng database)
    private static final ConcurrentHashMap<String, String> resetTokens = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String token = req.getParameter("token");
        
        if (token != null && !token.isEmpty()) {
            // Hiển thị form reset password
            String email = resetTokens.get(token);
            if (email != null) {
                req.setAttribute("token", token);
                req.setAttribute("email", email);
                req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Link đã hết hạn hoặc không hợp lệ!");
                req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
            }
        } else {
            // Hiển thị form nhập email
            req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("sendReset".equals(action)) {
            sendResetLink(req, resp);
        } else if ("resetPassword".equals(action)) {
            resetPassword(req, resp);
        } else {
            doGet(req, resp);
        }
    }

    private void sendResetLink(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String email = req.getParameter("email");
        
        if (email == null || email.isBlank()) {
            req.setAttribute("error", "Vui lòng nhập email.");
            req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
            return;
        }

        Optional<KhachHang> khOpt = khDAO.findByEmail(email);
        
        if (khOpt.isEmpty()) {
            // Không tiết lộ email có tồn tại hay không (bảo mật)
            req.setAttribute("success", 
                "Nếu email tồn tại, link đặt lại mật khẩu đã được gửi. Kiểm tra email của bạn!");
            req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
            return;
        }

        // Tạo token reset
        String token = UUID.randomUUID().toString();
        resetTokens.put(token, email);

        // Trong thực tế, gửi email ở đây
        // Tạm thời hiển thị link trên màn hình
        String resetLink = req.getContextPath() + "/forgot-password?token=" + token;
        
        req.setAttribute("success", 
            "Link đặt lại mật khẩu: <a href='" + resetLink + "'>" + resetLink + "</a>");
        req.setAttribute("showLink", true);
        req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
    }

    private void resetPassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        String email = resetTokens.get(token);
        if (email == null) {
            req.setAttribute("error", "Token không hợp lệ hoặc đã hết hạn!");
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            req.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            req.setAttribute("token", token);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            req.setAttribute("token", token);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }

        // Cập nhật mật khẩu
        Optional<KhachHang> khOpt = khDAO.findByEmail(email);
        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            kh.setMatKhau(newPassword);
            khDAO.update(kh);

            // Xóa token
            resetTokens.remove(token);

            req.setAttribute("success", "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập ngay.");
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
        } else {
            req.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại!");
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
        }
    }
}

