package com.demo.persistence;

import com.demo.model.PhieuGiamGia;

// Kế thừa GenericDAO để có sẵn các hàm findAll(), findById(), save(), deleteById()
public class PhieuGiamGiaDAO extends GenericDAO<PhieuGiamGia, Long> {
    
    public PhieuGiamGiaDAO() {
        super(PhieuGiamGia.class);
    }

    
}