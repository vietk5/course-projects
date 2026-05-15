package com.demo.controller;

import com.demo.enums.TrangThaiDonHang;
import com.demo.model.order.DonHang;
import com.demo.persistence.JPAUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin"})
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        try {
            // ====== COUNTERS ======
            long totalCustomers = em.createQuery("select count(k) from KhachHang k", Long.class).getSingleResult();
            long totalProducts  = em.createQuery("select count(p) from SanPham p", Long.class).getSingleResult();
            Long totalStockObj  = em.createQuery("select sum(coalesce(p.soLuongTon,0)) from SanPham p", Long.class).getSingleResult();
            long totalStock     = totalStockObj == null ? 0 : totalStockObj;

            // ====== ORDERS BY STATUS ======
            List<Object[]> byStatus = em.createQuery(
                    "select d.trangThai, count(d) from DonHang d group by d.trangThai",
                    Object[].class).getResultList();
            Map<String, Long> statusCounts = new LinkedHashMap<>();
            for (Object[] row : byStatus) {
                statusCounts.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
            }

            // ====== LOW STOCK ======
            List<?> lowStocks = em.createQuery(
                            "select p from SanPham p where coalesce(p.soLuongTon,0) < :t order by p.soLuongTon asc")
                    .setParameter("t", 10)
                    .setMaxResults(10)
                    .getResultList();

            // ====== FILTER ORDERS (status) ======
            String st = req.getParameter("status");
            TrangThaiDonHang status = null;
            try {
                if (st != null && !st.isBlank()) status = TrangThaiDonHang.valueOf(st);
            } catch (Exception ignore) { /* giữ null nếu parse lỗi */ }

            List<DonHang> orders = (status == null)
                    ? em.createQuery("select d from DonHang d order by d.ngayDatHang desc", DonHang.class)
                        .setMaxResults(20).getResultList()
                    : em.createQuery("select d from DonHang d where d.trangThai = :st order by d.ngayDatHang desc", DonHang.class)
                        .setParameter("st", status).setMaxResults(20).getResultList();

            // ====== REVENUE (date range + bucket) ======
            String period = Optional.ofNullable(req.getParameter("period")).orElse("month"); // day|week|month|quarter|year
            LocalDate toDate   = Optional.ofNullable(req.getParameter("to")).map(LocalDate::parse).orElse(LocalDate.now());
            LocalDate fromDate = Optional.ofNullable(req.getParameter("from")).map(LocalDate::parse).orElse(toDate.minusMonths(3));

            // bucket cho date_trunc
            String bucket;
            if ("day".equalsIgnoreCase(period))       bucket = "day";
            else if ("week".equalsIgnoreCase(period)) bucket = "week";
            else if ("quarter".equalsIgnoreCase(period)) bucket = "quarter";
            else if ("year".equalsIgnoreCase(period)) bucket = "year";
            else bucket = "month";

            // DÙNG LocalDateTime vì field DonHang.ngayDatHang là LocalDateTime
            LocalDateTime fromTs = fromDate.atStartOfDay();
            // upper-bound exclusive: đầu ngày hôm sau
            LocalDateTime toTs   = toDate.plusDays(1).atStartOfDay();

            // Dùng Native Query với bucket hardcoded (PostgreSQL yêu cầu date_trunc string literal trong GROUP BY)
            // Bucket đã được validate ở trên nên an toàn với String.format
            String sql = String.format(
                    "SELECT date_trunc('%s', t0.ngay_dat_hang) as period, " +
                    "       SUM(t1.so_luong * t1.don_gia) as revenue " +
                    "FROM don_hang t0 " +
                    "JOIN chi_tiet_don_hang t1 ON t1.don_hang_id = t0.id " +
                    "WHERE t0.trang_thai = ? AND t0.ngay_dat_hang BETWEEN ? AND ? " +
                    "GROUP BY date_trunc('%s', t0.ngay_dat_hang) " +
                    "ORDER BY period",
                    bucket, bucket);

            Query q = em.createNativeQuery(sql)
                    .setParameter(1, TrangThaiDonHang.HOAN_TAT.toString())
                    .setParameter(2, fromTs)
                    .setParameter(3, toTs);

            // Chuyển kết quả thành {label, value} cho chart
            List<Map<String, Object>> revenue = new ArrayList<>();
            var weekFields = WeekFields.ISO;

            @SuppressWarnings("unchecked")
            List<Object[]> resultList = q.getResultList();
            for (Object[] r : resultList) {
                Object tsObj = r[0];
                LocalDateTime ldt;
                if (tsObj instanceof LocalDateTime) {
                    ldt = (LocalDateTime) tsObj;
                } else if (tsObj instanceof java.sql.Timestamp) {
                    ldt = ((java.sql.Timestamp) tsObj).toLocalDateTime();
                } else {
                    // fallback
                    ldt = fromTs;
                }

                String label;
                switch (bucket) {
                    case "day":
                        label = ldt.toLocalDate().toString(); // yyyy-MM-dd
                        break;
                    case "week":
                        int w = ldt.get(weekFields.weekOfWeekBasedYear());
                        int y = ldt.get(weekFields.weekBasedYear());
                        label = String.format("W%02d %d", w, y);
                        break;
                    case "quarter":
                        int qn = (ldt.getMonthValue() - 1) / 3 + 1;
                        label = "Q" + qn + " " + ldt.getYear();
                        break;
                    case "year":
                        label = Integer.toString(ldt.getYear());
                        break;
                    default: // month
                        label = String.format("%02d/%d", ldt.getMonthValue(), ldt.getYear());
                }

                Map<String, Object> m = new LinkedHashMap<>();
                m.put("label", label);
                m.put("value", ((Number) r[1]).doubleValue());
                revenue.add(m);
            }

            // ====== set attributes & forward ======
            req.setAttribute("totalCustomers", totalCustomers);
            req.setAttribute("totalProducts", totalProducts);
            req.setAttribute("totalStock", totalStock);
            req.setAttribute("statusCounts", statusCounts);
            req.setAttribute("lowStocks", lowStocks);
            req.setAttribute("orders", orders);
            req.setAttribute("allStatus", TrangThaiDonHang.values());

            req.setAttribute("period", period);
            req.setAttribute("from", fromDate);
            req.setAttribute("to", toDate);
            req.setAttribute("revenue", revenue);

            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);

        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
