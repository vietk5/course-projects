-- =====================================================
-- SCRIPT TẠO DATABASE CHO HỆ THỐNG E-COMMERCE
-- Database: PostgreSQL
-- Encoding: UTF-8
-- =====================================================

-- Tạo database (chạy riêng nếu cần)
-- CREATE DATABASE electromart_db;
-- \c electromart_db;

-- =====================================================
-- 0. XÓA CÁC BẢNG CŨ (NẾU TỒN TẠI)
-- =====================================================

-- Xóa theo thứ tự ngược lại để tránh lỗi foreign key
DROP TABLE IF EXISTS chi_tiet_phieu_xuat CASCADE;
DROP TABLE IF EXISTS chi_tiet_phieu_nhap CASCADE;
DROP TABLE IF EXISTS phieu_xuat CASCADE;
DROP TABLE IF EXISTS phieu_nhap CASCADE;
DROP TABLE IF EXISTS phieu_giam_gia_san_pham CASCADE;
DROP TABLE IF EXISTS phieu_giam_gia_loai CASCADE;
DROP TABLE IF EXISTS phieu_giam_gia CASCADE;
DROP TABLE IF EXISTS token_forget_password CASCADE;
DROP TABLE IF EXISTS phieu_thanh_toan CASCADE;
DROP TABLE IF EXISTS chi_tiet_don_hang CASCADE;
DROP TABLE IF EXISTS don_hang CASCADE;
DROP TABLE IF EXISTS gio_hang CASCADE;
DROP TABLE IF EXISTS san_pham CASCADE;
DROP TABLE IF EXISTS loai_san_pham CASCADE;
DROP TABLE IF EXISTS thuong_hieu CASCADE;
DROP TABLE IF EXISTS phuong_thuc_tt CASCADE;
DROP TABLE IF EXISTS nhan_vien CASCADE;
DROP TABLE IF EXISTS admin CASCADE;
DROP TABLE IF EXISTS khach_hang CASCADE;
DROP TABLE IF EXISTS nguoi_dung CASCADE;
DROP TABLE IF EXISTS dia_chi CASCADE;

-- =====================================================
-- 1. TẠO CÁC BẢNG CƠ BẢN
-- =====================================================

-- Bảng địa chỉ
CREATE TABLE dia_chi (
    id BIGSERIAL PRIMARY KEY,
    so_nha VARCHAR(255),
    ten_duong VARCHAR(255),
    phuong_xa VARCHAR(255),
    quan_huyen VARCHAR(255),
    tinh_thanh VARCHAR(255)
);

-- Bảng người dùng (base table cho inheritance)
CREATE TABLE nguoi_dung (
    id BIGSERIAL PRIMARY KEY,
    ten VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    sdt VARCHAR(255),
    dia_chi_id BIGINT REFERENCES dia_chi(id),
    ngay_tao DATE DEFAULT CURRENT_DATE,
    ngay_cap_nhat DATE
);

-- Bảng khách hàng (kế thừa từ nguoi_dung)
CREATE TABLE khach_hang (
    id BIGINT PRIMARY KEY REFERENCES nguoi_dung(id),
    hang_thanh_vien VARCHAR(20) DEFAULT 'BAC' CHECK (hang_thanh_vien IN ('BAC', 'VANG', 'KIMCUONG', 'BACHKIM')),
    mat_khau VARCHAR(255)
);

-- Bảng admin (kế thừa từ nguoi_dung)
CREATE TABLE admin (
    id BIGINT PRIMARY KEY REFERENCES nguoi_dung(id),
    tai_khoan VARCHAR(100) UNIQUE,
    mat_khau VARCHAR(255)
);

-- Bảng nhân viên (kế thừa từ nguoi_dung)
CREATE TABLE nhan_vien (
    id BIGINT PRIMARY KEY REFERENCES nguoi_dung(id),
    chuc_vu VARCHAR(255)
);

-- =====================================================
-- 2. BẢNG SẢN PHẨM VÀ DANH MỤC
-- =====================================================

-- Bảng thương hiệu
CREATE TABLE thuong_hieu (
    id BIGSERIAL PRIMARY KEY,
    ten_thuong_hieu VARCHAR(255)
);

