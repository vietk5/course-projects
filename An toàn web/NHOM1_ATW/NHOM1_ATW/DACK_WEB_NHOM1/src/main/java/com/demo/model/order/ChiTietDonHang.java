/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.order;

import com.demo.model.SanPham; 
import jakarta.persistence.*;

import java.math.*;

@Entity
@Table(name = "chi_tiet_don_hang")
public class ChiTietDonHang {

    @EmbeddedId
    private ChiTietDonHangKey id = new ChiTietDonHangKey();
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("donHangId")
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sanPhamId")
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham;
    @Column(name = "so_luong")
    private Integer soLuong;
    @Column(name = "don_gia", precision = 15, scale = 2)
    private BigDecimal donGia;

    public ChiTietDonHangKey getId() {
        return id;
    }

    public void setId(ChiTietDonHangKey id) {
        this.id = id;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang v) {
        this.donHang = v;
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
