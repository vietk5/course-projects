package com.demo.persistence;

import com.demo.model.KhachHang;
import jakarta.persistence.EntityManager;
import java.util.*;

public class KhachHangDAO extends GenericDAO<KhachHang, Long> {
    public KhachHangDAO() { super(KhachHang.class); }

    public Optional<KhachHang> findByEmail(String email) {
        var list = findWhere("lower(e.email) = :mail",
                Map.of("mail", email == null ? "" : email.toLowerCase()));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
    public Optional<KhachHang> findByEmailAndPassword(String email, String password) {
        var list = findWhere("lower(e.email) = :e and e.matKhau = :p",
                Map.of("e", email == null ? "" : email.toLowerCase(), "p", password));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public boolean emailExists(String email) {
        var list = findWhere("lower(e.email) = :e",
                Map.of("e", email == null ? "" : email.toLowerCase()));
        return !list.isEmpty();
    }

    public KhachHang create(KhachHang k) {
        // nếu GenericDAO của bạn có save/insert thì gọi thẳng; ở đây dùng JPA thuần
        EntityManager em = JPAUtil.em();
        try {
            em.getTransaction().begin();
            em.persist(k);
            em.getTransaction().commit();
            return k;
        } finally { em.close(); }
    }
    public KhachHang findById(Long id) {
    EntityManager em = JPAUtil.em();
    try {
        return em.find(KhachHang.class, id);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    } finally {
        em.close();
    }
}
}
