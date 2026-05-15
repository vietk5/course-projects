package com.demo.persistence;

import com.demo.model.KhachHang;
import com.demo.model.TokenForgetPassword;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TokenForgetPasswordDAO {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("shopPU");

    public boolean insertTokenForget(TokenForgetPassword tokenForget) {
    EntityManager em = JPAUtil.em();
    try {
        em.getTransaction().begin();

        // Nếu có KhachHang, đảm bảo nó thuộc cùng EntityManager
        if (tokenForget.getKhachHang() != null && tokenForget.getKhachHang().getId() != null) {
            var managedKh = em.getReference(KhachHang.class, tokenForget.getKhachHang().getId());
            tokenForget.setKhachHang(managedKh);
        }

        em.persist(tokenForget);
        em.getTransaction().commit();
        return true;
        } catch (Exception e) {
        e.printStackTrace(); // In lỗi chi tiết
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        return false;
    } finally {
        em.close();
        }
    }



    public TokenForgetPassword findByToken(String token) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM TokenForgetPassword t WHERE t.token = :token", TokenForgetPassword.class)
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public boolean delete(TokenForgetPassword token) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TokenForgetPassword existing = em.find(TokenForgetPassword.class, token.getId());
            if (existing != null) {
                em.remove(existing);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    public boolean deleteByToken(String token) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TokenForgetPassword existing = em.createQuery(
                    "SELECT t FROM TokenForgetPassword t WHERE t.token = :token", TokenForgetPassword.class)
                    .setParameter("token", token)
                    .getSingleResult();
            if (existing != null) {
                em.remove(existing);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }
}
