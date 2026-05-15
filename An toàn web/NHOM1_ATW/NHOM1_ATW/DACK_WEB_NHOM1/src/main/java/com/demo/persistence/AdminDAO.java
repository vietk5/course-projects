package com.demo.persistence;

import jakarta.persistence.EntityManager;
import com.demo.model.Admin;
import java.util.*;

public class AdminDAO extends GenericDAO<Admin, Long> {

    public AdminDAO() {
        super(Admin.class);
    }

    /**
     * Tìm admin theo “username” (mặc định dùng email/ten). Nếu entity của bạn
     * có field username thì đổi where thành e.username = :u
     */
    public Optional<Admin> findByUsername(String username) {
        var list = findWhere("lower(e.email) = :u OR lower(e.ten) = :u",
                Map.of("u", username == null ? "" : username.toLowerCase()));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // Tìm theo tài khoản (ưu tiên taiKhoan, rồi email)
    public Optional<Admin> findByAccount(String account) {
        if (account == null) {
            account = "";
        }
        account = account.toLowerCase();
        var list = findWhere(
                "(lower(e.taiKhoan) = :acc) OR (lower(e.email) = :acc)",
                Map.of("acc", account));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // Seed admin mặc định nếu chưa có
    public void ensureDefaultAdmin() {
        EntityManager em = JPAUtil.em();
        try {
            Long cnt = em.createQuery("select count(a) from Admin a where a.taiKhoan = :u", Long.class)
                    .setParameter("u", "admindackweb")
                    .getSingleResult();
            if (cnt == 0) {
                em.getTransaction().begin();
                Admin a = new Admin();
                a.setTaiKhoan("admindackweb");
                a.setEmail("admin@electromart.local");
                a.setTen("Quản trị");
                a.setMatKhau("P@ssw0rd"); // DEV: plaintext
                em.persist(a);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }
}
