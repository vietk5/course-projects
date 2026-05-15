/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.order;

import com.demo.enums.TrangThaiDonHang;
import com.demo.model.*;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "don_hang")
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ngay_dat_hang")
    private LocalDateTime ngayDatHang = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id")
    private KhachHang khachHang;
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiDonHang trangThai = TrangThaiDonHang.MOI;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pttt_id")
    private PhuongThucThanhToan thanhToan;
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDonHang> chiTiet = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getNgayDatHang() {
        return ngayDatHang;
    }

    public void setNgayDatHang(LocalDateTime v) {
        this.ngayDatHang = v;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang v) {
        this.khachHang = v;
    }

    public TrangThaiDonHang getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiDonHang v) {
        this.trangThai = v;
    }

    public PhuongThucThanhToan getThanhToan() {
        return thanhToan;
    }

    public void setThanhToan(PhuongThucThanhToan v) {
        this.thanhToan = v;
    }

    public List<ChiTietDonHang> getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(List<ChiTietDonHang> v) {
        this.chiTiet = v;
    }
}
