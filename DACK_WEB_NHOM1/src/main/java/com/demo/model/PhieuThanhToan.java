/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model;

import com.demo.model.order.DonHang; 
import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "phieu_thanh_toan")
public class PhieuThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "thoi_gian_thanh_toan")
    private LocalDateTime thoiGianThanhToan = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pttt_id")
    private PhuongThucThanhToan phuongThuc;
    @Column(name = "trang_thai")
    private String trangThai;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    public void setThoiGianThanhToan(LocalDateTime v) {
        this.thoiGianThanhToan = v;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang v) {
        this.donHang = v;
    }

    public PhuongThucThanhToan getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(PhuongThucThanhToan v) {
        this.phuongThuc = v;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String v) {
        this.trangThai = v;
    }
}