-- Bảng loại sản phẩm
CREATE TABLE loai_san_pham (
    id BIGSERIAL PRIMARY KEY,
    ten_loai VARCHAR(255),
    ngay_tao DATE DEFAULT CURRENT_DATE
);

-- Bảng sản phẩm
CREATE TABLE san_pham (
    id BIGSERIAL PRIMARY KEY,
    ten_san_pham VARCHAR(255),
    thuong_hieu_id BIGINT REFERENCES thuong_hieu(id),
    loai_id BIGINT REFERENCES loai_san_pham(id),
    gia DECIMAL(15,2),
    mo_ta_ngan TEXT,
    ngay_cap_phat DATE DEFAULT CURRENT_DATE,
    so_luong_ton INTEGER DEFAULT 0,
    ngay_tao DATE DEFAULT CURRENT_DATE,
    ngay_cap_nhat DATE
);

-- =====================================================
-- 3. BẢNG GIỎ HÀNG
-- =====================================================

-- Bảng giỏ hàng
CREATE TABLE gio_hang (
    id BIGSERIAL PRIMARY KEY,
    khach_hang_id BIGINT REFERENCES khach_hang(id)
);

-- =====================================================
-- 4. BẢNG PHƯƠNG THỨC THANH TOÁN
-- =====================================================

-- Bảng phương thức thanh toán
CREATE TABLE phuong_thuc_tt (
    id BIGSERIAL PRIMARY KEY,
    ten_phuong_thuc VARCHAR(255),
    mo_ta VARCHAR(255)
);

-- =====================================================
-- 5. BẢNG ĐỚN HÀNG
-- =====================================================

-- Bảng đơn hàng
CREATE TABLE don_hang (
    id BIGSERIAL PRIMARY KEY,
    ngay_dat_hang TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    khach_hang_id BIGINT REFERENCES khach_hang(id),
    trang_thai VARCHAR(50) DEFAULT 'MOI' CHECK (trang_thai IN ('MOI', 'DANG_XU_LY', 'DA_THANH_TOAN', 'DANG_GIAO', 'HOAN_TAT', 'DA_HUY', 'TRA_HANG')),
    pttt_id BIGINT REFERENCES phuong_thuc_tt(id)
);

-- Bảng chi tiết đơn hàng
CREATE TABLE chi_tiet_don_hang (
    don_hang_id BIGINT REFERENCES don_hang(id),
    san_pham_id BIGINT REFERENCES san_pham(id),
    so_luong INTEGER,
    don_gia DECIMAL(15,2),
    PRIMARY KEY (don_hang_id, san_pham_id)
);

-- =====================================================
-- 6. BẢNG PHIẾU THANH TOÁN
-- =====================================================

-- Bảng phiếu thanh toán
CREATE TABLE phieu_thanh_toan (
    id BIGSERIAL PRIMARY KEY,
    thoi_gian_thanh_toan TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    don_hang_id BIGINT REFERENCES don_hang(id),
    pttt_id BIGINT REFERENCES phuong_thuc_tt(id),
    trang_thai VARCHAR(255)
);

-- =====================================================
-- 7. BẢNG PHIẾU GIẢM GIÁ
-- =====================================================

-- Bảng phiếu giảm giá
CREATE TABLE phieu_giam_gia (
    id BIGSERIAL PRIMARY KEY,
    ma VARCHAR(64) UNIQUE,
    kieu VARCHAR(16) CHECK (kieu IN ('PHAN_TRAM', 'TIEN_MAT')),
    gia_tri DECIMAL(15,2),
    giam_toi_da DECIMAL(15,2),
    don_toi_thieu DECIMAL(15,2),
    mo_ta TEXT,
    ngay_bat_dau TIMESTAMP,
    ngay_ket_thuc TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    ap_dung_toan_bo BOOLEAN DEFAULT FALSE
);

-- Bảng liên kết phiếu giảm giá với loại sản phẩm
CREATE TABLE phieu_giam_gia_loai (
    phieu_id BIGINT REFERENCES phieu_giam_gia(id),
    loai_id BIGINT REFERENCES loai_san_pham(id),
    PRIMARY KEY (phieu_id, loai_id)
);

