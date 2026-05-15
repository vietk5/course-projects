package com.demo.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import com.demo.persistence.SanPhamDAO;
import com.demo.persistence.GenericDAO.Page;
import com.demo.model.SanPham;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        // Lấy tham số tìm kiếm
        String keyword = req.getParameter("q");
        if (keyword == null) keyword = "";
        keyword = keyword.trim();
        
        String brand = req.getParameter("brand");
        if (brand != null) brand = brand.trim();
        
        String category = req.getParameter("category");
        if (category != null) category = category.trim();

        // Khoảng giá
        String minStr = req.getParameter("min");
        String maxStr = req.getParameter("max");
        BigDecimal minPrice = null, maxPrice = null;
        try { 
            if (minStr != null && !minStr.isBlank()) 
                minPrice = new BigDecimal(minStr.trim()); 
        } catch (Exception ignored) {}
        try { 
            if (maxStr != null && !maxStr.isBlank()) 
                maxPrice = new BigDecimal(maxStr.trim()); 
        } catch (Exception ignored) {}
        
        // Phân trang
        int page = 0;
        int size = 20; // 20 sản phẩm mỗi trang
        try {
            String pageParam = req.getParameter("page");
            if (pageParam != null) page = Math.max(0, Integer.parseInt(pageParam));
        } catch (Exception ignored) {}
        
        // Sắp xếp
        String sort = req.getParameter("sort");
        String sortBy = "id";
        boolean asc = true;
        
        if ("price_asc".equalsIgnoreCase(sort)) {
            sortBy = "gia";
            asc = true;
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            sortBy = "gia";
            asc = false;
        } else if ("name_asc".equalsIgnoreCase(sort)) {
            sortBy = "tenSanPham";
            asc = true;
        }

        // ========== TÍNH NĂNG MỚI: SEARCH THÔNG MINH ==========
        
        List<SanPham> searchResults;
        long totalResults;
        
        // Nếu chỉ có keyword (không có filter) → dùng searchWithCache + Ranking
        boolean hasFilters = (brand != null && !brand.isEmpty()) || 
                            (category != null && !category.isEmpty()) || 
                            minPrice != null || maxPrice != null || 
                            sort != null;
        
        if (!hasFilters && !keyword.isEmpty()) {
            // 🚀 Sử dụng CACHE + RANKING cho tìm kiếm thuần
            System.out.println("🎯 [SEARCH] Sử dụng searchWithCache + Ranking");
            searchResults = sanPhamDAO.searchWithCache(keyword, page, size);
            totalResults = searchResults.size();
        } else {
            // Dùng searchAdvanced với filters
            System.out.println("🔍 [SEARCH] Sử dụng searchAdvanced với filters");
            Page<SanPham> searchPage = sanPhamDAO.searchAdvanced(
                keyword, brand, category, minPrice, maxPrice, 
                page, size, sortBy, asc
            );
            searchResults = searchPage.getContent();
            totalResults = searchPage.getTotalElements();
        }
        
        // Lấy autocomplete suggestions
        List<String> suggestions = new ArrayList<>();
        if (!keyword.isEmpty() && keyword.length() >= 2) {
            suggestions = sanPhamDAO.getSuggestions(keyword, 5);
        }
        
        // Lấy danh sách thương hiệu và loại sản phẩm
        List<String> brands = sanPhamDAO.getAllBrands();
        List<String> categories = sanPhamDAO.getAllCategories();

        // Gửi dữ liệu đến JSP
        req.setAttribute("searchResults", searchResults);
        req.setAttribute("keyword", keyword);
        req.setAttribute("suggestions", suggestions); // 💡 Autocomplete
        req.setAttribute("brands", brands);
        req.setAttribute("categories", categories);
        req.setAttribute("categoryBrands", sanPhamDAO.getCategoryBrandsMap()); // Map category -> brands
        req.setAttribute("activeBrand", brand);
        req.setAttribute("activeCategory", category);
        req.setAttribute("resultCount", totalResults);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", (int) Math.ceil(totalResults / (double) size));
        req.setAttribute("minPrice", minPrice);
        req.setAttribute("maxPrice", maxPrice);
        req.setAttribute("sort", sort);

        req.getRequestDispatcher("/WEB-INF/views/search.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        doGet(req, resp);
    }
}
