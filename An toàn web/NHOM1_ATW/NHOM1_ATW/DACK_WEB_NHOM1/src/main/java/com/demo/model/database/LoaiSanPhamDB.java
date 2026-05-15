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

public class LoaiSanPhamDB {
    public static List<String> selectAllTenLoaiSanPham() {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT l.tenLoai FROM LoaiSanPham l";  // dùng alias và tên field trong entity
        TypedQuery<String> query = em.createQuery(qString, String.class);
        try {
            List<String> tenLoaiList = query.getResultList();
            return tenLoaiList;
        } finally {
            em.close();
        }
    }
    public static String selectTenSanPhamById(int id) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT s.tenSanPham FROM SanPham s"
                + " WHERE s.id = :id";
        TypedQuery<String> query = em.createQuery(qString, String.class);
        query.setParameter("id", id);
        try {
            String tenSanPham = query.getSingleResult();
            return tenSanPham;
        } catch (NoResultException e) {
            return null; // hoặc trả về "Không tìm thấy"
        } finally {
            em.close();
        }
    }
    public static LoaiSanPham selectLoaiSanPhamByTen(String tenLoaiSanPham) {
        EntityManager em = JPAUtil.getEmFactory().createEntityManager();
        String qString = "SELECT l FROM LoaiSanPham l " 
                + "WHERE l.tenLoai= :tenLoai";
        TypedQuery<LoaiSanPham> query = em.createQuery(qString, LoaiSanPham.class);
        query.setParameter("tenLoai", tenLoaiSanPham);
        try {
            LoaiSanPham loaiSanPham = query.getSingleResult();
            return loaiSanPham;
        } finally {
            em.close();
        }
    }
}
