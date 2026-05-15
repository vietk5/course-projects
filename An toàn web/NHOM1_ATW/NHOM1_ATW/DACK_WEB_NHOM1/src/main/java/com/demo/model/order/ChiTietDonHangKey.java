/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model.order;

import java.io.*;

import jakarta.persistence.*;
@Embeddable
public class ChiTietDonHangKey implements Serializable {

    @Column(name = "don_hang_id")
    private Long donHangId;
    @Column(name = "san_pham_id")
    private Long sanPhamId;

    public ChiTietDonHangKey() {
    }

    public ChiTietDonHangKey(Long d, Long s) {
        donHangId = d;
        sanPhamId = s;
    }

    public Long getDonHangId() {
        return donHangId;
    }

    public void setDonHangId(Long v) {
        this.donHangId = v;
    }

    public Long getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(Long v) {
        this.sanPhamId = v;
    }
}
