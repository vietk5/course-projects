package com.demo.controller;

import com.demo.model.SanPham;
import com.demo.model.cart.GioHangItem;
import com.demo.model.session.SessionUser;
import com.demo.model.KhachHang;
import com.demo.model.cart.GioHang;
import com.demo.persistence.GioHangDAO;
import com.demo.persistence.KhachHangDAO;
import com.demo.persistence.SanPhamDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        List<GioHangItem> cart = (List<GioHangItem>) session.getAttribute("cart");
        session.removeAttribute("buyNowCart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "view";
        }

        System.out.println("[DEBUG] Action GET nhận được: " + action);

        switch (action) {
            case "add":
            case "update": { // hợp nhất "add" & "update"
                handleAddOrUpdate(req, session, cart);
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }

            case "remove": {
                String sku = req.getParameter("sku");
                System.out.println("🗑️ [DEBUG] Xóa sản phẩm: " + sku);
                cart.removeIf(i -> i.getSku().equals(sku));
                session.setAttribute("cart", cart);
                deleteItemFromDatabaseIfLoggedIn(session, sku);
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }

            default:
                System.out.println("📦 [DEBUG] Hiển thị giỏ hàng – tổng sản phẩm: " + cart.size());
                req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        List<GioHangItem> cart = (List<GioHangItem>) session.getAttribute("cart");
        session.removeAttribute("buyNowCart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "view";
        }

        System.out.println("🛒 [DEBUG] Action POST nhận được: " + action);

        switch (action) {
            case "add":
            case "update": {
                handleAddOrUpdate(req, session, cart);
                break;
            }

            case "remove": {
                String sku = req.getParameter("sku");
                System.out.println("🗑️ [DEBUG] Xóa sản phẩm: " + sku);
                cart.removeIf(i -> i.getSku().equals(sku));
                session.setAttribute("cart", cart);
                deleteItemFromDatabaseIfLoggedIn(session, sku);
                break;
            }
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleAddOrUpdate(HttpServletRequest req, HttpSession session, List<GioHangItem> cart) {
        try {
            String productIdStr = req.getParameter("productId");
            int qtyChange = Integer.parseInt(req.getParameter("qty"));
            if (productIdStr == null || productIdStr.trim().isEmpty()) {
                return;
            }

            Long productId = Long.valueOf(productIdStr.trim());
            SanPham product = sanPhamDAO.find(productId);
            if (product == null) {
                return;
            }

            String sku = "SP-" + productId;
            Optional<GioHangItem> existing = cart.stream()
                    .filter(i -> i.getSku().equals(sku))
                    .findFirst();

            if (existing.isPresent()) {
                GioHangItem item = existing.get();
                int newQty = item.getSoLuong() + qtyChange;

                if (newQty <= 0) {
                    // 🗑️ Nếu giảm xuống 0 → xóa khỏi session + DB
                    cart.remove(item);
                    System.out.println("🗑️ [DEBUG] Xóa SP vì SL=0: " + sku);

                    SessionUser user = (SessionUser) session.getAttribute("user");
                    if (user != null && !user.isAdmin()) {
                        GioHangDAO gioHangDAO = new GioHangDAO();
                        KhachHangDAO khDAO = new KhachHangDAO();
                        KhachHang kh = khDAO.findById(user.getId());
                        if (kh != null) {
                            GioHang gioHang = gioHangDAO.findByKhachHang(kh);
                            if (gioHang != null) {
                                gioHangDAO.deleteItemBySku(gioHang, sku);
                            }
                        }
                    }
                } else {
                    if (newQty > product.getSoLuongTon()) {
                        newQty = product.getSoLuongTon();
                    }
                    item.setSoLuong(newQty);
                    System.out.println("✏️ [DEBUG] Cập nhật SL SP " + sku + " -> " + newQty);

                    // 🔹 Chỉ cập nhật sản phẩm hiện tại trong DB
                    SessionUser user = (SessionUser) session.getAttribute("user");
                    if (user != null && !user.isAdmin()) {
                        GioHangDAO gioHangDAO = new GioHangDAO();
                        KhachHangDAO khDAO = new KhachHangDAO();
                        KhachHang kh = khDAO.findById(user.getId());
                        if (kh != null) {
                            GioHang gioHang = gioHangDAO.findByKhachHang(kh);
                            if (gioHang == null) {
                                gioHang = gioHangDAO.createForUser(kh);
                            }
                            gioHangDAO.updateItemQuantity(gioHang.getId(), productId, newQty);
                        }
                    }
                }

            } else {
                // 🟢 Nếu sản phẩm chưa có trong giỏ và bấm "+"
                if (qtyChange > 0) {
                    GioHangItem newItem = new GioHangItem(
                            sku,
                            product.getTenSanPham(),
                            "assets/img/products/" + productId + ".jpg",
                            product.getGia().longValue(),
                            qtyChange
                    );
                    cart.add(newItem);
                    System.out.println("🟢 [DEBUG] Thêm SP mới: " + sku);

                    // 🔹 Ghi sản phẩm mới xuống DB
                    SessionUser user = (SessionUser) session.getAttribute("user");
                    if (user != null && !user.isAdmin()) {
                        GioHangDAO gioHangDAO = new GioHangDAO();
                        KhachHangDAO khDAO = new KhachHangDAO();
                        KhachHang kh = khDAO.findById(user.getId());
                        if (kh != null) {
                            GioHang gioHang = gioHangDAO.findByKhachHang(kh);
                            if (gioHang == null) {
                                gioHang = gioHangDAO.createForUser(kh);
                            }
                            gioHangDAO.saveItems(gioHang, Collections.singletonList(newItem));
                        }
                    }
                }
            }

            session.setAttribute("cart", cart);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ [ERROR] handleAddOrUpdate lỗi: " + e.getMessage());
        }
    }

    private int parseInt(String val, int def) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return def;
        }
    }

    // Đồng bộ giỏ hàng xuống DB
    private void saveCartToDatabaseIfLoggedIn(HttpSession session, List<GioHangItem> cart) {
        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null || user.isAdmin()) {
            return;
        }

        try {
            GioHangDAO gioHangDAO = new GioHangDAO();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhachHang kh = khDAO.findById(user.getId());
            if (kh == null) {
                return;
            }

            GioHang gioHang = gioHangDAO.findByKhachHang(kh);
            if (gioHang == null) {
                gioHang = gioHangDAO.createForUser(kh);
            }

            gioHangDAO.saveItems(gioHang, cart);
            System.out.println("💾 [DEBUG] Giỏ hàng đồng bộ DB cho KH=" + kh.getId());
        } catch (Exception e) {
            System.err.println("❌ [ERROR] Lưu giỏ hàng xuống DB lỗi: " + e.getMessage());
        }
    }

    // Xóa một sản phẩm trong DB
    private void deleteItemFromDatabaseIfLoggedIn(HttpSession session, String sku) {
        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null || user.isAdmin()) {
            return;
        }

        try {
            GioHangDAO gioHangDAO = new GioHangDAO();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhachHang kh = khDAO.findById(user.getId());
            if (kh != null) {
                GioHang gioHang = gioHangDAO.findByKhachHang(kh);
                if (gioHang != null) {
                    gioHangDAO.deleteItemBySku(gioHang, sku);
                }
            }
        } catch (Exception ex) {
            System.err.println("⚠️ [DEBUG] Lỗi khi xóa SP khỏi DB: " + ex.getMessage());
        }
    }
}
