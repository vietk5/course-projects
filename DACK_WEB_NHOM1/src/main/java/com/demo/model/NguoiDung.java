/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nguoi_dung")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class NguoiDung extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ten")
    private String ten;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "sdt")
    private String sdt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dia_chi_id")
    private DiaChi diaChi;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String v) {
        this.ten = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        this.email = v;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String v) {
        this.sdt = v;
    }

    public DiaChi getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(DiaChi v) {
        this.diaChi = v;
    }
}
