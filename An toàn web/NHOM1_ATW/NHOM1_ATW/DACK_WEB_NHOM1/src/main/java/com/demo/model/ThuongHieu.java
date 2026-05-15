/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "thuong_hieu")
public class ThuongHieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ten_thuong_hieu")
    private String tenThuongHieu;
    
    public ThuongHieu() {
        
    }
    public ThuongHieu(Long id, String tenThuongHieu) {
        this.id = id;
        this.tenThuongHieu = tenThuongHieu;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenThuongHieu() {
        return tenThuongHieu;
    }

    public void setTenThuongHieu(String v) {
        this.tenThuongHieu = v;
    }
}