-- Bảng liên kết phiếu giảm giá với sản phẩm cụ thể
CREATE TABLE phieu_giam_gia_san_pham (
    phieu_id BIGINT REFERENCES phieu_giam_gia(id),
    san_pham_id BIGINT REFERENCES san_pham(id),
    PRIMARY KEY (phieu_id, san_pham_id)
);

-- =====================================================
-- 8. BẢNG QUẢN LÝ KHO
-- =====================================================

-- Bảng phiếu nhập
CREATE TABLE phieu_nhap (
    id BIGSERIAL PRIMARY KEY,
    ngay_nhap DATE DEFAULT CURRENT_DATE
);

-- Bảng chi tiết phiếu nhập
CREATE TABLE chi_tiet_phieu_nhap (
    phieu_nhap_id BIGINT REFERENCES phieu_nhap(id),
    san_pham_id BIGINT REFERENCES san_pham(id),
    so_luong INTEGER,
    don_gia DECIMAL(15,2),
    PRIMARY KEY (phieu_nhap_id, san_pham_id)
);

-- Bảng phiếu xuất
CREATE TABLE phieu_xuat (
    id BIGSERIAL PRIMARY KEY,
    ngay_xuat DATE DEFAULT CURRENT_DATE
);

-- Bảng chi tiết phiếu xuất
CREATE TABLE chi_tiet_phieu_xuat (
    phieu_xuat_id BIGINT REFERENCES phieu_xuat(id),
    san_pham_id BIGINT REFERENCES san_pham(id),
    so_luong INTEGER,
    don_gia DECIMAL(15,2),
    PRIMARY KEY (phieu_xuat_id, san_pham_id)
);

-- =====================================================
-- 9. BẢNG TOKEN QUÊN MẬT KHẨU
-- =====================================================

-- Bảng token quên mật khẩu
CREATE TABLE token_forget_password (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES khach_hang(id),
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_datetime TIMESTAMP NOT NULL
);

-- =====================================================
-- 10. TẠO INDEX ĐỂ TỐI ƯU HIỆU SUẤT
-- =====================================================

-- Index cho email người dùng
CREATE INDEX idx_nguoi_dung_email ON nguoi_dung(email);

-- Index cho sản phẩm
CREATE INDEX idx_san_pham_ten ON san_pham(ten_san_pham);
CREATE INDEX idx_san_pham_loai ON san_pham(loai_id);
CREATE INDEX idx_san_pham_thuong_hieu ON san_pham(thuong_hieu_id);
CREATE INDEX idx_san_pham_gia ON san_pham(gia);

-- Index cho đơn hàng
CREATE INDEX idx_don_hang_khach_hang ON don_hang(khach_hang_id);
CREATE INDEX idx_don_hang_ngay ON don_hang(ngay_dat_hang);
CREATE INDEX idx_don_hang_trang_thai ON don_hang(trang_thai);

-- Index cho phiếu giảm giá
CREATE INDEX idx_phieu_giam_gia_ma ON phieu_giam_gia(ma);
CREATE INDEX idx_phieu_giam_gia_active ON phieu_giam_gia(active);
CREATE INDEX idx_phieu_giam_gia_ngay ON phieu_giam_gia(ngay_bat_dau, ngay_ket_thuc);

-- Index cho token
CREATE INDEX idx_token_user ON token_forget_password(user_id);
CREATE INDEX idx_token_expiry ON token_forget_password(expiry_datetime);

-- =====================================================
-- 11. THÊM DỮ LIỆU MẪU
-- =====================================================

-- Thêm phương thức thanh toán
INSERT INTO phuong_thuc_tt (ten_phuong_thuc, mo_ta) VALUES 
('Tiền mặt', 'Thanh toán bằng tiền mặt khi nhận hàng'),
('Chuyển khoản', 'Chuyển khoản ngân hàng'),
('VNPay', 'Thanh toán qua VNPay'),
('Thẻ tín dụng', 'Thanh toán bằng thẻ tín dụng'),
('MoMo', 'Thanh toán qua ví MoMo'),
('ZaloPay', 'Thanh toán qua ZaloPay');

