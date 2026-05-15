package com.demo.model;

import com.demo.enums.LoaiGiamGia;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "phieu_giam_gia")
public class PhieuGiamGia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma", unique = true, length = 64)
    private String ma;

    @Enumerated(EnumType.STRING)
    @Column(name = "kieu", length = 16)
    private LoaiGiamGia kieu;  // Discount type (percentage or fixed)

    @Column(name = "gia_tri", precision = 15, scale = 2)
    private BigDecimal giaTri;  // The value of the discount

    @Column(name = "giam_toi_da", precision = 15, scale = 2)
    private BigDecimal giamToiDa;  // Max discount

    @Column(name = "don_toi_thieu", precision = 15, scale = 2)
    private BigDecimal donToiThieu;  // Minimum order value for discount

    @Column(name = "mo_ta")
    private String moTa;  // Description of the promotion

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;  // Start date/time of the promotion

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;  // End date/time of the promotion

    @Column(name = "active", nullable = false)
    private boolean active = true;  // Whether the promotion is active or not

    @Column(name = "ap_dung_toan_bo", nullable = false)
    private boolean apDungToanBo = false;  // Whether the promotion applies to all products

    @ManyToMany
    @JoinTable(name = "phieu_giam_gia_loai",
            joinColumns = @JoinColumn(name = "phieu_id"),
            inverseJoinColumns = @JoinColumn(name = "loai_id"))
    private Set<LoaiSanPham> loaiApDung = new HashSet<>();  // Products' categories to which the promotion applies

    @ManyToMany
    @JoinTable(name = "phieu_giam_gia_san_pham",
            joinColumns = @JoinColumn(name = "phieu_id"),
            inverseJoinColumns = @JoinColumn(name = "san_pham_id"))
    private Set<SanPham> sanPhamApDung = new HashSet<>();  // Specific products to which the promotion applies

    // Getter and Setter methods

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }

    public LoaiGiamGia getKieu() { return kieu; }
    public void setKieu(LoaiGiamGia kieu) { this.kieu = kieu; }

    public BigDecimal getGiaTri() { return giaTri; }
    public void setGiaTri(BigDecimal giaTri) { this.giaTri = giaTri; }

    public BigDecimal getGiamToiDa() { return giamToiDa; }
    public void setGiamToiDa(BigDecimal giamToiDa) { this.giamToiDa = giamToiDa; }

    public BigDecimal getDonToiThieu() { return donToiThieu; }
    public void setDonToiThieu(BigDecimal donToiThieu) { this.donToiThieu = donToiThieu; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public LocalDateTime getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDateTime ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public LocalDateTime getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDateTime ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isApDungToanBo() { return apDungToanBo; }
    public void setApDungToanBo(boolean apDungToanBo) { this.apDungToanBo = apDungToanBo; }

    public Set<LoaiSanPham> getLoaiApDung() { return loaiApDung; }
    public void setLoaiApDung(Set<LoaiSanPham> loaiApDung) { this.loaiApDung = loaiApDung; }

    public Set<SanPham> getSanPhamApDung() { return sanPhamApDung; }
    public void setSanPhamApDung(Set<SanPham> sanPhamApDung) { this.sanPhamApDung = sanPhamApDung; }
}
