package com.demo.enums;

public enum TrangThaiDonHang {
    MOI("Mới"),
    DANG_XU_LY("Đang xử lý"),
    DA_THANH_TOAN("Đã thanh toán"),
    DANG_GIAO("Đang giao"),
    HOAN_TAT("Hoàn tất"),
    DA_HUY("Đã hủy"),
    TRA_HANG("Trả hàng");

    private final String displayName;

    TrangThaiDonHang(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}