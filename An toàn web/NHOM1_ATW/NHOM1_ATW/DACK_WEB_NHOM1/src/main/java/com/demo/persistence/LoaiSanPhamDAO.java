package com.demo.persistence;

import com.demo.model.LoaiSanPham;

// Kế thừa từ GenericDAO để có sẵn các phương thức findById, findAll, save, delete...
public class LoaiSanPhamDAO extends GenericDAO<LoaiSanPham, Long> {

    public LoaiSanPhamDAO() {
        super(LoaiSanPham.class);
    }

    // Bạn không cần viết lại các phương thức CRUD cơ bản.
    // Thêm các phương thức truy vấn riêng cho LoaiSanPham ở đây nếu cần.
}