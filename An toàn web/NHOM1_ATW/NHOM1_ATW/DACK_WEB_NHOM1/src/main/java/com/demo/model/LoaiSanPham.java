/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "loai_san_pham")
public class LoaiSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ten_loai")
    private String tenLoai;
    @Column(name = "ngay_tao")
    private LocalDate ngayTao = LocalDate.now();
    
    public LoaiSanPham()
    {
        
    }
    public LoaiSanPham(Long id, String tenLoai) {
        this.id = id;
        this.tenLoai = tenLoai;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String v) {
        this.tenLoai = v;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate v) {
        this.ngayTao = v;
    }
}
