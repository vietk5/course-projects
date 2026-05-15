package com.demo.controller;

import com.demo.model.TokenForgetPassword;
import com.demo.model.KhachHang;
import com.demo.persistence.TokenForgetPasswordDAO;
import com.demo.persistence.KhachHangDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Servlet xử lý khi người dùng nhấn vào link reset mật khẩu trong email
 */
@WebServlet(name = "resetPassword", urlPatterns = {"/resetPassword"})
public class resetPassword extends HttpServlet {

    private TokenForgetPasswordDAO tokenDAO = new TokenForgetPasswordDAO();
    private KhachHangDAO khachHangDAO = new KhachHangDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tokenParam = request.getParameter("token");
        HttpSession session = request.getSession();
        resetService service = new resetService();

        if (tokenParam == null || tokenParam.isEmpty()) {
            request.setAttribute("mess", "Token không hợp lệ!");
            request.getRequestDispatcher("/WEB-INF/views/requestPassword.jsp").forward(request, response);
            return;
        }

        // Tìm token trong DB
        TokenForgetPassword token = tokenDAO.findByToken(tokenParam);

        if (token == null) {
            request.setAttribute("mess", "Token không tồn tại!");
            request.getRequestDispatcher("/WEB-INF/views/requestPassword.jsp").forward(request, response);
            return;
        }

        if (token.isUsed()) {
            request.setAttribute("mess", "Token đã được sử dụng!");
            request.getRequestDispatcher("/WEB-INF/views/requestPassword.jsp").forward(request, response);
            return;
        }

        if (service.isExpireTime(token.getExpiryDatetime())) {
            request.setAttribute("mess", "Token đã hết hạn!");
            request.getRequestDispatcher("/WEB-INF/views/requestPassword.jsp").forward(request, response);
            return;
        }

        // Nếu token hợp lệ → hiển thị trang nhập mật khẩu mới
        KhachHang kh = token.getKhachHang();
        request.setAttribute("email", kh.getEmail());
        session.setAttribute("token", token.getToken());
        request.getRequestDispatcher("/WEB-INF/views/reset-forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        if (email == null || password == null || confirmPassword == null) {
            request.setAttribute("mess", "Thiếu thông tin!");
            request.getRequestDispatcher("/WEB-INF/views/resetPassword.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("mess", "Mật khẩu nhập lại không khớp!");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/resetPassword.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        String tokenStr = (String) session.getAttribute("token");

        if (tokenStr == null) {
            request.setAttribute("mess", "Phiên làm việc không hợp lệ, vui lòng yêu cầu lại email!");
            request.getRequestDispatcher("/WEB-INF/views/requestPassword.jsp").forward(request, response);
            return;
        }

        TokenForgetPassword token = tokenDAO.findByToken(tokenStr);
        resetService service = new resetService();

        if (token == null || token.isUsed() || service.isExpireTime(token.getExpiryDatetime())) {
            request.setAttribute("mess", "Token không hợp lệ hoặc đã hết hạn!");
            request.getRequestDispatcher("/WEB-INF/views/requestPassword.jsp").forward(request, response);
            return;
        }

        // Cập nhật mật khẩu mới
        KhachHang kh = token.getKhachHang();
        kh.setMatKhau(password);
        khachHangDAO.update(kh);

        // Đánh dấu token đã được sử dụng
        token.setUsed(true);
        tokenDAO.delete(token);

        request.setAttribute("mess", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.");
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Xử lý đặt lại mật khẩu bằng token trong email";
    }
}
