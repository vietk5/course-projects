/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.kho;

import jakarta.persistence.*; import java.time.*;

import java.util.*;
@Entity
@Table(name = "phieu_nhap")
public class PhieuNhap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ngay_nhap")
    private LocalDate ngayNhap = LocalDate.now();
    @OneToMany(mappedBy = "phieuNhap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietPhieuNhap> chiTiet = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate v) {
        this.ngayNhap = v;
    }

    public List<ChiTietPhieuNhap> getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(List<ChiTietPhieuNhap> v) {
        this.chiTiet = v;
    }
}
