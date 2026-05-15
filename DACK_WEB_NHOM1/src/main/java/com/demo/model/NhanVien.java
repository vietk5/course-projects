/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nhan_vien")
public class NhanVien extends NguoiDung {

    @Column(name = "chuc_vu")
    private String chucVu;

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String v) {
        this.chucVu = v;
    }
}
