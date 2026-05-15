//package com.demo.persistence;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.TypedQuery;
//import java.io.Serializable;
//import java.util.*;
//import java.util.function.Consumer;
//import java.util.function.Function;
//
//public class GenericDAO<T, ID extends Serializable> {
//    private final Class<T> clazz;
//    public GenericDAO(Class<T> clazz) { this.clazz = clazz; }
//
//    /* ===== Transaction helpers ===== */
//    public <R> R inTransaction(Function<EntityManager, R> work) {
//        EntityManager em = JPAUtil.em();
//        try {
//            em.getTransaction().begin();
//            R result = work.apply(em);
//            em.getTransaction().commit();
//            return result;
//        } catch (RuntimeException e) {
//            if (em.getTransaction().isActive()) em.getTransaction().rollback();
//            throw e;
//        } finally { if (em.isOpen()) em.close(); }
//    }
//
//    // Bản void: tên khác để tránh ambiguous
//    public void inTransactionVoid(Consumer<EntityManager> work) {
//        inTransaction(em -> { work.accept(em); return null; });
//    }
//
//    /* ===== CRUD ===== */
//    public T find(ID id) {
//        EntityManager em = JPAUtil.em();
//        try { return em.find(clazz, id); }
//        finally { if (em.isOpen()) em.close(); }
//    }
//
//    public List<T> findAll() {
//        EntityManager em = JPAUtil.em();
//        try {
//            return em.createQuery("select e from " + clazz.getSimpleName() + " e", clazz)
//                     .getResultList();
//        } finally { if (em.isOpen()) em.close(); }
//    }
//
//    public Page<T> findAll(int page, int size, String orderBy, boolean asc) {
//        EntityManager em = JPAUtil.em();
//        try {
//            String alias = "e";
//            String order = (orderBy==null||orderBy.isBlank()) ? "" :
//                    " order by " + alias + "." + orderBy + (asc ? " asc" : " desc");
//            TypedQuery<T> q = em.createQuery(
//                    "select " + alias + " from " + clazz.getSimpleName() + " " + alias + order, clazz)
//                    .setFirstResult(Math.max(0,page)*Math.max(1,size))
//                    .setMaxResults(Math.max(1,size));
//            List<T> data = q.getResultList();
//            long total = em.createQuery("select count(e) from " + clazz.getSimpleName() + " e",
//                                        Long.class).getSingleResult();
//            return new Page<>(data,page,size,total);
//        } finally { if (em.isOpen()) em.close(); }
//    }
//
//    public long count() {
//        EntityManager em = JPAUtil.em();
//        try {
//            return em.createQuery("select count(e) from " + clazz.getSimpleName() + " e",
//                                  Long.class).getSingleResult();
//        } finally { if (em.isOpen()) em.close(); }
//    }
//
//    public T save(T entity) { return inTransaction(em -> { em.persist(entity); return entity; }); }
//    public T update(T entity) { return inTransaction(em -> em.merge(entity)); }
//
//    public void deleteById(ID id) {
//        inTransactionVoid(em -> {
//            T ref = em.find(clazz, id);
//            if (ref != null) em.remove(ref);
//        });
//    }
//
//    public void delete(T entity) {
//        inTransactionVoid(em -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
//    }
//
//    /* ===== Queries ===== */
//    public List<T> findWhere(String whereClause, Map<String,Object> params) {
//        EntityManager em = JPAUtil.em();
//        try {
//            String filter = (whereClause==null||whereClause.isBlank()) ? "" : " where " + whereClause;
//            TypedQuery<T> q = em.createQuery(
//                    "select e from " + clazz.getSimpleName() + " e" + filter, clazz);
//            if (params!=null) params.forEach(q::setParameter);
//            return q.getResultList();
//        } finally { if (em.isOpen()) em.close(); }
//    }
//
//    public Page<T> findWhere(String whereClause, Map<String,Object> params,
//                             int page,int size,String orderBy,boolean asc) {
//        EntityManager em = JPAUtil.em();
//        try {
//            String base = clazz.getSimpleName(), alias="e";
//            String filter = (whereClause==null||whereClause.isBlank()) ? "" : " where " + whereClause;
//            String order = (orderBy==null||orderBy.isBlank()) ? "" :
//                    " order by " + alias + "." + orderBy + (asc ? " asc" : " desc");
//
//            String ql = "select " + alias + " from " + base + " " + alias + filter + order;
//            TypedQuery<T> q = em.createQuery(ql, clazz)
//                    .setFirstResult(Math.max(0,page)*Math.max(1,size))
//                    .setMaxResults(Math.max(1,size));
//            if (params!=null) params.forEach(q::setParameter);
//            List<T> data = q.getResultList();
//
//            String qlCount = "select count(" + alias + ") from " + base + " " + alias + filter;
//            TypedQuery<Long> cq = em.createQuery(qlCount, Long.class);
//            if (params!=null) params.forEach(cq::setParameter);
//            long total = cq.getSingleResult();
//            return new Page<>(data,page,size,total);
//        } finally { if (em.isOpen()) em.close(); }
//    }
//
//    /* ===== Page wrapper ===== */
//    public static class Page<E> {
//        private final List<E> content; private final int page,size; 
//        private final long totalElements; private final int totalPages;
//        public Page(List<E> content,int page,int size,long totalElements){
//            this.content = content==null?Collections.emptyList():content;
//            this.page=Math.max(0,page); this.size=Math.max(1,size);
//            this.totalElements=Math.max(0,totalElements);
//            this.totalPages=(int)Math.max(1,Math.ceil(this.totalElements/(double)this.size));
//        }
//        public List<E> getContent(){ return content; }
//        public int getPage(){ return page; }
//        public int getSize(){ return size; }
//        public long getTotalElements(){ return totalElements; }
//        public int getTotalPages(){ return totalPages; }
//        public boolean isFirst(){ return page<=0; }
//        public boolean isLast(){ return page>=totalPages-1; }
//    }
//}
package com.demo.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class GenericDAO<T, ID extends Serializable> {
    private final Class<T> clazz;
    public GenericDAO(Class<T> clazz) { this.clazz = clazz; }

    /* ===== Transaction helpers (CODE CŨ GIỮ NGUYÊN) ===== */
    public <R> R inTransaction(Function<EntityManager, R> work) {
        EntityManager em = JPAUtil.em();
        try {
            em.getTransaction().begin();
            R result = work.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally { if (em.isOpen()) em.close(); }
    }

    public void inTransactionVoid(Consumer<EntityManager> work) {
        inTransaction(em -> { work.accept(em); return null; });
    }

    /* ===== CRUD (CODE CŨ GIỮ NGUYÊN) ===== */
    public T find(ID id) {
        EntityManager em = JPAUtil.em();
        try { return em.find(clazz, id); }
        finally { if (em.isOpen()) em.close(); }
    }

    public List<T> findAll() {
        EntityManager em = JPAUtil.em();
        try {
            return em.createQuery("select e from " + clazz.getSimpleName() + " e", clazz)
                     .getResultList();
        } finally { if (em.isOpen()) em.close(); }
    }

    public Page<T> findAll(int page, int size, String orderBy, boolean asc) {
        EntityManager em = JPAUtil.em();
        try {
            String alias = "e";
            String order = (orderBy==null||orderBy.isBlank()) ? "" :
                    " order by " + alias + "." + orderBy + (asc ? " asc" : " desc");
            TypedQuery<T> q = em.createQuery(
                    "select " + alias + " from " + clazz.getSimpleName() + " " + alias + order, clazz)
                    .setFirstResult(Math.max(0,page)*Math.max(1,size))
                    .setMaxResults(Math.max(1,size));
            List<T> data = q.getResultList();
            long total = em.createQuery("select count(e) from " + clazz.getSimpleName() + " e",
                                        Long.class).getSingleResult();
            return new Page<>(data,page,size,total);
        } finally { if (em.isOpen()) em.close(); }
    }

    public long count() {
        EntityManager em = JPAUtil.em();
        try {
            return em.createQuery("select count(e) from " + clazz.getSimpleName() + " e",
                                    Long.class).getSingleResult();
        } finally { if (em.isOpen()) em.close(); }
    }

    public T save(T entity) { return inTransaction(em -> { em.persist(entity); return entity; }); }
    public T update(T entity) { return inTransaction(em -> em.merge(entity)); }

    public void deleteById(ID id) {
        inTransactionVoid(em -> {
            T ref = em.find(clazz, id);
            if (ref != null) em.remove(ref);
        });
    }

    public void delete(T entity) {
        inTransactionVoid(em -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
    }
    
    /* ===== Queries (CODE CŨ GIỮ NGUYÊN) ===== */
    public List<T> findWhere(String whereClause, Map<String,Object> params) {
        EntityManager em = JPAUtil.em();
        try {
            String filter = (whereClause==null||whereClause.isBlank()) ? "" : " where " + whereClause;
            TypedQuery<T> q = em.createQuery(
                    "select e from " + clazz.getSimpleName() + " e" + filter, clazz);
            if (params!=null) params.forEach(q::setParameter);
            return q.getResultList();
        } finally { if (em.isOpen()) em.close(); }
    }

    public Page<T> findWhere(String whereClause, Map<String,Object> params,
                                int page,int size,String orderBy,boolean asc) {
        EntityManager em = JPAUtil.em();
        try {
            String base = clazz.getSimpleName(), alias="e";
            String filter = (whereClause==null||whereClause.isBlank()) ? "" : " where " + whereClause;
            String order = (orderBy==null||orderBy.isBlank()) ? "" :
                    " order by " + alias + "." + orderBy + (asc ? " asc" : " desc");

            String ql = "select " + alias + " from " + base + " " + alias + filter + order;
            TypedQuery<T> q = em.createQuery(ql, clazz)
                    .setFirstResult(Math.max(0,page)*Math.max(1,size))
                    .setMaxResults(Math.max(1,size));
            if (params!=null) params.forEach(q::setParameter);
            List<T> data = q.getResultList();

            String qlCount = "select count(" + alias + ") from " + base + " " + alias + filter;
            TypedQuery<Long> cq = em.createQuery(qlCount, Long.class);
            if (params!=null) params.forEach(cq::setParameter);
            long total = cq.getSingleResult();
            return new Page<>(data,page,size,total);
        } finally { if (em.isOpen()) em.close(); }
    }

    /* ===== Page wrapper (CODE CŨ GIỮ NGUYÊN) ===== */
    public static class Page<E> {
        private final List<E> content; private final int page,size; 
        private final long totalElements; private final int totalPages;
        public Page(List<E> content,int page,int size,long totalElements){
            this.content = content==null?Collections.emptyList():content;
            this.page=Math.max(0,page); this.size=Math.max(1,size);
            this.totalElements=Math.max(0,totalElements);
            this.totalPages=(int)Math.max(1,Math.ceil(this.totalElements/(double)this.size));
        }
        public List<E> getContent(){ return content; }
        public int getPage(){ return page; }
        public int getSize(){ return size; }
        public long getTotalElements(){ return totalElements; }
        public int getTotalPages(){ return totalPages; }
        public boolean isFirst(){ return page<=0; }
        public boolean isLast(){ return page>=totalPages-1; }
    }

    // ====================================================================
    // ===== CÁC HÀM ĐƯỢC THÊM VÀO ĐỂ TƯƠNG THÍCH VỚI SERVLET CỦA BẠN =====
    // ====================================================================

    /**
     * Thêm hàm 'getById' để gọi lại hàm 'find' đã có.
     */
    public T getById(ID id) {
        return find(id);
    }

    /**
     * Thêm hàm 'delete' để gọi lại hàm 'deleteById' đã có.
     */
    public void delete(ID id) {
        deleteById(id);
    }

    /**
     * Thêm hàm 'addPhieuGiamGia' để gọi lại hàm 'save'.
     * @param entity
     */
    public void addPhieuGiamGia(T entity) {
        save(entity);
    }
}