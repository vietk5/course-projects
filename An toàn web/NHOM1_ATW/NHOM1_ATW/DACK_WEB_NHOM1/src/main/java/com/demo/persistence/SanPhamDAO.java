package com.demo.persistence;

import com.demo.model.SanPham;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SanPhamDAO extends GenericDAO<SanPham, Long> {
    public SanPhamDAO() { super(SanPham.class); }
    
    // Cache để tăng tốc độ tìm kiếm
    private static final Map<String, CachedResult> searchCache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL = 5 * 60 * 1000; // 5 phút

    // Tìm theo từ khóa trong tên, thương hiệu, hoặc loại sản phẩm
    public Page<SanPham> search(String keyword, int page, int size) {
        String where = "lower(e.tenSanPham) like :kw OR lower(e.thuongHieu.tenThuongHieu) like :kw OR lower(e.loai.tenLoai) like :kw";
        Map<String,Object> p = new HashMap<>();
        p.put("kw", "%" + (keyword == null ? "" : keyword.toLowerCase()) + "%");
        return findWhere(where, p, page, size, "id", false);
    }

    // Lấy sản phẩm liên quan theo loại
    public List<SanPham> relatedByLoai(long loaiId, long excludeId, int limit) {
        List<SanPham> list = findWhere("e.loai.id = :lid and e.id <> :ex",
                Map.of("lid", loaiId, "ex", excludeId));
        return list.size() > limit ? list.subList(0, limit) : list;
    }

    // Tìm kiếm nâng cao với nhiều điều kiện
    public Page<SanPham> searchAdvanced(String keyword, String brand, String category, 
                                        BigDecimal minPrice, BigDecimal maxPrice,
                                        int page, int size, String sortBy, boolean asc) {
        StringBuilder where = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();
        
        // Tìm theo keyword trong tên sản phẩm, thương hiệu, hoặc loại sản phẩm
        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND (lower(e.tenSanPham) like :kw OR lower(e.thuongHieu.tenThuongHieu) like :kw OR lower(e.loai.tenLoai) like :kw)");
            params.put("kw", "%" + keyword.toLowerCase().trim() + "%");
        }
        
        // Lọc theo thương hiệu
        if (brand != null && !brand.trim().isEmpty()) {
            where.append(" AND lower(e.thuongHieu.tenThuongHieu) = :brand");
            params.put("brand", brand.toLowerCase().trim());
        }
        
        // Lọc theo loại sản phẩm
        if (category != null && !category.trim().isEmpty()) {
            where.append(" AND lower(e.loai.tenLoai) = :cat");
            params.put("cat", category.toLowerCase().trim());
        }
        
        // Lọc theo khoảng giá
        if (minPrice != null) {
            where.append(" AND e.gia >= :minPrice");
            params.put("minPrice", minPrice);
        }
        if (maxPrice != null) {
            where.append(" AND e.gia <= :maxPrice");
            params.put("maxPrice", maxPrice);
        }
        
        // Chỉ hiển thị sản phẩm còn hàng
        where.append(" AND e.soLuongTon > 0");
        
        String orderBy = (sortBy == null || sortBy.isEmpty()) ? "id" : sortBy;
        return findWhere(where.toString(), params, page, size, orderBy, asc);
    }
    
    // Lấy danh sách tất cả thương hiệu
    public List<String> getAllBrands() {
        return inTransaction(em -> {
            return em.createQuery(
                "SELECT DISTINCT th.tenThuongHieu FROM ThuongHieu th ORDER BY th.tenThuongHieu",
                String.class
            ).getResultList();
        });
    }
    
    // Lấy danh sách tất cả loại sản phẩm
    public List<String> getAllCategories() {
        return inTransaction(em -> {
            return em.createQuery(
                "SELECT DISTINCT l.tenLoai FROM LoaiSanPham l ORDER BY l.tenLoai",
                String.class
            ).getResultList();
        });
    }
    
    /**
     * Lấy danh sách thương hiệu có sản phẩm trong một loại (category) cụ thể
     * Chỉ lấy thương hiệu có sản phẩm còn hàng
     */
    public List<String> getBrandsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return inTransaction(em -> {
            String jpql = "SELECT DISTINCT th.tenThuongHieu " +
                         "FROM SanPham s " +
                         "JOIN s.thuongHieu th " +
                         "JOIN s.loai l " +
                         "WHERE LOWER(l.tenLoai) = :cat " +
                         "AND s.soLuongTon > 0 " +
                         "ORDER BY th.tenThuongHieu";
            
            return em.createQuery(jpql, String.class)
                .setParameter("cat", category.toLowerCase().trim())
                .getResultList();
        });
    }
    
    /**
     * Lấy Map<Category, List<Brand>> cho menu dropdown
     * Map này chứa category → danh sách brands có sản phẩm trong category đó
     */
    public Map<String, List<String>> getCategoryBrandsMap() {
        Map<String, List<String>> result = new HashMap<>();
        List<String> categories = getAllCategories();
        
        for (String category : categories) {
            result.put(category, getBrandsByCategory(category));
        }
        
        return result;
    }
    
    // ========== TÍNH NĂNG NÂNG CAO (Giống Elasticsearch) ==========
    
    /**
     * Tìm kiếm với RANKING - Xếp hạng kết quả theo độ phù hợp
     * Tên khớp chính xác = 100 điểm, tên bắt đầu bằng keyword = 80 điểm, v.v.
     */
    @SuppressWarnings("unchecked")
    public List<SanPham> searchWithRanking(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(page, size, "id", false).getContent();
        }
        
        String kw = "%" + keyword.toLowerCase().trim() + "%";
        String exactKw = keyword.toLowerCase().trim();
        String startKw = keyword.toLowerCase().trim() + "%";
        
        return inTransaction(em -> {
            // Native query với CASE để tính điểm relevance
            // PostgreSQL sử dụng positional parameters (?1, ?2, ?3...)
            String sql = "SELECT s.*, " +
                        "CASE " +
                        "  WHEN LOWER(s.ten_san_pham) = ?1 THEN 100 " +
                        "  WHEN LOWER(s.ten_san_pham) LIKE ?2 THEN 80 " +
                        "  WHEN LOWER(s.ten_san_pham) LIKE ?3 THEN 60 " +
                        "  WHEN LOWER(th.ten_thuong_hieu) LIKE ?3 THEN 50 " +
                        "  WHEN LOWER(l.ten_loai) LIKE ?3 THEN 40 " +
                        "  WHEN LOWER(s.mo_ta_ngan) LIKE ?3 THEN 20 " +
                        "  ELSE 0 " +
                        "END as relevance_score " +
                        "FROM san_pham s " +
                        "LEFT JOIN thuong_hieu th ON s.thuong_hieu_id = th.id " +
                        "LEFT JOIN loai_san_pham l ON s.loai_id = l.id " +
                        "WHERE (LOWER(s.ten_san_pham) LIKE ?3 " +
                        "   OR LOWER(th.ten_thuong_hieu) LIKE ?3 " +
                        "   OR LOWER(l.ten_loai) LIKE ?3 " +
                        "   OR LOWER(s.mo_ta_ngan) LIKE ?3) " +
                        "AND s.so_luong_ton > 0 " +
                        "ORDER BY relevance_score DESC, s.id DESC " +
                        "LIMIT ?4 OFFSET ?5";
            
            Query q = em.createNativeQuery(sql, SanPham.class);
            q.setParameter(1, exactKw);    // ?1
            q.setParameter(2, startKw);    // ?2
            q.setParameter(3, kw);         // ?3 (dùng nhiều lần)
            q.setParameter(4, size);       // ?4
            q.setParameter(5, page * size); // ?5
            
            return q.getResultList();
        });
    }
    
    /**
     * AUTOCOMPLETE - Gợi ý tìm kiếm khi người dùng gõ
     * Trả về danh sách gợi ý từ tên sản phẩm và thương hiệu
     */
    public List<String> getSuggestions(String prefix, int limit) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String pattern = prefix.toLowerCase().trim() + "%";
        
        return inTransaction(em -> {
            List<String> suggestions = new ArrayList<>();
            
            // Gợi ý từ tên sản phẩm
            String jpql1 = "SELECT DISTINCT s.tenSanPham FROM SanPham s " +
                          "WHERE LOWER(s.tenSanPham) LIKE :pattern " +
                          "AND s.soLuongTon > 0 " +
                          "ORDER BY s.tenSanPham";
            
            List<String> tenSP = em.createQuery(jpql1, String.class)
                .setParameter("pattern", pattern)
                .setMaxResults(limit)
                .getResultList();
            suggestions.addAll(tenSP);
            
            // Gợi ý từ thương hiệu (nếu còn chỗ)
            if (suggestions.size() < limit) {
                String jpql2 = "SELECT DISTINCT th.tenThuongHieu FROM ThuongHieu th " +
                              "WHERE LOWER(th.tenThuongHieu) LIKE :pattern " +
                              "ORDER BY th.tenThuongHieu";
                
                List<String> thuongHieu = em.createQuery(jpql2, String.class)
                    .setParameter("pattern", pattern)
                    .setMaxResults(limit - suggestions.size())
                    .getResultList();
                suggestions.addAll(thuongHieu);
            }
            
            return suggestions.stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
        });
    }
    
    /**
     * Tìm kiếm với CACHE - Lưu kết quả trong bộ nhớ để tăng tốc
     * Cache tồn tại trong 5 phút
     */
    public List<SanPham> searchWithCache(String keyword, int page, int size) {
        String cacheKey = keyword + "_" + page + "_" + size;
        
        // Kiểm tra cache
        CachedResult cached = searchCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            System.out.println("✅ [CACHE HIT] Lấy kết quả từ cache: " + cacheKey);
            return cached.results;
        }
        
        // Nếu không có cache hoặc đã hết hạn, tìm kiếm mới
        System.out.println("🔍 [CACHE MISS] Tìm kiếm mới: " + cacheKey);
        List<SanPham> results = searchWithRanking(keyword, page, size);
        searchCache.put(cacheKey, new CachedResult(results));
        
        return results;
    }
    
    /**
     * Xóa cache (dùng khi cập nhật sản phẩm)
     */
    public static void clearCache() {
        searchCache.clear();
        System.out.println("🧹 Cache đã được xóa");
    }
    
    // ========== INNER CLASS ==========
    
    /**
     * Class lưu kết quả cache với timestamp
     */
    static class CachedResult {
        final List<SanPham> results;
        final long timestamp;
        
        CachedResult(List<SanPham> results) {
            this.results = results;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL;
        }
    }
}
