/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.database;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import com.demo.model.*;
import com.demo.persistence.JPAUtil;
import jakarta.persistence.Query;

public class SanPhamDB {

    public static List<SanPham> selectAllSanPham() {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT s FROM SanPham s";  // dùng alias và tên field trong entity
        TypedQuery<SanPham> query = em.createQuery(qString, SanPham.class);
        try {
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public static void insert(SanPham sanPham) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.persist(sanPham);
            trans.commit();
        } catch (Exception e) {
            System.out.println(e);
            trans.rollback();
        } finally {
            em.close();
        }
    }

    public static void delete(SanPham sanPham) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.remove(em.merge(sanPham));
            trans.commit();
        } catch (Exception e) {
            System.out.println(e);
            trans.rollback();
        } finally {
            em.close();
        }
    }

    public static boolean isProductExistById(Long id) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        try {
            SanPham sanPham = em.find(SanPham.class, id);
            return sanPham != null;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        } finally {
            em.close();
        }
    }
    public static SanPham selectSanPhamByTen(String tenSanPham) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT s FROM SanPham s "
                + "WHERE s.tenSanPham= :tenSanPham";
        TypedQuery<SanPham> query = em.createQuery(qString, SanPham.class);
        query.setParameter("tenSanPham", tenSanPham);
        try {
            SanPham sanPham = query.getSingleResult();
            return sanPham;
        } catch (NoResultException e) {
            // Nếu không tìm thấy sản phẩm nào
            return null;
        }
        finally {
            em.close();
        }
    }
    public static Long selectIDSanPhamByTen(String tenSanPham) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT s.id FROM SanPham s " 
                + "WHERE s.tenSanPham= :tenSanPham";
        TypedQuery<Long> query = em.createQuery(qString, Long.class);
        query.setParameter("tenSanPham", tenSanPham);
        try {
            Long id = query.getSingleResult();
            return id;
        } 
        catch (NoResultException e) {
            // Nếu không tìm thấy sản phẩm nào
            return null;
        }
        finally {
            em.close();
        }
    }
    public static List<SanPham> selectAllSanPhamByTenLoai(String tenLoaiSanPham) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT sp FROM SanPham sp "
                + "WHERE sp.loai.tenLoai = :tenLoaiSanPham";
        TypedQuery<SanPham> query = em.createQuery(qString, SanPham.class);
        query.setParameter("tenLoaiSanPham", tenLoaiSanPham);
        try {
            List<SanPham> daSanPham = query.getResultList();
            return daSanPham;
        } 
        catch (NoResultException e) {
            // Nếu không tìm thấy sản phẩm nào
            return null;
        }
        finally {
            em.close();
        }
    }
    public static SanPham selectSanPhamById(Long id) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT sp FROM SanPham sp "
                + "WHERE sp.id = :id";
        TypedQuery<SanPham> query = em.createQuery(qString, SanPham.class);
        query.setParameter("id", id);
        try {
            SanPham sanPham = query.getSingleResult();
            return sanPham;
        } 
        catch (NoResultException e) {
            // Nếu không tìm thấy sản phẩm nào
            return null;
        }
        finally {
            em.close();
        }
    }
    public static List<SanPham> selectAllSanPhamByLoaiHoacTHieu(String tenLoaiSanPham, String tenThuongHieu) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT sp FROM SanPham sp "
                + "WHERE sp.loai.tenLoai = :tenLoaiSanPham "
                + "or sp.thuongHieu.tenThuongHieu = :tenThuongHieu";
        TypedQuery<SanPham> query = em.createQuery(qString, SanPham.class);
        query.setParameter("tenLoaiSanPham", tenLoaiSanPham);
        query.setParameter("tenThuongHieu", tenThuongHieu);
        try {
            List<SanPham> daSanPham = query.getResultList();
            return daSanPham;
        } 
        catch (NoResultException e) {
            // Nếu không tìm thấy sản phẩm nào
            return null;
        }
        finally {
            em.close();
        }
    }
    public static List<SanPham> select8SanPham() {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT s FROM SanPham s";
        TypedQuery<SanPham> query = em.createQuery(qString, SanPham.class);
        query.setMaxResults(8);
        try {
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public static boolean updateSoLuongTonById(Long id, int delta) {
        if (delta <= 0) {
            delta = 1; // an toàn
        }
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();

        // COALESCE để phòng soLuongTon null
        String ql = "UPDATE SanPham s "
                + "SET s.soLuongTon = COALESCE(s.soLuongTon, 0) + :d "
                + "WHERE s.id = :id";

        try {
            trans.begin();
            int n = em.createQuery(ql)
                    .setParameter("d", delta)
                    .setParameter("id", id)
                    .executeUpdate();
            trans.commit();
            return n > 0;
        } catch (Exception e) {
            System.out.println(e);
            if (trans != null && trans.isActive()) {
                trans.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Tăng tồn kho theo TÊN sản phẩm với số lượng tùy ý. Tiện khi bạn chỉ có
     * tên trong form xác nhận. Trả về true nếu có bản ghi được cập nhật.
     */
    public static boolean updateSoLuongTonByTen(String tenSanPham, int delta) {
        if (delta <= 0) {
            delta = 1;
        }
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();

        String ql = "UPDATE SanPham s "
                + "SET s.soLuongTon = COALESCE(s.soLuongTon, 0) + :d "
                + "WHERE s.tenSanPham = :ten";

        try {
            trans.begin();
            int n = em.createQuery(ql)
                    .setParameter("d", delta)
                    .setParameter("ten", tenSanPham)
                    .executeUpdate();
            trans.commit();
            return n > 0;
        } catch (Exception e) {
            System.out.println(e);
            if (trans != null && trans.isActive()) {
                trans.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }
}
