/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dia_chi")
public class DiaChi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "so_nha")
    private String soNha;
    @Column(name = "ten_duong")
    private String tenDuong;
    @Column(name = "phuong_xa")
    private String phuongXa;
    @Column(name = "quan_huyen")
    private String quanHuyen;
    @Column(name = "tinh_thanh")
    private String tinhThanh;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSoNha() {
        return soNha;
    }

    public void setSoNha(String v) {
        this.soNha = v;
    }

    public String getTenDuong() {
        return tenDuong;
    }

    public void setTenDuong(String v) {
        this.tenDuong = v;
    }

    public String getPhuongXa() {
        return phuongXa;
    }

    public void setPhuongXa(String v) {
        this.phuongXa = v;
    }

    public String getQuanHuyen() {
        return quanHuyen;
    }

    public void setQuanHuyen(String v) {
        this.quanHuyen = v;
    }

    public String getTinhThanh() {
        return tinhThanh;
    }

    public void setTinhThanh(String v) {
        this.tinhThanh = v;
    }
}
