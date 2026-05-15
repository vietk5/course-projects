/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model;


import jakarta.persistence.*;
@Entity
@Table(name = "phuong_thuc_tt")
public class PhuongThucThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ten_phuong_thuc")
    private String tenPhuongThuc;
    @Column(name = "mo_ta")
    private String moTa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenPhuongThuc() {
        return tenPhuongThuc;
    }

    public void setTenPhuongThuc(String v) {
        this.tenPhuongThuc = v;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String v) {
        this.moTa = v;
    }
}
