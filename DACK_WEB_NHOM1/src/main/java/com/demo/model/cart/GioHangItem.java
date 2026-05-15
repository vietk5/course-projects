package com.demo.model.cart;

public class GioHangItem {
    private String sku;
    private String ten;
    private String hinh;
    private long gia;
    private int soLuong;

    public GioHangItem() {}

    public GioHangItem(String sku, String ten, String hinh, long gia, int soLuong) {
        this.sku = sku;
        this.ten = ten;
        this.hinh = hinh;
        this.gia = gia;
        this.soLuong = soLuong;
    }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getHinh() { return hinh; }
    public void setHinh(String hinh) { this.hinh = hinh; }

    public long getGia() { return gia; }
    public void setGia(long gia) { this.gia = gia; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
}
