package com.demo.controller;

import com.demo.enums.LoaiGiamGia;
import com.demo.model.LoaiSanPham;
import com.demo.model.PhieuGiamGia;
import com.demo.model.SanPham;
import com.demo.persistence.LoaiSanPhamDAO;
import com.demo.persistence.PhieuGiamGiaDAO;
import com.demo.persistence.SanPhamDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = "/admin/promos")
public class AdminPromosServlet extends HttpServlet {

    private PhieuGiamGiaDAO phieuGiamGiaDAO;
    private SanPhamDAO sanPhamDAO;
    private LoaiSanPhamDAO loaiSanPhamDAO;

    @Override
    public void init() {
        phieuGiamGiaDAO = new PhieuGiamGiaDAO();
        sanPhamDAO = new SanPhamDAO();
        loaiSanPhamDAO = new LoaiSanPhamDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "toggle":
                handleToggle(req, resp);
                return;
            case "delete":
                handleDelete(req, resp);
                return;
        }

        showPromoPage(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleSave(req, resp);
    }

    private void showPromoPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Luôn tải danh sách mã để hiển thị ở bảng dưới
        List<PhieuGiamGia> promoList = phieuGiamGiaDAO.findAll();
        req.setAttribute("promoList", promoList);

        // Luôn tải danh sách sản phẩm và loại cho các <select> trong form
        List<SanPham> productList = sanPhamDAO.findAll();
        List<LoaiSanPham> categoryList = loaiSanPhamDAO.findAll();
        req.setAttribute("productList", productList);
        req.setAttribute("categoryList", categoryList);

        // **SỬA LỖI Ở ĐÂY:** Bắt đầu bằng một đối tượng promo mới, rỗng
        PhieuGiamGia promoToDisplay = new PhieuGiamGia();

        // Nếu là action "edit", tìm và thay thế đối tượng rỗng bằng đối tượng từ DB
        if ("edit".equals(req.getParameter("action"))) {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                // Giả sử GenericDAO của bạn có phương thức find() hoặc getById()
                promoToDisplay = phieuGiamGiaDAO.getById(Long.valueOf(idStr));
            }
        }

        // **SỬA LỖI Ở ĐÂY:** Luôn gửi một đối tượng 'promo' (dù mới hay cũ) sang JSP
        req.setAttribute("promo", promoToDisplay);

        req.getRequestDispatcher("/WEB-INF/views/admin/promos.jsp").forward(req, resp);
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idStr = req.getParameter("id");
            PhieuGiamGia phieu = (idStr != null && !idStr.isEmpty())
                    ? phieuGiamGiaDAO.getById(Long.valueOf(idStr))
                    : new PhieuGiamGia();

            phieu.setMa(req.getParameter("code"));
            phieu.setKieu(LoaiGiamGia.valueOf(req.getParameter("kieu")));
            phieu.setGiaTri(new BigDecimal(req.getParameter("value")));
            phieu.setMoTa(req.getParameter("desc"));

            String maxOff = req.getParameter("maxOff");
            phieu.setGiamToiDa(maxOff != null && !maxOff.isEmpty() ? new BigDecimal(maxOff) : null);
            String minOrder = req.getParameter("minOrder");
            phieu.setDonToiThieu(minOrder != null && !minOrder.isEmpty() ? new BigDecimal(minOrder) : null);

            // Sửa logic ngày tháng để khớp với LocalDateTime trong model
            String startDateStr = req.getParameter("start");
            phieu.setNgayBatDau(startDateStr != null && !startDateStr.isEmpty() ? LocalDate.parse(startDateStr).atStartOfDay() : null);
            String endDateStr = req.getParameter("end");
            phieu.setNgayKetThuc(endDateStr != null && !endDateStr.isEmpty() ? LocalDate.parse(endDateStr).atTime(23, 59, 59) : null);

            phieu.setActive(req.getParameter("active") != null);
            phieu.setApDungToanBo("ALL".equals(req.getParameter("applyScope")));

            phieu.getSanPhamApDung().clear();
            phieu.getLoaiApDung().clear();

            if (!phieu.isApDungToanBo()) {
                String scope = req.getParameter("applyScope");
                if ("PRODUCT".equals(scope)) {
                    String[] productIds = req.getParameterValues("productIds");
                    if (productIds != null) {
                        for (String id : productIds) {
                            phieu.getSanPhamApDung().add(sanPhamDAO.find(Long.valueOf(id)));
                        }
                    }
                } else if ("CATEGORY".equals(scope)) {
                    String[] categoryIds = req.getParameterValues("categoryIds");
                    if (categoryIds != null) {
                        for (String id : categoryIds) {
                            phieu.getLoaiApDung().add(loaiSanPhamDAO.find(Long.valueOf(id)));
                        }
                    }
                }
            }

            phieuGiamGiaDAO.update(phieu); // Dùng update (merge) cho cả tạo mới và sửa
            resp.sendRedirect(req.getContextPath() + "/admin/promos?ok=1");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/admin/promos?err=1");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        phieuGiamGiaDAO.delete(id);
        resp.sendRedirect(req.getContextPath() + "/admin/promos?ok=1");
    }

    private void handleToggle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        PhieuGiamGia phieu = phieuGiamGiaDAO.getById(id);
        if (phieu != null) {
            phieu.setActive(!phieu.isActive());
            phieuGiamGiaDAO.update(phieu);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/promos");
    }
}
