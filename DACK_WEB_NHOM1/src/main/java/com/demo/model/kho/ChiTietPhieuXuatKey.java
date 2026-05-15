/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.kho;

import java.io.*;

import jakarta.persistence.*;
@Embeddable
public class ChiTietPhieuXuatKey implements Serializable {

    @Column(name = "phieu_xuat_id")
    private Long phieuXuatId;
    @Column(name = "san_pham_id")
    private Long sanPhamId;

    public ChiTietPhieuXuatKey() {
    }

    public ChiTietPhieuXuatKey(Long p, Long s) {
        phieuXuatId = p;
        sanPhamId = s;
    }

    public Long getPhieuXuatId() {
        return phieuXuatId;
    }

    public void setPhieuXuatId(Long v) {
        this.phieuXuatId = v;
    }

    public Long getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(Long v) {
        this.sanPhamId = v;
    }
}
