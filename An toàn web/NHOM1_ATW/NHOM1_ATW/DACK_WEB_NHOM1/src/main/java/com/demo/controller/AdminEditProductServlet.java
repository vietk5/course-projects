package com.demo.controller;

import com.demo.model.SanPham;
import com.demo.model.ThuongHieu;
import com.demo.model.LoaiSanPham;
import com.demo.persistence.JPAUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "AdminEditProductServlet", urlPatterns = {"/admin/products/edit"})
@MultipartConfig(maxFileSize = 10 * 1024 * 1024) // 10MB
public class AdminEditProductServlet extends HttpServlet {

  private static String safe(String s) { return s == null ? "" : s.trim(); }
  private static BigDecimal parseMoney(String raw) {
    if (raw == null) return BigDecimal.ZERO;
    String digits = raw.replaceAll("[^0-9]", "");
    if (digits.isEmpty()) return BigDecimal.ZERO;
    return new BigDecimal(digits);
  }
  private static int  parseInt (String s, int def){ try { return Integer.parseInt(s); } catch(Exception e){ return def; } }
  private static long parseLong(String s, long def){ try { return Long.parseLong(s); }  catch(Exception e){ return def; } }

  private List<ThuongHieu> allBrands(EntityManager em) {
    return em.createQuery("SELECT th FROM ThuongHieu th ORDER BY th.tenThuongHieu", ThuongHieu.class).getResultList();
  }
  private List<LoaiSanPham> allCategories(EntityManager em) {
    return em.createQuery("SELECT l FROM LoaiSanPham l ORDER BY l.tenLoai", LoaiSanPham.class).getResultList();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8"); resp.setCharacterEncoding("UTF-8");

    long id = parseLong(req.getParameter("id"), 0L);
    if (id <= 0) { resp.sendError(404); return; }

    EntityManager em = JPAUtil.getEmFactory().createEntityManager();
    try {
      SanPham item = em.find(SanPham.class, id);
      if (item == null) { resp.sendError(404); return; }

      // Đặt đúng tên attribute mà JSP đang dùng: brands, categories
      req.setAttribute("item", item);
      req.setAttribute("brands", allBrands(em));
      req.setAttribute("categories", allCategories(em));

      req.setAttribute("q",    safe(req.getParameter("q")));
      req.setAttribute("page", parseInt(req.getParameter("page"), 1));
      req.setAttribute("size", parseInt(req.getParameter("size"), 20));

      req.getRequestDispatcher("/WEB-INF/views/admin/product_edit.jsp").forward(req, resp);
    } finally { em.close(); }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8"); resp.setCharacterEncoding("UTF-8");

    long id = parseLong(req.getParameter("id"), 0L);
    if (id <= 0) { resp.sendError(400); return; }

    String q    = safe(req.getParameter("q"));
    int page    = parseInt(req.getParameter("page"), 1);
    int size    = parseInt(req.getParameter("size"), 20);

    String ten  = safe(req.getParameter("tenSanPham"));
    long brandId= parseLong(req.getParameter("thuongHieuId"), 0L);
    long loaiId = parseLong(req.getParameter("loaiId"), 0L);
    BigDecimal gia = parseMoney(req.getParameter("gia"));

    // CHUẨN HÓA THEO SanPham.java: soLuongTon
    int ton = parseInt(req.getParameter("soLuongTon"), 0);

    Part imagePart = null;
    try { imagePart = req.getPart("hinhAnh"); } catch (Exception ignored) {}

    EntityManager em = JPAUtil.getEmFactory().createEntityManager();
    EntityTransaction tx = em.getTransaction();

    try {
      tx.begin();

      SanPham sp = em.find(SanPham.class, id);
      if (sp == null) { tx.rollback(); resp.sendError(404); return; }

      // Tên setter khớp SanPham.java
      sp.setTenSanPham(ten);
      sp.setGia(gia);
      sp.setSoLuongTon(ton);

      if (brandId > 0) {
        ThuongHieu th = em.find(ThuongHieu.class, brandId);
        sp.setThuongHieu(th);
      }
      if (loaiId > 0) {
        LoaiSanPham loai = em.find(LoaiSanPham.class, loaiId);
        sp.setLoai(loai);
      }

      // === LƯU ẢNH: /assets/img/products/{id}.ext ===
      if (imagePart != null && imagePart.getSize() > 0) {
        String submitted = imagePart.getSubmittedFileName();
        String ext = "";
        int dot = submitted.lastIndexOf('.');
        if (dot >= 0) ext = submitted.substring(dot).toLowerCase();
        if (ext.isEmpty()) ext = ".jpg";

        String folder = getServletContext().getRealPath("/assets/img/products");
        File dir = new File(folder);
        if (!dir.exists()) dir.mkdirs();

        String fileName = id + ext; // ví dụ: 105.jpg
        File out = new File(dir, fileName);
        try (InputStream in = imagePart.getInputStream()) {
          Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // Xoá các biến thể cũ khác đuôi & dạng p{id}.ext
        String[] exts = {".jpg", ".jpeg", ".png", ".webp", ".gif", ".bmp"};
        for (String e : exts) {
          if (!e.equals(ext)) {
            try { Files.deleteIfExists(new File(dir, id + e).toPath()); } catch (Exception ignored) {}
          }
          try { Files.deleteIfExists(new File(dir, "p" + id + e).toPath()); } catch (Exception ignored) {}
        }

        // Nếu SanPham có field hinhAnh thì có thể dùng:
        // sp.setHinhAnh("assets/img/products/" + fileName);
      }

      // CHUẨN HÓA THEO SanPham.java: ngayCapPhat
      sp.setNgayCapPhat(LocalDate.now());

      em.merge(sp);
      tx.commit();

      String base = req.getContextPath() + "/admin/products";
      String redirect = String.format(
          "%s?q=%s&page=%d&size=%d&updated=1",
          base,
          java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8),
          page, size
      );
      resp.sendRedirect(redirect);

    } catch (Exception ex) {
      if (tx.isActive()) tx.rollback();
      throw new ServletException(ex);
    } finally { em.close(); }
  }
}
