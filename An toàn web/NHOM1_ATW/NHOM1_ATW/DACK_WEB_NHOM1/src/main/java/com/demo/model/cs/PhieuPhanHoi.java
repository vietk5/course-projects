/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.cs;

import com.demo.model.NhanVien; 
import jakarta.persistence.*;

import java.time.*;
@Entity
@Table(name = "phieu_phan_hoi")
public class PhieuPhanHoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ho_tro_id")
    private PhieuHoTro hoTro;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhan_vien_id")
    private NhanVien nhanVien;
    @Column(name = "noi_dung", length = 2000)
    private String noiDung;
    @Column(name = "ngay_phan_hoi")
    private LocalDate ngayPhanHoi = LocalDate.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PhieuHoTro getHoTro() {
        return hoTro;
    }

    public void setHoTro(PhieuHoTro v) {
        this.hoTro = v;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien v) {
        this.nhanVien = v;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String v) {
        this.noiDung = v;
    }

    public LocalDate getNgayPhanHoi() {
        return ngayPhanHoi;
    }

    public void setNgayPhanHoi(LocalDate v) {
        this.ngayPhanHoi = v;
    }
}
