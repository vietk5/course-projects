package com.demo.persistence;

import com.demo.model.KhachHang;
import com.demo.model.cart.GioHang;
import com.demo.model.cart.GioHangItem;
import com.demo.model.GioHangItemEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho việc lưu và nạp giỏ hàng từ cơ sở dữ liệu. KHÔNG ảnh hưởng đến hệ
 * thống session hiện tại.
 */
public class GioHangDAO {

    /**
     * 🔹 Lấy giỏ hàng của khách hàng, tạo mới nếu chưa có
     */
    public GioHang findByKhachHang(KhachHang kh) {
        EntityManager em = JPAUtil.em();
        try {
            TypedQuery<GioHang> q = em.createQuery(
                    "SELECT g FROM GioHang g WHERE g.chuSoHuu = :kh", GioHang.class);
            q.setParameter("kh", kh);
            List<GioHang> list = q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * 🔹 Tạo giỏ hàng mới cho người dùng — có flush để đảm bảo ID được sinh ra
     */
    public GioHang createForUser(KhachHang kh) {
        EntityManager em = JPAUtil.em();
        GioHang g = new GioHang();
        g.setChuSoHuu(kh);

        try {
            em.getTransaction().begin();
            em.persist(g);
            em.flush(); // ⚡ Đảm bảo g.getId() có giá trị ngay
            em.getTransaction().commit();
            System.out.println("✅ [DEBUG] Tạo giỏ hàng mới ID=" + g.getId() + " cho KH=" + kh.getId());
            return g;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * 🔹 Lưu danh sách item xuống bảng gio_hang_item (giữ lại các sản phẩm cũ,
     * không xóa)
     */
    public void saveItems(GioHang gioHang, List<GioHangItem> items) {
        if (gioHang == null || gioHang.getId() == null) {
            System.err.println("❌ [ERROR] Giỏ hàng null hoặc chưa có ID — không thể lưu.");
            return;
        }

        EntityManager em = JPAUtil.em();
        try {
            em.getTransaction().begin();

            for (GioHangItem item : items) {
                Long sanPhamId;
                try {
                    sanPhamId = Long.valueOf(item.getSku().replace("SP-", "").trim());
                } catch (Exception ex) {
                    System.err.println("⚠️ [DEBUG] SKU không hợp lệ: " + item.getSku());
                    continue;
                }

                // ✅ Kiểm tra xem sản phẩm đã tồn tại trong giỏ này chưa
                List<GioHangItemEntity> existingList = em.createQuery(
                        "SELECT i FROM GioHangItemEntity i WHERE i.gioHangId = :gid AND i.sanPhamId = :sid",
                        GioHangItemEntity.class)
                        .setParameter("gid", gioHang.getId())
                        .setParameter("sid", sanPhamId)
                        .getResultList();

                if (existingList.isEmpty()) {
                    // ➕ Thêm mới sản phẩm
                    GioHangItemEntity entity = new GioHangItemEntity();
                    entity.setGioHangId(gioHang.getId());
                    entity.setSanPhamId(sanPhamId);
                    entity.setSoLuong(item.getSoLuong());
                    em.persist(entity);
                    System.out.println("🟢 [DEBUG] Đã thêm SP " + sanPhamId + " (SL=" + item.getSoLuong() + ") vào giỏ ID=" + gioHang.getId());
                } else {
                    // 🔁 Cập nhật số lượng (cộng dồn)
                    GioHangItemEntity existing = existingList.get(0);
                    int newQty = existing.getSoLuong() + item.getSoLuong();
                    existing.setSoLuong(newQty);
                    em.merge(existing);
                    System.out.println("🟡 [DEBUG] Cập nhật SP " + sanPhamId + " → SL=" + newQty);
                }
            }

            em.getTransaction().commit();
            em.clear(); // đảm bảo flush toàn bộ xuống DB
            System.out.println("✅ [DEBUG] Đã lưu " + items.size() + " sản phẩm vào DB cho giỏ ID " + gioHang.getId());

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            System.err.println("❌ [ERROR] Lỗi khi lưu item giỏ hàng: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public void deleteItemBySku(GioHang gioHang, String sku) {
        EntityManager em = JPAUtil.em();
        try {
            em.getTransaction().begin();

            Long sanPhamId = null;
            try {
                sanPhamId = Long.valueOf(sku.replace("SP-", "").trim());
            } catch (Exception ex) {
                System.err.println("⚠️ [DEBUG] SKU không hợp lệ khi xóa: " + sku);
                return;
            }

            int deleted = em.createQuery(
                    "DELETE FROM GioHangItemEntity i WHERE i.gioHangId = :gid AND i.sanPhamId = :sid")
                    .setParameter("gid", gioHang.getId())
                    .setParameter("sid", sanPhamId)
                    .executeUpdate();

            em.getTransaction().commit();
            System.out.println("🗑️ [DEBUG] Đã xóa " + deleted + " sản phẩm (" + sku + ") khỏi giỏ ID " + gioHang.getId());
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            System.err.println("❌ [ERROR] Lỗi khi xóa item giỏ hàng: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * 🔹 Nạp lại giỏ hàng từ DB sau khi đăng nhập
     */
    public List<GioHangItem> loadCartAfterLogin(Long khachHangId) {
        EntityManager em = JPAUtil.em();
        List<GioHangItem> items = new ArrayList<>();

        try {
            // Tìm ID giỏ hàng của khách hàng
            List<Long> gioHangIds = em.createQuery(
                    "SELECT g.id FROM GioHang g WHERE g.chuSoHuu.id = :kid",
                    Long.class)
                    .setParameter("kid", khachHangId)
                    .getResultList();

            if (gioHangIds.isEmpty()) {
                System.out.println("⚠️ [DEBUG] Người dùng " + khachHangId + " chưa có giỏ hàng trong DB.");
                return items;
            }

            Long gioHangId = gioHangIds.get(0);

            // Truy vấn sản phẩm trong giỏ
            List<Object[]> results = em.createQuery(
                    "SELECT i.soLuong, p.id, p.tenSanPham, p.gia "
                    + "FROM GioHangItemEntity i JOIN SanPham p ON i.sanPhamId = p.id "
                    + "WHERE i.gioHangId = :gid",
                    Object[].class)
                    .setParameter("gid", gioHangId)
                    .getResultList();

            for (Object[] r : results) {
                int soLuong = ((Number) r[0]).intValue();
                Long sanPhamId = ((Number) r[1]).longValue();
                String ten = (String) r[2];
                Long gia = ((Number) r[3]).longValue();

                String hinh = "assets/img/products/" + sanPhamId + ".jpg";
                items.add(new GioHangItem("SP-" + sanPhamId, ten, hinh, gia, soLuong));
            }

            System.out.println("🛒 [DEBUG] Nạp được " + items.size() + " sản phẩm từ DB cho khách " + khachHangId);
        } catch (Exception e) {
            System.err.println("❌ [ERROR] Lỗi khi nạp giỏ hàng từ DB: " + e.getMessage());
        } finally {
            em.close();
        }

        return items;
    }

    /**
     * 🔹 Cập nhật số lượng cho 1 sản phẩm trong giỏ
     */
 public void updateItemQuantity(Long gioHangId, Long sanPhamId, int soLuong) {
    EntityManager em = JPAUtil.em();
    try {
        em.getTransaction().begin();
        em.createQuery(
            "UPDATE GioHangItemEntity i SET i.soLuong = :sl " +
            "WHERE i.gioHangId = :gid AND i.sanPhamId = :sid"
        )
        .setParameter("sl", soLuong)
        .setParameter("gid", gioHangId)
        .setParameter("sid", sanPhamId)
        .executeUpdate();
        em.getTransaction().commit();
        System.out.println("✅ [DEBUG] Update SL=" + soLuong + " cho SP=" + sanPhamId);
    } catch (Exception e) {
        em.getTransaction().rollback();
        System.err.println("❌ [ERROR] updateItemQuantity: " + e.getMessage());
    } finally {
        em.close();
    }
}

}
