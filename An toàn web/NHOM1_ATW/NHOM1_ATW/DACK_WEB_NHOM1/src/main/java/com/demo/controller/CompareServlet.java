package com.demo.controller;

import com.demo.model.SanPham;
import com.demo.persistence.SanPhamDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "CompareServlet", urlPatterns = {"/compare"})
public class CompareServlet extends HttpServlet {

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private static final int MAX_COMPARE_ITEMS = 2; // Chỉ so sánh 2 sản phẩm

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        List<Long> compareList = (List<Long>) session.getAttribute("compareList");
        if (compareList == null) {
            compareList = new ArrayList<>();
            session.setAttribute("compareList", compareList);
        }

        String action = req.getParameter("action");
        if (action == null) action = "view";

        System.out.println("🔍 [COMPARE] Action: " + action);

        switch (action) {
            case "add": {
                String productIdStr = req.getParameter("productId");
                if (productIdStr != null && !productIdStr.trim().isEmpty()) {
                    try {
                        Long productId = Long.valueOf(productIdStr.trim());
                        SanPham product = sanPhamDAO.find(productId);
                        
                        if (product == null) {
                            System.out.println("⚠️ [COMPARE] Sản phẩm không tồn tại: " + productId);
                            resp.sendRedirect(req.getContextPath() + "/compare");
                            return;
                        }

                        // Kiểm tra nếu danh sách đã đầy
                        if (compareList.size() >= MAX_COMPARE_ITEMS) {
                            req.setAttribute("errorMessage", "Bạn chỉ có thể so sánh tối đa " + MAX_COMPARE_ITEMS + " sản phẩm!");
                            loadCompareData(req, compareList);
                            req.getRequestDispatcher("/WEB-INF/views/compare.jsp").forward(req, resp);
                            return;
                        }

                        // Kiểm tra nếu sản phẩm đã có trong danh sách
                        if (compareList.contains(productId)) {
                            System.out.println("⚠️ [COMPARE] Sản phẩm đã có trong danh sách");
                            resp.sendRedirect(req.getContextPath() + "/compare");
                            return;
                        }

                        // Kiểm tra loại sản phẩm - chỉ cho phép so sánh sản phẩm cùng loại
                        if (!compareList.isEmpty()) {
                            Long firstProductId = compareList.get(0);
                            SanPham firstProduct = sanPhamDAO.find(firstProductId);
                            
                            System.out.println("🔍 [COMPARE DEBUG] First product category ID: " + firstProduct.getLoai().getId());
                            System.out.println("🔍 [COMPARE DEBUG] New product category ID: " + product.getLoai().getId());
                            
                            if (firstProduct != null && firstProduct.getLoai() != null && product.getLoai() != null) {
                                Long firstCatId = firstProduct.getLoai().getId();
                                Long newCatId = product.getLoai().getId();
                                
                                if (!firstCatId.equals(newCatId)) {
                                    req.setAttribute("errorMessage", 
                                        "Chỉ có thể so sánh các sản phẩm cùng loại! " +
                                        "Danh sách hiện tại chứa: " + firstProduct.getLoai().getTenLoai());
                                    loadCompareData(req, compareList);
                                    req.getRequestDispatcher("/WEB-INF/views/compare.jsp").forward(req, resp);
                                    return;
                                }
                            }
                        }

                        compareList.add(productId);
                        session.setAttribute("compareList", compareList);
                        session.setAttribute("compareCount", compareList.size());
                        System.out.println("✅ [COMPARE] Đã thêm sản phẩm " + productId + " vào danh sách so sánh");
                        
                    } catch (NumberFormatException e) {
                        System.err.println("ID sản phẩm không hợp lệ: " + e.getMessage());
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/compare");
                return;
            }

            case "remove": {
                String productIdStr = req.getParameter("productId");
                if (productIdStr != null && !productIdStr.trim().isEmpty()) {
                    try {
                        Long productId = Long.valueOf(productIdStr.trim());
                        compareList.remove(productId);
                        session.setAttribute("compareList", compareList);
                        session.setAttribute("compareCount", compareList.size());
                        System.out.println("🗑️ [COMPARE] Đã xóa sản phẩm " + productId);
                    } catch (NumberFormatException e) {
                        System.err.println("ID sản phẩm không hợp lệ: " + e.getMessage());
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/compare");
                return;
            }

            case "clear": {
                compareList.clear();
                session.setAttribute("compareList", compareList);
                session.setAttribute("compareCount", 0);
                System.out.println("🧹 [COMPARE] Đã xóa toàn bộ danh sách so sánh");
                resp.sendRedirect(req.getContextPath() + "/compare");
                return;
            }

            default:
                // Hiển thị trang so sánh
                loadCompareData(req, compareList);
                req.getRequestDispatcher("/WEB-INF/views/compare.jsp").forward(req, resp);
        }
    }

    /**
     * Load dữ liệu sản phẩm để so sánh
     */
    private void loadCompareData(HttpServletRequest req, List<Long> compareList) {
        List<SanPham> compareProducts = new ArrayList<>();
        
        for (Long productId : compareList) {
            SanPham product = sanPhamDAO.find(productId);
            if (product != null) {
                compareProducts.add(product);
            }
        }
        
        req.setAttribute("compareProducts", compareProducts);
        req.setAttribute("compareCount", compareProducts.size());
        req.setAttribute("maxCompare", MAX_COMPARE_ITEMS);
        
        // Nếu đã có sản phẩm đầu tiên, chỉ load sản phẩm cùng loại để chọn
        if (!compareProducts.isEmpty()) {
            SanPham firstProduct = compareProducts.get(0);
            Long categoryId = firstProduct.getLoai().getId();
            
            // Lấy tất cả sản phẩm cùng loại (trừ những sản phẩm đã chọn)
            List<SanPham> availableProducts = sanPhamDAO.findWhere(
                "e.loai.id = :categoryId AND e.id NOT IN :excludeIds AND e.soLuongTon > 0",
                Map.of("categoryId", categoryId, "excludeIds", compareList.isEmpty() ? List.of(-1L) : compareList)
            );
            req.setAttribute("availableProducts", availableProducts);
            req.setAttribute("selectedCategory", firstProduct.getLoai().getTenLoai());
        } else {
            // Nếu chưa có sản phẩm nào, load tất cả sản phẩm còn hàng
            List<SanPham> availableProducts = sanPhamDAO.findWhere(
                "e.soLuongTon > 0",
                Map.of()
            );
            req.setAttribute("availableProducts", availableProducts);
        }
        
        // Set categories và brands cho navbar
        req.setAttribute("categories", sanPhamDAO.getAllCategories());
        req.setAttribute("brands", sanPhamDAO.getAllBrands());
        req.setAttribute("categoryBrands", sanPhamDAO.getCategoryBrandsMap());
        
        // Set page title
        req.setAttribute("pageTitle", "So sánh sản phẩm - ElectroMart");
        
        System.out.println("📊 [COMPARE] Đang so sánh " + compareProducts.size() + " sản phẩm");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}

