/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.kho;

import com.demo.model.SanPham; 
import jakarta.persistence.*;

import java.math.*;
@Entity
@Table(name = "chi_tiet_phieu_xuat")
public class ChiTietPhieuXuat {

    @EmbeddedId
    private ChiTietPhieuXuatKey id = new ChiTietPhieuXuatKey();
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("phieuXuatId")
    @JoinColumn(name = "phieu_xuat_id")
    private PhieuXuat phieuXuat;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sanPhamId")
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham;
    @Column(name = "so_luong")
    private Integer soLuong;
    @Column(name = "don_gia", precision = 15, scale = 2)
    private BigDecimal donGia;

    public ChiTietPhieuXuatKey getId() {
        return id;
    }

    public void setId(ChiTietPhieuXuatKey id) {
        this.id = id;
    }

    public PhieuXuat getPhieuXuat() {
        return phieuXuat;
    }

    public void setPhieuXuat(PhieuXuat v) {
        this.phieuXuat = v;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham v) {
        this.sanPham = v;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer v) {
        this.soLuong = v;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal v) {
        this.donGia = v;
    }
}
