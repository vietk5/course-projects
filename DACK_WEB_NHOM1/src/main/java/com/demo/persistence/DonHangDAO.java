package com.demo.persistence;

import com.demo.enums.TrangThaiDonHang;
import com.demo.model.order.DonHang;

import java.util.HashMap;
import java.util.Map;

public class DonHangDAO extends GenericDAO<DonHang, Long> {

    public DonHangDAO() {
        super(DonHang.class);
    }

    // User: xem đơn của mình + lọc trạng thái (tùy chọn)
    public Page<DonHang> byCustomer(Long khId, TrangThaiDonHang status, int page, int size) {
        String where = "e.khachHang.id = :khId";
        Map<String, Object> p = new HashMap<>();
        p.put("khId", khId);

        if (status != null) {
            where += " and e.trangThai = :st";
            p.put("st", status);
        }
        return findWhere(where, p, page, size, "ngayDatHang", false); // sort desc
    }

    // Admin: lọc theo trạng thái + keyword (email/sđt/id KH)
    public Page<DonHang> adminFilter(String keyword, TrangThaiDonHang status, int page, int size) {
        String where = "1=1";
        Map<String, Object> p = new HashMap<>();

        if (status != null) {
            where += " and e.trangThai = :st";
            p.put("st", status);
        }
        if (keyword != null && !keyword.isBlank()) {
            where += " and (lower(e.khachHang.email) like :kw or lower(e.khachHang.sdt) like :kw or cast(e.khachHang.id as string) like :kw)";
            p.put("kw", "%" + keyword.toLowerCase() + "%");
        }
        return findWhere(where, p, page, size, "ngayDatHang", false);
    }

    // Đổi trạng thái đơn
// Cập nhật trạng thái
    public void updateStatus(long orderId, TrangThaiDonHang newStatus) {
        inTransactionVoid(em -> {
            var d = em.find(DonHang.class, orderId);
            if (d != null) {
                d.setTrangThai(newStatus);
            }
        });
    }

}
