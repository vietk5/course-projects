/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.kho;

import java.io.*;

import jakarta.persistence.*;
@Embeddable
public class ChiTietPhieuNhapKey implements Serializable {

    @Column(name = "phieu_nhap_id")
    private Long phieuNhapId;
    @Column(name = "san_pham_id")
    private Long sanPhamId;

    public ChiTietPhieuNhapKey() {
    }

    public ChiTietPhieuNhapKey(Long p, Long s) {
        phieuNhapId = p;
        sanPhamId = s;
    }

    public Long getPhieuNhapId() {
        return phieuNhapId;
    }

    public void setPhieuNhapId(Long v) {
        this.phieuNhapId = v;
    }

    public Long getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(Long v) {
        this.sanPhamId = v;
    }
}
