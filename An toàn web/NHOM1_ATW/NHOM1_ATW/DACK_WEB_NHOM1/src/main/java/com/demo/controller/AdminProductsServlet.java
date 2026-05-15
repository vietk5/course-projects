package com.demo.controller;

import com.demo.model.SanPham;
import com.demo.persistence.JPAUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet(name = "AdminProductsServlet", urlPatterns = {"/admin/products"})
public class AdminProductsServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.setCharacterEncoding("UTF-8");
    resp.setCharacterEncoding("UTF-8");

    String q = Optional.ofNullable(req.getParameter("q")).orElse("").trim().toLowerCase();
    int page = parseInt(req.getParameter("page"), 1);
    int size = parseInt(req.getParameter("size"), 20);
    if (page < 1) page = 1;
    if (size < 1) size = 20;
    int offset = (page - 1) * size;

    EntityManager em = JPAUtil.getEmFactory().createEntityManager();
    try {
      long total = em.createQuery(
          "select count(p) from SanPham p " +
          "where :kw='' " +
          "   or lower(p.tenSanPham) like concat('%',:kw,'%') " +
          "   or lower(p.thuongHieu.tenThuongHieu) like concat('%',:kw,'%') " +
          "   or lower(p.loai.tenLoai) like concat('%',:kw,'%')",
          Long.class)
          .setParameter("kw", q)
          .getSingleResult();

      // SẮP XẾP MỚI NHẤT TRƯỚC (ID GIẢM DẦN)
      List<SanPham> items = em.createQuery(
          "select distinct p from SanPham p " +
          "left join fetch p.thuongHieu th " +
          "left join fetch p.loai lo " +
          "where :kw='' " +
          "   or lower(p.tenSanPham) like concat('%',:kw,'%') " +
          "   or lower(th.tenThuongHieu) like concat('%',:kw,'%') " +
          "   or lower(lo.tenLoai) like concat('%',:kw,'%') " +
          "order by p.id desc", SanPham.class)
          .setParameter("kw", q)
          .setFirstResult(offset)
          .setMaxResults(size)
          .getResultList();

      req.setAttribute("items", items);
      req.setAttribute("q", q);
      req.setAttribute("page", page);
      req.setAttribute("size", size);
      req.setAttribute("total", total);
      req.setAttribute("offset", offset); // JSP dùng offset + index + 1 để render STT

      req.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(req, resp);
    } finally {
      em.close();
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.setCharacterEncoding("UTF-8");
    resp.setCharacterEncoding("UTF-8");

    String action = Optional.ofNullable(req.getParameter("action")).orElse("");
    if (!"delete".equals(action)) {
      doGet(req, resp);
      return;
    }

    long id = parseLong(req.getParameter("id"), -1L);

    // Giữ lại tham số tìm kiếm/phân trang để redirect
    String qRaw = Optional.ofNullable(req.getParameter("q")).orElse("").trim().toLowerCase();
    int page = parseInt(req.getParameter("page"), 1);
    int size = parseInt(req.getParameter("size"), 20);

    boolean ok = false;
    EntityManager em = JPAUtil.getEmFactory().createEntityManager();
    try {
      em.getTransaction().begin();
      if (id > 0) {
        SanPham sp = em.find(SanPham.class, id);
        if (sp != null) {
          em.remove(sp);
          ok = true;
        }
      }
      em.getTransaction().commit();
    } catch (Exception ex) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      ok = false;
    } finally {
      em.close();
    }

    // Redirect để tránh submit lại form và làm mới danh sách
    String base = req.getContextPath() + "/admin/products";
    String q = URLEncoder.encode(qRaw, StandardCharsets.UTF_8);
    String redirectUrl = String.format("%s?q=%s&page=%d&size=%d&deleted=%s",
        base, q, page, size, ok ? "1" : "0");
    resp.sendRedirect(redirectUrl);
  }

  private int parseInt(String s, int def) {
    try { return Integer.parseInt(s); } catch (Exception e) { return def; }
  }

  private long parseLong(String s, long def) {
    try { return Long.parseLong(s); } catch (Exception e) { return def; }
  }
}
