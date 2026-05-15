//package com.demo.controller;
//
//import com.demo.model.SanPham;
//import com.demo.persistence.SanPhamDAO;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.util.List;
//
//@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product"})
//public class ProductDetailServlet extends HttpServlet {
//    
//    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
//    
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//        throws ServletException, IOException {
//
//        req.setCharacterEncoding("UTF-8");
//        resp.setCharacterEncoding("UTF-8");
//        resp.setContentType("text/html; charset=UTF-8");
//
//        // Lấy tham số sản phẩm ID
//        String productIdStr = req.getParameter("id");
//        if (productIdStr == null || productIdStr.trim().isEmpty()) {
//            resp.sendRedirect(req.getContextPath() + "/home");
//            return;
//        }
//
//        try {
//            Long productId = Long.valueOf(productIdStr.trim());
//            SanPham product = sanPhamDAO.find(productId);
//            
//            if (product == null) {
//                resp.sendRedirect(req.getContextPath() + "/home");
//                return;
//            }
//            
//            // Lấy các sản phẩm liên quan (cùng loại sản phẩm, trừ sản phẩm hiện tại)
//            List<SanPham> relatedProducts = sanPhamDAO.relatedByLoai(
//                product.getLoai().getId(), 
//                productId, 
//                4
//            );
//
//            // Gửi dữ liệu đến JSP
//            req.setAttribute("product", product);
//            req.setAttribute("relatedProducts", relatedProducts);
//            req.setAttribute("brands", sanPhamDAO.getAllBrands());
//            req.setAttribute("categories", sanPhamDAO.getAllCategories());
//            req.setAttribute("categoryBrands", sanPhamDAO.getCategoryBrandsMap());
//
//            req.getRequestDispatcher("/WEB-INF/views/product_detail.jsp").forward(req, resp);
//            
//        } catch (NumberFormatException e) {
//            System.err.println("ID không hợp lệ: " + e.getMessage());
//            resp.sendRedirect(req.getContextPath() + "/home");
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//        throws ServletException, IOException {
//        doGet(req, resp);
//    }
//}
package com.demo.controller;

//import com.demo.model.SanPham;
//import com.demo.persistence.SanPhamDAO;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
import com.demo.model.SanPham;
import com.demo.model.PhieuGiamGia;
import com.demo.persistence.SanPhamDAO;
import com.demo.persistence.PhieuGiamGiaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import java.io.IOException;
//import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product"})
public class ProductDetailServlet extends HttpServlet {
    
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final PhieuGiamGiaDAO phieuGiamGiaDAO = new PhieuGiamGiaDAO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        // Lấy tham số sản phẩm ID
        String productIdStr = req.getParameter("id");
        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        try {
            Long productId = Long.valueOf(productIdStr.trim());
            SanPham product = sanPhamDAO.find(productId);
            
            if (product == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            
            // Lấy các sản phẩm liên quan (cùng loại sản phẩm, trừ sản phẩm hiện tại)
            List<SanPham> relatedProducts = sanPhamDAO.relatedByLoai(
                product.getLoai().getId(), 
                productId, 
                4
            );
            
            
            List<PhieuGiamGia> allPromos = phieuGiamGiaDAO.findAll();
            System.out.println("Tổng số mã giảm giá trong DB: " + (allPromos != null ? allPromos.size() : 0));
            if (allPromos != null) {
                allPromos.forEach(p -> System.out.println("Mã: " + p.getMa() + ", Active: " + p.isActive() + 
                    ", Thời gian: " + p.getNgayBatDau() + " - " + p.getNgayKetThuc() + 
                    ", apDungToanBo: " + p.isApDungToanBo() + 
                    ", Số loại áp dụng: " + (p.getLoaiApDung() != null ? p.getLoaiApDung().size() : 0) + 
                    ", Số sản phẩm áp dụng: " + (p.getSanPhamApDung() != null ? p.getSanPhamApDung().size() : 0)));
            }

            LocalDateTime now = LocalDateTime.now();
            List<PhieuGiamGia> promoList = allPromos.stream()
                .filter(p -> {
                    System.out.println("Kiểm tra mã " + p.getMa() + ": Active=" + p.isActive());
                    return p.isActive();
                })
                .filter(p -> {
                    // Tạm thời bỏ lọc thời gian để test
                    System.out.println("Kiểm tra thời gian " + p.getMa() + ": Bỏ qua lọc thời gian");
                    return true; // Chấp nhận tất cả, bất kể thời gian
                })
                .filter(p -> {
                    boolean isApplicable = p.isApDungToanBo() ||
                        (p.getLoaiApDung() != null && !p.getLoaiApDung().isEmpty() && 
                         p.getLoaiApDung().stream().anyMatch(c -> c.getId().equals(product.getLoai().getId()))) ||
                        (p.getSanPhamApDung() != null && !p.getSanPhamApDung().isEmpty() && 
                         p.getSanPhamApDung().stream().anyMatch(sp -> sp.getId().equals(productId)));
                    System.out.println("Kiểm tra áp dụng " + p.getMa() + ": " + isApplicable + 
                        ", Loai ID: " + (product.getLoai() != null ? product.getLoai().getId() : "null") + 
                        ", Product ID: " + productId);
                    return isApplicable;
                })
                .collect(Collectors.toList());

            System.out.println("Số mã giảm giá áp dụng: " + promoList.size());
            

            // Gửi dữ liệu đến JSP
            req.setAttribute("product", product);
            req.setAttribute("relatedProducts", relatedProducts);
            req.setAttribute("brands", sanPhamDAO.getAllBrands());
            req.setAttribute("categories", sanPhamDAO.getAllCategories());
            req.setAttribute("categoryBrands", sanPhamDAO.getCategoryBrandsMap());
            req.setAttribute("promoList", promoList);

            req.getRequestDispatcher("/WEB-INF/views/product_detail.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            System.err.println("ID không hợp lệ: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        doGet(req, resp);
    }
}
