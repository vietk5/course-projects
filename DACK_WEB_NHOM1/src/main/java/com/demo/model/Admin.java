package com.demo.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Admin kế thừa NguoiDung.
 * - Lưu username (taiKhoan) và mật khẩu (matKhau).
 */
@Entity
@Table(name = "admin")
public class Admin extends NguoiDung implements Serializable {

    @Column(name = "tai_khoan", unique = true, length = 100)
    private String taiKhoan;

    @Column(name = "mat_khau", length = 255)
    private String matKhau;

    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String v) { this.taiKhoan = v; }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    /** Trả về id admin để tương thích với code cũ.
     *  NguoiDung nên có getId(). Nếu base class là getIdNguoiDung() thì đổi lại. */
    @Transient
    public Long getIdAdmin() {
        return super.getId();   // <--- nếu NguoiDung dùng tên khác, đổi thành super.getIdNguoiDung()
    }
}
