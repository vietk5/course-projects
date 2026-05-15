/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.kho;

import jakarta.persistence.*; 
import java.time.*;

import java.util.*;
import  com.demo.model.kho.*;
@Entity
@Table(name = "phieu_xuat")
public class PhieuXuat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ngay_xuat")
    private LocalDate ngayXuat = LocalDate.now();
    @OneToMany(mappedBy = "phieuXuat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietPhieuXuat> chiTiet = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getNgayXuat() {
        return ngayXuat;
    }

    public void setNgayXuat(LocalDate v) {
        this.ngayXuat = v;
    }

    public List<ChiTietPhieuXuat> getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(List<ChiTietPhieuXuat> v) {
        this.chiTiet = v;
    }
}
