package com.demo.controller;

import com.demo.enums.TrangThaiDonHang;
import com.demo.model.order.DonHang;
import com.demo.model.order.ChiTietDonHang;
import com.demo.persistence.JPAUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet(name = "AdminOrdersServlet", urlPatterns = {"/admin/orders"})
public class AdminOrdersServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.setCharacterEncoding("UTF-8");
    resp.setCharacterEncoding("UTF-8");

    String st = req.getParameter("status");
    TrangThaiDonHang status = null;
    try { if (st != null && !st.isBlank()) status = TrangThaiDonHang.valueOf(st); } catch (Exception ignore) {}

    LocalDate toDate   = Optional.ofNullable(req.getParameter("to")).map(LocalDate::parse).orElse(LocalDate.now());
    LocalDate fromDate = Optional.ofNullable(req.getParameter("from")).map(LocalDate::parse).orElse(toDate.minusMonths(1));
    LocalDateTime f = fromDate.atStartOfDay();
    LocalDateTime t = toDate.plusDays(1).atStartOfDay();

    String qStr = Optional.ofNullable(req.getParameter("q")).orElse("").trim().toLowerCase();
    Long idFilter = null;
    if (qStr.matches("\\d+")) {
      try { idFilter = Long.valueOf(qStr); } catch (Exception ignore) {}
    }

    int page = parseInt(req.getParameter("page"), 1);
    int size = parseInt(req.getParameter("size"), 20);
    if (page < 1) page = 1; if (size < 1) size = 20;
    int offset = (page - 1) * size;

    EntityManager em = JPAUtil.getEmFactory().createEntityManager();
    try {
      String base = " from DonHang d " +
          "where d.ngayDatHang between :f and :t " +
          (status   != null ? "and d.trangThai = :st " : "") +
          (idFilter != null ? "and d.id = :idv " : "and (:kw='' or lower(d.khachHang.email) like concat('%',:kw,'%')) ");

      // count
      var qCount = em.createQuery("select count(d)" + base, Long.class)
          .setParameter("f", f)
          .setParameter("t", t);
      if (status != null)   qCount.setParameter("st", status);
      if (idFilter != null) qCount.setParameter("idv", idFilter); else qCount.setParameter("kw", qStr);
      long total = qCount.getSingleResult();

      // page ids
      var qIds = em.createQuery("select d.id" + base + " order by d.ngayDatHang desc", Long.class)
          .setParameter("f", f)
          .setParameter("t", t)
          .setFirstResult(offset)
          .setMaxResults(size);
      if (status != null)   qIds.setParameter("st", status);
      if (idFilter != null) qIds.setParameter("idv", idFilter); else qIds.setParameter("kw", qStr);
      List<Long> ids = qIds.getResultList();

      List<Map<String,Object>> orders = new ArrayList<>();
      if (!ids.isEmpty()) {
        // fetch join để dùng ở JSP không lỗi Lazy
        List<DonHang> list = em.createQuery(
            "select distinct d from DonHang d " +
            "left join fetch d.khachHang kh " +
            "left join fetch d.chiTiet ct " +
            "where d.id in :ids " +
            "order by d.ngayDatHang desc", DonHang.class)
            .setParameter("ids", ids)
            .getResultList();

        for (DonHang d : list) {
          double totalMoney = 0;
          if (d.getChiTiet() != null) {
            for (ChiTietDonHang ct : d.getChiTiet()) {
              Number sl = (Number) ct.getSoLuong();
              Number dg = (Number) ct.getDonGia();
              totalMoney += (sl == null ? 0 : sl.doubleValue()) * (dg == null ? 0 : dg.doubleValue());
            }
          }
          Map<String,Object> m = new LinkedHashMap<>();
          m.put("d", d);
          m.put("total", totalMoney);
          orders.add(m);
        }
      }

      req.setAttribute("orders", orders);
      req.setAttribute("allStatus", TrangThaiDonHang.values());
      req.setAttribute("status", status);
      req.setAttribute("from", fromDate);
      req.setAttribute("to", toDate);
      req.setAttribute("q", qStr);
      req.setAttribute("page", page);
      req.setAttribute("size", size);
      req.setAttribute("total", total);

      req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, resp);
    } finally {
      em.close();
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String idStr = req.getParameter("id");
    String st = req.getParameter("newStatus");
    if (idStr == null || st == null) { resp.sendRedirect(req.getContextPath()+"/admin/orders"); return; }

    EntityManager em = JPAUtil.getEmFactory().createEntityManager();
    try {
      em.getTransaction().begin();
      DonHang d = em.find(DonHang.class, Long.valueOf(idStr));
      if (d != null) d.setTrangThai(TrangThaiDonHang.valueOf(st));
      em.getTransaction().commit();
    } finally {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      em.close();
    }
    resp.sendRedirect(req.getContextPath()+"/admin/orders");
  }

  private int parseInt(String s, int def) {
    try { return Integer.parseInt(s); } catch (Exception e) { return def; }
  }
}
