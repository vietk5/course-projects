/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.database;

import com.demo.model.*;
import com.demo.persistence.JPAUtil;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;


public class ThuongHieuDB {
    public static List<String> selectAllTenThuongHieu() {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT th.tenThuongHieu FROM ThuongHieu th";  // dùng alias và tên field trong entity
        TypedQuery<String> query = em.createQuery(qString, String.class);
        try {
            List<String> tenThuongHieuList = query.getResultList();
            return tenThuongHieuList;
        } finally {
            em.close();
        }
    }
    public static String selectTenThuongHieuById(int id) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT th.tenThuongHieu FROM ThuongHieu th "
                + " WHERE s.id = :id";
        TypedQuery<String> query = em.createQuery(qString, String.class);
        query.setParameter("id", id);
        try {
            String tenThuongHieu = query.getSingleResult();
            return tenThuongHieu;
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
    public static ThuongHieu selectThuongHieuByTen(String tenThuongHieu) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT th FROM ThuongHieu th " 
                + "WHERE th.tenThuongHieu = :tenThuongHieu";
        TypedQuery<ThuongHieu> query = em.createQuery(qString, ThuongHieu.class);
        query.setParameter("tenThuongHieu", tenThuongHieu);
        try {
            ThuongHieu thuongHieu = query.getSingleResult();
            return thuongHieu;
        } finally {
            em.close();
        }
    }
}
