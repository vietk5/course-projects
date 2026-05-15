/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model;

import java.io.Serializable;
import jakarta.persistence.*; 
import java.math.*;

import java.time.*;

@Entity
@Table(name = "san_pham")
public class SanPham extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ten_san_pham")
    private String tenSanPham;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thuong_hieu_id")
    private ThuongHieu thuongHieu;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loai_id")
    private LoaiSanPham loai;
    @Column(name = "gia", precision = 15, scale = 2)
    private BigDecimal gia;
    @Column(name = "mo_ta_ngan")
    @Lob
    private String moTaNgan;
    @Column(name = "ngay_cap_phat")
    private LocalDate ngayCapPhat = LocalDate.now();
    @Column(name = "so_luong_ton")
    private Integer soLuongTon = 0;
    
    public SanPham() {
        
    }
    public SanPham(String tenSanPham, ThuongHieu thuongHieu, LoaiSanPham loai, BigDecimal gia, String moTaNgan, LocalDate ngayCapPhat, Integer soLuongTon) {
        this.tenSanPham = tenSanPham;
        this.thuongHieu = thuongHieu;
        this.loai = loai;
        this.gia = gia;
        this.moTaNgan = moTaNgan;
        this.ngayCapPhat = ngayCapPhat;
        this.soLuongTon = soLuongTon;
    }
        
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String v) {
        this.tenSanPham = v;
    }

    public ThuongHieu getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(ThuongHieu v) {
        this.thuongHieu = v;
    }

    public LoaiSanPham getLoai() {
        return loai;
    }

    public void setLoai(LoaiSanPham v) {
        this.loai = v;
    }

    public BigDecimal getGia() {
        return gia;
    }

    public void setGia(BigDecimal v) {
        this.gia = v;
    }

    public String getMoTaNgan() {
        return moTaNgan;
    }

    public void setMoTaNgan(String v) {
        this.moTaNgan = v;
    }

    public LocalDate getNgayCapPhat() {
        return ngayCapPhat;
    }

    public void setNgayCapPhat(LocalDate v) {
        this.ngayCapPhat = v;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer v) {
        this.soLuongTon = v;
    }
}
