package com.demo.controller;

import com.demo.model.Admin;
import com.demo.persistence.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name="AdminLoginServlet", urlPatterns={"/admin/login"})
public class AdminLoginServlet extends HttpServlet {
    private final AdminDAO adminDAO = new AdminDAO();
    private static final String DEFAULT_USER = "admindackweb";
    private static final String DEFAULT_PASS = "P@ssw0rd";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // seed admin mặc định nếu chưa có
        adminDAO.findByUsername(DEFAULT_USER).orElseGet(() -> {
            Admin a = new Admin();
            // TÙY ENTITY: nếu có field username => setUsername(DEFAULT_USER)
            a.setEmail(DEFAULT_USER);         // dùng email làm username
            a.setMatKhau(DEFAULT_PASS);       // bản dev: lưu plain; production nên băm password
            a.setTen("Quản trị hệ thống");
            return adminDAO.save(a);
        });
        req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getParameter("username");
        String pass = req.getParameter("password");

        var opt = adminDAO.findByUsername(user == null ? "" : user.trim());
        if (opt.isPresent() && opt.get().getMatKhau().equals(pass)) {
            HttpSession session = req.getSession(true);
            session.setAttribute("IS_ADMIN", true);
            session.setAttribute("ADMIN_ID", opt.get().getIdAdmin()); // TÙY ENTITY: getIdAdmin() / getId()
            resp.sendRedirect(req.getContextPath() + "/admin");
        } else {
            req.setAttribute("error", "Sai tài khoản hoặc mật khẩu!");
            req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
        }
    }
}
