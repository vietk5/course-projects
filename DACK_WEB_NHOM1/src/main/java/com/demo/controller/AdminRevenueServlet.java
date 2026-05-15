package com.demo.controller;

import com.demo.enums.TrangThaiDonHang;
import com.demo.persistence.JPAUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;

@WebServlet(name = "AdminRevenueServlet", urlPatterns = {"/admin/revenue"})
public class AdminRevenueServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.setCharacterEncoding("UTF-8");
    resp.setCharacterEncoding("UTF-8");

    // period: day|week|month|quarter|year
    String period  = Optional.ofNullable(req.getParameter("period")).orElse("month");
    LocalDate to   = Optional.ofNullable(req.getParameter("to")).map(LocalDate::parse).orElse(LocalDate.now());
    LocalDate from = Optional.ofNullable(req.getParameter("from")).map(LocalDate::parse).orElse(to.minusMonths(3));
    boolean csv    = "csv".equalsIgnoreCase(req.getParameter("export"));

    String bucket;
    if      ("day".equalsIgnoreCase(period))     bucket = "day";
    else if ("week".equalsIgnoreCase(period))    bucket = "week";
    else if ("quarter".equalsIgnoreCase(period)) bucket = "quarter";
    else if ("year".equalsIgnoreCase(period))    bucket = "year";
    else                                         bucket = "month";

    LocalDateTime fromTs = from.atStartOfDay();
    LocalDateTime toTs   = to.plusDays(1).atStartOfDay(); // upper-exclusive

    EntityManager em = JPAUtil.getEmFactory().createEntityManager();
    try {
      // ---- Chuẩn bị tham số dùng lại
      String done = TrangThaiDonHang.HOAN_TAT.name();
      Timestamp f = Timestamp.valueOf(fromTs);
      Timestamp t = Timestamp.valueOf(toTs);

      // ---- Tổng doanh thu (native để đồng bộ)
      Number totalRevenueN = (Number) em.createNativeQuery(
              "select coalesce(sum(ct.so_luong * ct.don_gia),0) " +
              "from don_hang d join chi_tiet_don_hang ct on ct.don_hang_id = d.id " +
              "where d.trang_thai = ? and d.ngay_dat_hang between ? and ?")
          .setParameter(1, done).setParameter(2, f).setParameter(3, t)
          .getSingleResult();
      double totalRevenue = totalRevenueN == null ? 0.0 : totalRevenueN.doubleValue();

      // ---- Số đơn hoàn tất
      Number orderCountN = (Number) em.createNativeQuery(
              "select count(distinct d.id) " +
              "from don_hang d " +
              "where d.trang_thai = ? and d.ngay_dat_hang between ? and ?")
          .setParameter(1, done).setParameter(2, f).setParameter(3, t)
          .getSingleResult();
      long orderCount = orderCountN == null ? 0L : orderCountN.longValue();
      double avgOrder = orderCount > 0 ? totalRevenue / orderCount : 0.0;

      // ---- Doanh thu theo kỳ (native + literal cho date_trunc('<bucket>', ...))
      String sql =
          "select date_trunc('" + bucket + "', d.ngay_dat_hang) as bucket, " +
          "       sum(ct.so_luong * ct.don_gia) as total " +
          "from don_hang d join chi_tiet_don_hang ct on ct.don_hang_id = d.id " +
          "where d.trang_thai = ? and d.ngay_dat_hang between ? and ? " +
          "group by bucket " +
          "order by bucket";

      @SuppressWarnings("unchecked")
      List<Object[]> rows = em.createNativeQuery(sql)
          .setParameter(1, done).setParameter(2, f).setParameter(3, t)
          .getResultList();

      List<String> labels = new ArrayList<>();
      List<Double> values = new ArrayList<>();
      WeekFields wf = WeekFields.ISO;

      for (Object[] r : rows) {
        Timestamp ts = (Timestamp) r[0];
        LocalDateTime ldt = ts.toLocalDateTime();

        String label;
        switch (bucket) {
          case "day":
            label = ldt.toLocalDate().toString();
            break;
          case "week":
            label = String.format("W%02d %d",
                    ldt.get(wf.weekOfWeekBasedYear()),
                    ldt.get(wf.weekBasedYear()));
            break;
          case "quarter":
            label = "Q" + ((ldt.getMonthValue()-1)/3 + 1) + " " + ldt.getYear();
            break;
          case "year":
            label = Integer.toString(ldt.getYear());
            break;
          default: // month
            label = String.format("%02d/%d", ldt.getMonthValue(), ldt.getYear());
        }
        labels.add(label);

        Number val = (Number) r[1];
        values.add(val == null ? 0.0 : val.doubleValue());
      }

      // ---- CSV export
      if (csv) {
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=revenue.csv");
        try (PrintWriter out = resp.getWriter()) {
          out.println("label,value");
          for (int i = 0; i < labels.size(); i++) {
            out.printf("%s,%.2f%n", labels.get(i).replace(",", " "), values.get(i));
          }
        }
        return;
      }

      // ---- Gửi về JSP
      req.setAttribute("period", period);
      req.setAttribute("from", from);
      req.setAttribute("to", to);
      req.setAttribute("labels", labels);
      req.setAttribute("values", values);
      req.setAttribute("totalRevenue", totalRevenue);
      req.setAttribute("orderCount", orderCount);
      req.setAttribute("avgOrder", avgOrder);

      req.getRequestDispatcher("/WEB-INF/views/admin/revenue.jsp").forward(req, resp);

    } finally {
      if (em.isOpen()) em.close();
    }
  }
}
