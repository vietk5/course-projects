package com.demo.controller;

import com.demo.model.KhachHang;
import com.demo.model.TokenForgetPassword;
import com.demo.persistence.KhachHangDAO;
import com.demo.persistence.TokenForgetPasswordDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servlet xử lý yêu cầu đặt lại mật khẩu
 */
@WebServlet(name = "requestPassword", urlPatterns = {"/requestPassword"})
public class requestPassword extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị trang nhập email
        request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        // Khởi tạo DAO & service
        KhachHangDAO khachHangDAO = new KhachHangDAO();
        TokenForgetPasswordDAO tokenDAO = new TokenForgetPasswordDAO();
        resetService resetService = new resetService();

        //Kiểm tra email có tồn tại không
        Optional<KhachHang> optionalKh = khachHangDAO.findByEmail(email);
        if (optionalKh.isEmpty()) {
            request.setAttribute("error", "Email không tồn tại. Vui lòng nhập lại!");
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            return;
        }

        KhachHang kh = optionalKh.get();

        //Sinh token và thời gian hết hạn
        String token = resetService.generateToken();
        LocalDateTime expiryTime = resetService.expireDatetime();

        //Tạo entity TokenForgetPassword mới
        TokenForgetPassword tokenForget = new TokenForgetPassword();
        tokenForget.setKhachHang(kh);           // liên kết trực tiếp entity (chứ không chỉ ID)
        tokenForget.setUsed(false);
        tokenForget.setToken(token);
        tokenForget.setExpiryDatetime(expiryTime);

        // Lưu token vào DB
        boolean isSaved = tokenDAO.insertTokenForget(tokenForget);
        if (!isSaved) {
            request.setAttribute("error", "Không thể tạo token đặt lại mật khẩu. Vui lòng thử lại!");
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            return;
        }

        //Gửi email chứa link đặt lại mật khẩu
        String linkReset = "http://localhost:8080/resetPassword?token=" + token;
        boolean isSent = resetService.sendEmail(email, linkReset, kh.getHoTen());

        if (!isSent) {
            request.setAttribute("error", "Không thể gửi email đặt lại mật khẩu. Vui lòng thử lại!");
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            return;
        }

        //Nếu thành công
        request.setAttribute("success", "Đã gửi yêu cầu đặt lại mật khẩu đến email của bạn!");
        request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet xử lý yêu cầu đặt lại mật khẩu";
    }
}
