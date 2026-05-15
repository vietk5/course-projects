package com.demo.model;

import com.demo.enums.LoaiThanhVien;
import jakarta.persistence.*;

@Entity
@Table(name = "khach_hang")
public class KhachHang extends NguoiDung {

    @Enumerated(EnumType.STRING)
    @Column(name = "hang_thanh_vien")
    private LoaiThanhVien hangThanhVien = LoaiThanhVien.BAC;

    @OneToOne(mappedBy = "chuSoHuu", cascade = CascadeType.ALL, orphanRemoval = true)
    private com.demo.model.cart.GioHang gioHang;

    // --- MẬT KHẨU: lưu plain text (không hash) ---
    @Column(name = "mat_khau", length = 255)
    private String matKhau;

    /* ================== getter/setter ================== */

    public LoaiThanhVien getHangThanhVien() {
        return hangThanhVien;
    }

    public void setHangThanhVien(LoaiThanhVien v) {
        this.hangThanhVien = v;
    }

    public com.demo.model.cart.GioHang getGioHang() {
        return gioHang;
    }

    public void setGioHang(com.demo.model.cart.GioHang g) {
        this.gioHang = g;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        // NguoiDung đã có field tên (ví dụ: ten); ánh xạ lại cho code cũ
        return this.getTen();
    }
}
