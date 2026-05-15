package com.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_forget_password")
public class TokenForgetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-Nhiều với KhachHang
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) 
    private KhachHang khachHang;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "expiry_datetime", nullable = false) 
    private LocalDateTime expiryDatetime;

    /* ================== Constructors ================== */
    public TokenForgetPassword() {
    }

    public TokenForgetPassword(KhachHang khachHang, boolean isUsed, String token, LocalDateTime expiryDatetime) {
        this.khachHang = khachHang;
        this.isUsed = isUsed;
        this.token = token;
        this.expiryDatetime = expiryDatetime;
    }

    /* ================== Getters / Setters ================== */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiryDatetime() { return expiryDatetime; }
    public void setExpiryDatetime(LocalDateTime expiryDatetime) { this.expiryDatetime = expiryDatetime; }

    /* ================== toString ================== */
    @Override
    public String toString() {
        return "TokenForgetPassword{" +
                "id=" + id +
                ", khachHangId=" + (khachHang != null ? khachHang.getId() : "null") +
                ", isUsed=" + isUsed +
                ", token='" + token + '\'' +
                ", expiryDatetime=" + expiryDatetime +
                '}';
    }
}
