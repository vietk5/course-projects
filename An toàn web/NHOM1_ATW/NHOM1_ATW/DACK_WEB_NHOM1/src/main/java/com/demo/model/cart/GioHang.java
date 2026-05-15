package com.demo.model.cart;

import com.demo.model.KhachHang;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "gio_hang")
public class GioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "khach_hang_id")
    private KhachHang chuSoHuu;

    // ❌ Bỏ quan hệ JPA để tránh lỗi “non-entity target”
    @Transient
    private List<GioHangItem> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KhachHang getChuSoHuu() {
        return chuSoHuu;
    }

    public void setChuSoHuu(KhachHang chuSoHuu) {
        this.chuSoHuu = chuSoHuu;
    }

    public List<GioHangItem> getItems() {
        return items;
    }

    public void setItems(List<GioHangItem> items) {
        this.items = items;
    }
}