-- Thêm thương hiệu
INSERT INTO thuong_hieu (ten_thuong_hieu) VALUES 
('Samsung'),
('Apple'),
('LG'),
('Sony'),
('Panasonic'),
('Xiaomi'),
('Oppo'),
('Vivo'),
('Huawei'),
('Dell'),
('HP'),
('Asus'),
('Acer'),
('MSI'),
('Lenovo'),
('TCL'),
('Sharp'),
('Toshiba'),
('Electrolux'),
('Whirlpool');

-- Thêm loại sản phẩm
INSERT INTO loai_san_pham (ten_loai) VALUES 
('Điện thoại'),
('Laptop'),
('Tivi'),
('Tủ lạnh'),
('Máy giặt'),
('Điều hòa'),
('Loa'),
('Tai nghe'),
('Máy tính bảng'),
('Đồng hồ thông minh'),
('Camera'),
('Máy ảnh'),
('Lò vi sóng'),
('Nồi cơm điện'),
('Máy xay sinh tố'),
('Quạt điện'),
('Bàn ủi'),
('Máy hút bụi');

-- Thêm địa chỉ
INSERT INTO dia_chi (so_nha, ten_duong, phuong_xa, quan_huyen, tinh_thanh) VALUES 
('123', 'Nguyễn Văn Cừ', 'Phường 1', 'Quận 5', 'TP.HCM'),
('456', 'Lê Văn Việt', 'Phường Tăng Nhơn Phú A', 'Quận 9', 'TP.HCM'),
('789', 'Võ Văn Ngân', 'Phường Linh Chiểu', 'Thủ Đức', 'TP.HCM'),
('321', 'Lý Thường Kiệt', 'Phường 14', 'Quận 10', 'TP.HCM'),
('654', 'Cách Mạng Tháng 8', 'Phường 5', 'Quận 3', 'TP.HCM'),
('987', 'Nguyễn Thị Minh Khai', 'Phường Đa Kao', 'Quận 1', 'TP.HCM'),
('147', 'Hoàng Văn Thụ', 'Phường 4', 'Quận Tân Bình', 'TP.HCM'),
('258', 'Phan Văn Trị', 'Phường 11', 'Quận Bình Thạnh', 'TP.HCM'),
('369', 'Quang Trung', 'Phường 8', 'Quận Gò Vấp', 'TP.HCM'),
('741', 'Lê Đức Thọ', 'Phường 16', 'Quận Gò Vấp', 'TP.HCM');

-- Thêm người dùng
INSERT INTO nguoi_dung (ten, email, sdt, dia_chi_id) VALUES 
('Nguyễn Văn An', 'admin@electromart.com', '0901234567', 1),
('Trần Thị Bình', 'binh.tran@gmail.com', '0912345678', 2),
('Lê Văn Cường', 'cuong.le@yahoo.com', '0923456789', 3),
('Phạm Thị Dung', 'dung.pham@hotmail.com', '0934567890', 4),
('Hoàng Văn Em', 'em.hoang@gmail.com', '0945678901', 5),
('Vũ Thị Phương', 'phuong.vu@outlook.com', '0956789012', 6),
('Đặng Văn Giang', 'giang.dang@gmail.com', '0967890123', 7),
('Bùi Thị Hoa', 'hoa.bui@yahoo.com', '0978901234', 8),
('Ngô Văn Inh', 'inh.ngo@gmail.com', '0989012345', 9),
('Lý Thị Kim', 'kim.ly@hotmail.com', '0990123456', 10);

-- Thêm admin
INSERT INTO admin (id, tai_khoan, mat_khau) VALUES 
(1, 'admin', 'admin123');

-- Thêm khách hàng
INSERT INTO khach_hang (id, hang_thanh_vien, mat_khau) VALUES 
(2, 'BAC', 'password123'),
(3, 'VANG', 'password123'),
(4, 'BAC', 'password123'),
(5, 'KIMCUONG', 'password123'),
(6, 'VANG', 'password123'),
(7, 'BAC', 'password123'),
(8, 'BACHKIM', 'password123'),
(9, 'VANG', 'password123'),
(10, 'BAC', 'password123');

