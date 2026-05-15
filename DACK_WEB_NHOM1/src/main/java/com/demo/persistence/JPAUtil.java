package com.demo.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("shopPU");

    private JPAUtil() { }

    /** Lấy EntityManager mới cho mỗi lần gọi */
    public static EntityManager em() {
        return EMF.createEntityManager();
    }

    /** Nếu nơi khác cần dùng EMF trực tiếp */
    public static EntityManagerFactory getEmFactory() {
        return EMF;
    }

    /** Đóng EMF khi ứng dụng dừng (tùy chọn) */
    public static void close() {
        if (EMF != null && EMF.isOpen()) EMF.close();
    }
}
