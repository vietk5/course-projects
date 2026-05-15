/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model;


import jakarta.persistence.*;

@Entity
@Table(name = "gio_hang_item")
public class GioHangItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "gio_hang_id")
    private Long gioHangId;

    @Column(name = "san_pham_id")
    private Long sanPhamId;

    // ✅ Constructors
    public GioHangItemEntity() {}

    public GioHangItemEntity(Long gioHangId, Long sanPhamId, int soLuong) {
        this.gioHangId = gioHangId;
        this.sanPhamId = sanPhamId;
        this.soLuong = soLuong;
    }

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public int getSoLuong() {
        return soLuong;
    }
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public Long getGioHangId() {
        return gioHangId;
    }
    public void setGioHangId(Long gioHangId) {
        this.gioHangId = gioHangId;
    }

    public Long getSanPhamId() {
        return sanPhamId;
    }
    public void setSanPhamId(Long sanPhamId) {
        this.sanPhamId = sanPhamId;
    }
}