-- Thêm nhân viên
INSERT INTO nhan_vien (id, chuc_vu) VALUES 
(1, 'Quản lý hệ thống');

-- Thêm sản phẩm điện thoại
INSERT INTO san_pham (ten_san_pham, thuong_hieu_id, loai_id, gia, mo_ta_ngan, so_luong_ton) VALUES 
('iPhone 15 Pro Max 256GB', 2, 1, 34990000, 'iPhone 15 Pro Max với chip A17 Pro, camera 48MP, màn hình 6.7 inch Super Retina XDR', 50),
('Samsung Galaxy S24 Ultra 512GB', 1, 1, 32990000, 'Galaxy S24 Ultra với bút S Pen, camera 200MP, màn hình 6.8 inch Dynamic AMOLED 2X', 45),
('Xiaomi 14 Ultra 512GB', 6, 1, 24990000, 'Xiaomi 14 Ultra với camera Leica, chip Snapdragon 8 Gen 3, màn hình 6.73 inch', 30),
('Oppo Find X7 Ultra 256GB', 7, 1, 22990000, 'Oppo Find X7 Ultra với camera Hasselblad, chip Snapdragon 8 Gen 3', 25),
('Vivo X100 Pro 256GB', 8, 1, 19990000, 'Vivo X100 Pro với camera Zeiss, chip MediaTek Dimensity 9300', 35),
('iPhone 14 128GB', 2, 1, 19990000, 'iPhone 14 với chip A15 Bionic, camera kép 12MP, màn hình 6.1 inch', 60),
('Samsung Galaxy A55 128GB', 1, 1, 8990000, 'Galaxy A55 với camera 50MP, chip Exynos 1480, màn hình 6.6 inch Super AMOLED', 80),
('Xiaomi Redmi Note 13 Pro 256GB', 6, 1, 6990000, 'Redmi Note 13 Pro với camera 200MP, chip Snapdragon 7s Gen 2', 100);

-- Thêm sản phẩm laptop
INSERT INTO san_pham (ten_san_pham, thuong_hieu_id, loai_id, gia, mo_ta_ngan, so_luong_ton) VALUES 
('MacBook Pro 16 inch M3 Pro 512GB', 2, 2, 64990000, 'MacBook Pro 16 inch với chip M3 Pro, RAM 18GB, SSD 512GB, màn hình Liquid Retina XDR', 20),
('Dell XPS 13 Plus i7 1TB', 10, 2, 45990000, 'Dell XPS 13 Plus với Intel Core i7-1360P, RAM 32GB, SSD 1TB, màn hình 13.4 inch 4K', 15),
('HP Spectre x360 14 i7 512GB', 11, 2, 38990000, 'HP Spectre x360 14 với Intel Core i7-1355U, RAM 16GB, SSD 512GB, màn hình cảm ứng', 18),
('Asus ZenBook Pro 15 i9 1TB', 12, 2, 52990000, 'Asus ZenBook Pro 15 với Intel Core i9-13900H, RTX 4060, RAM 32GB, SSD 1TB', 12),
('Acer Swift 3 Ryzen 7 512GB', 13, 2, 18990000, 'Acer Swift 3 với AMD Ryzen 7 7730U, RAM 16GB, SSD 512GB, màn hình 14 inch Full HD', 25),
('MSI Gaming Laptop GF63 i5 512GB', 14, 2, 16990000, 'MSI GF63 với Intel Core i5-12450H, GTX 1650, RAM 8GB, SSD 512GB', 30),
('Lenovo ThinkPad X1 Carbon i7 1TB', 15, 2, 48990000, 'ThinkPad X1 Carbon với Intel Core i7-1365U, RAM 32GB, SSD 1TB, màn hình 14 inch 2.8K', 10);

