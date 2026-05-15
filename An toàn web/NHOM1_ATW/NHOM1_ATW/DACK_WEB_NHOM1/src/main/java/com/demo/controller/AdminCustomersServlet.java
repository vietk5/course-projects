package com.demo.controller;

import com.demo.persistence.JPAUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "AdminCustomersServlet", urlPatterns = {"/admin/customers"})
public class AdminCustomersServlet extends HttpServlet {

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
      // Count (không đụng tới mat_khau_hash)
      Long total = em.createQuery(
          "select count(k) from KhachHang k " +
          "where :kw='' or lower(k.ten) like concat('%',:kw,'%') " +
          "   or lower(k.email) like concat('%',:kw,'%')", Long.class)
          .setParameter("kw", q)
          .getSingleResult();

      // Projection: chỉ lấy cột cần -> trả về Object[]
      // 0: id(Long), 1: ten(String), 2: email(String), 3: hangThanhVien(Enum), 4: ngayTao(LocalDateTime/Date)
      List<Object[]> items = em.createQuery(
          "select k.id, k.ten, k.email, k.hangThanhVien, k.ngayTao " +
          "from KhachHang k " +
          "where :kw='' or lower(k.ten) like concat('%',:kw,'%') " +
          "   or lower(k.email) like concat('%',:kw,'%') " +
          "order by k.id desc", Object[].class)
          .setParameter("kw", q)
          .setFirstResult(offset)
          .setMaxResults(size)
          .getResultList();

      req.setAttribute("items", items);
      req.setAttribute("q", q);
      req.setAttribute("page", page);
      req.setAttribute("size", size);
      req.setAttribute("total", total);

      req.getRequestDispatcher("/WEB-INF/views/admin/customers.jsp").forward(req, resp);
    } finally {
      em.close();
    }
  }

  private int parseInt(String s, int def) {
    try { return Integer.parseInt(s); } catch (Exception e) { return def; }
  }
}
