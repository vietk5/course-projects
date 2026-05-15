/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.cs;

import com.demo.model.KhachHang;
import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "phieu_ho_tro")
public class PhieuHoTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id")
    private KhachHang khachHang;
    @Column(name = "tieu_de")
    private String tieuDe;
    @Column(name = "noi_dung", length = 2000)
    private String noiDung;
    @Column(name = "ngay_tao")
    private LocalDate ngayTao = LocalDate.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang v) {
        this.khachHang = v;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String v) {
        this.tieuDe = v;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String v) {
        this.noiDung = v;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate v) {
        this.ngayTao = v;
    }
}