-- Thêm sản phẩm tivi
INSERT INTO san_pham (ten_san_pham, thuong_hieu_id, loai_id, gia, mo_ta_ngan, so_luong_ton) VALUES 
('Samsung Neo QLED 4K 65 inch QN65QN90C', 1, 3, 42990000, 'Neo QLED 4K với Quantum Matrix Technology, Object Tracking Sound+', 15),
('LG OLED 4K 55 inch OLED55C3PSA', 3, 3, 32990000, 'LG OLED C3 với α9 Gen6 AI Processor, Dolby Vision IQ, webOS 23', 20),
('Sony Bravia XR 65 inch XR-65X90L', 4, 3, 38990000, 'Sony Bravia XR với Cognitive Processor XR, Full Array LED, Google TV', 12),
('TCL QLED 4K 55 inch 55C735', 16, 3, 14990000, 'TCL C735 với Quantum Dot Display, Dolby Vision, Android TV 11', 25),
('Sharp Aquos 4K 50 inch 4T-C50DL8X', 17, 3, 12990000, 'Sharp Aquos với HDR10+, Harman Kardon Audio, Android TV', 18);

-- Thêm sản phẩm tủ lạnh
INSERT INTO san_pham (ten_san_pham, thuong_hieu_id, loai_id, gia, mo_ta_ngan, so_luong_ton) VALUES 
('Samsung Inverter 360L RT35CG5424S9SV', 1, 4, 12990000, 'Tủ lạnh Samsung 2 cửa Inverter 360L, công nghệ Digital Inverter', 30),
('LG Inverter 393L GN-D422PS', 3, 4, 11990000, 'Tủ lạnh LG 2 cửa Inverter 393L, công nghệ DoorCooling+', 25),
('Panasonic Inverter 322L NR-BL351PKVN', 5, 4, 9990000, 'Tủ lạnh Panasonic 2 cửa Inverter 322L, công nghệ Panorama', 35),
('Electrolux Inverter 320L ETB3440K-H', 19, 4, 8990000, 'Tủ lạnh Electrolux 2 cửa Inverter 320L, công nghệ NutriFresh', 28);

-- Thêm sản phẩm máy giặt
INSERT INTO san_pham (ten_san_pham, thuong_hieu_id, loai_id, gia, mo_ta_ngan, so_luong_ton) VALUES 
('Samsung Inverter 9kg WW90TP44DSB/SV', 1, 5, 8990000, 'Máy giặt Samsung cửa trước Inverter 9kg, công nghệ EcoBubble', 22),
('LG Inverter 8.5kg FV1408S4W', 3, 5, 7990000, 'Máy giặt LG cửa trước Inverter 8.5kg, công nghệ 6 Motion DD', 20),
('Panasonic Inverter 8kg NA-V80FX2LVT', 5, 5, 6990000, 'Máy giặt Panasonic cửa trước Inverter 8kg, công nghệ ActiveFoam', 25),
('Electrolux Inverter 9kg EWF9024BDWB', 19, 5, 9490000, 'Máy giặt Electrolux cửa trước Inverter 9kg, công nghệ UltraMix', 18);

-- Thêm giỏ hàng cho khách hàng
INSERT INTO gio_hang (khach_hang_id) VALUES 
(2), (3), (4), (5), (6), (7), (8), (9), (10);

-- Thêm phiếu giảm giá
INSERT INTO phieu_giam_gia (ma, kieu, gia_tri, giam_toi_da, don_toi_thieu, mo_ta, ngay_bat_dau, ngay_ket_thuc, active, ap_dung_toan_bo) VALUES 
('WELCOME10', 'PHAN_TRAM', 10.00, 500000, 1000000, 'Giảm 10% cho khách hàng mới, tối đa 500k', '2024-01-01 00:00:00', '2024-12-31 23:59:59', true, true),
('SAVE50K', 'TIEN_MAT', 50000, 50000, 500000, 'Giảm 50k cho đơn hàng từ 500k', '2024-01-01 00:00:00', '2024-12-31 23:59:59', true, true),
('PHONE20', 'PHAN_TRAM', 20.00, 2000000, 5000000, 'Giảm 20% cho điện thoại, tối đa 2 triệu', '2024-03-01 00:00:00', '2024-03-31 23:59:59', true, false),
('LAPTOP15', 'PHAN_TRAM', 15.00, 3000000, 10000000, 'Giảm 15% cho laptop, tối đa 3 triệu', '2024-03-01 00:00:00', '2024-03-31 23:59:59', true, false),
('FREESHIP', 'TIEN_MAT', 30000, 30000, 200000, 'Miễn phí vận chuyển cho đơn từ 200k', '2024-01-01 00:00:00', '2024-12-31 23:59:59', true, true);

