-- Fix inheritance issue by adding DTYPE column
-- Run this SQL script to fix the inheritance mapping

-- Add DTYPE column to nguoi_dung table
ALTER TABLE nguoi_dung ADD COLUMN dtype VARCHAR(31);

-- Update existing records with appropriate discriminator values
UPDATE nguoi_dung SET dtype = 'Admin' 
WHERE id IN (SELECT id FROM admin);

UPDATE nguoi_dung SET dtype = 'KhachHang' 
WHERE id IN (SELECT id FROM khach_hang);

UPDATE nguoi_dung SET dtype = 'NhanVien' 
WHERE id IN (SELECT id FROM nhan_vien);

-- Set NOT NULL constraint after updating data
ALTER TABLE nguoi_dung ALTER COLUMN dtype SET NOT NULL;

-- Create index for better performance
CREATE INDEX idx_nguoi_dung_dtype ON nguoi_dung(dtype);