-- Liên kết phiếu giảm giá với loại sản phẩm
INSERT INTO phieu_giam_gia_loai (phieu_id, loai_id) VALUES 
(3, 1), -- PHONE20 áp dụng cho điện thoại
(4, 2); -- LAPTOP15 áp dụng cho laptop

-- Thêm đơn hàng mẫu
INSERT INTO don_hang (khach_hang_id, trang_thai, pttt_id, ngay_dat_hang) VALUES 
(2, 'HOAN_TAT', 1, '2024-02-15 10:30:00'),
(3, 'DANG_GIAO', 3, '2024-02-20 14:15:00'),
(4, 'DA_THANH_TOAN', 2, '2024-02-22 09:45:00'),
(5, 'DANG_XU_LY', 4, '2024-02-25 16:20:00'),
(6, 'MOI', 1, '2024-02-28 11:10:00');

-- Thêm chi tiết đơn hàng
INSERT INTO chi_tiet_don_hang (don_hang_id, san_pham_id, so_luong, don_gia) VALUES 
(1, 6, 1, 19990000), -- iPhone 14
(1, 15, 1, 8990000), -- Samsung A55
(2, 9, 1, 64990000), -- MacBook Pro M3
(2, 1, 1, 34990000), -- iPhone 15 Pro Max
(3, 2, 1, 32990000), -- Galaxy S24 Ultra
(3, 16, 1, 42990000), -- Samsung Neo QLED TV
(4, 10, 1, 45990000), -- Dell XPS 13
(4, 3, 1, 24990000), -- Xiaomi 14 Ultra
(5, 21, 1, 12990000), -- Samsung Tủ lạnh
(5, 25, 1, 8990000); -- Samsung Máy giặt

-- Thêm phiếu nhập kho
INSERT INTO phieu_nhap (ngay_nhap) VALUES 
('2024-01-15'),
('2024-02-01'),
('2024-02-15');

-- Thêm chi tiết phiếu nhập
INSERT INTO chi_tiet_phieu_nhap (phieu_nhap_id, san_pham_id, so_luong, don_gia) VALUES 
(1, 1, 50, 30000000), -- iPhone 15 Pro Max
(1, 2, 45, 28000000), -- Galaxy S24 Ultra
(1, 6, 60, 17000000), -- iPhone 14
(2, 9, 20, 55000000), -- MacBook Pro M3
(2, 10, 15, 40000000), -- Dell XPS 13
(2, 16, 15, 35000000), -- Samsung TV
(3, 21, 30, 10000000), -- Samsung Tủ lạnh
(3, 25, 22, 7000000); -- Samsung Máy giặt

-- Thêm token quên mật khẩu (mẫu)
INSERT INTO token_forget_password (user_id, is_used, token, expiry_datetime) VALUES 
(2, false, 'abc123def456ghi789', '2024-03-10 23:59:59'),
(3, true, 'xyz789uvw456rst123', '2024-02-28 23:59:59');

-- =====================================================
-- 12. COMMENT CHO CÁC BẢNG
-- =====================================================

COMMENT ON TABLE nguoi_dung IS 'Bảng người dùng cơ sở cho inheritance';
COMMENT ON TABLE khach_hang IS 'Bảng khách hàng kế thừa từ nguoi_dung';
COMMENT ON TABLE admin IS 'Bảng admin kế thừa từ nguoi_dung';
COMMENT ON TABLE san_pham IS 'Bảng sản phẩm chính của hệ thống';
COMMENT ON TABLE don_hang IS 'Bảng đơn hàng của khách hàng';
COMMENT ON TABLE phieu_giam_gia IS 'Bảng quản lý các phiếu giảm giá/khuyến mãi';

-- =====================================================
-- KẾT THÚC SCRIPT
-- =====================================================