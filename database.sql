-- ========================================
-- Database Setup for Bea Cukai Nadella
-- ========================================

-- Create database
CREATE DATABASE IF NOT EXISTS beacukai_nadella
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE beacukai_nadella;

-- ========================================
-- Table: users
-- ========================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('Admin', 'Petugas') NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Table: logistik
-- ========================================
CREATE TABLE IF NOT EXISTS logistik (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kode_logistik VARCHAR(50) NOT NULL UNIQUE,
    nama_barang VARCHAR(200) NOT NULL,
    jenis_kegiatan ENUM('IMPOR', 'EKSPOR') NOT NULL,
    negara VARCHAR(100) NOT NULL,
    pelabuhan_bandara VARCHAR(100) NOT NULL,
    jalur_pemeriksaan ENUM('HIJAU', 'KUNING', 'MERAH') NOT NULL,
    status_kepabeanan VARCHAR(100) NOT NULL,
    nomor_dokumen VARCHAR(12) NOT NULL,
    email_petugas VARCHAR(100) NOT NULL,
    tanggal_pendaftaran DATE NOT NULL,
    created_by VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_kode_logistik (kode_logistik),
    INDEX idx_jenis_kegiatan (jenis_kegiatan),
    INDEX idx_jalur_pemeriksaan (jalur_pemeriksaan)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Sample Data (Optional)
-- ========================================

-- Insert default users (passwords are BCrypt hashed)
-- admin:admin123 -> $2a$10$... (will be created by DataInitializer)
-- petugas:petugas123 -> $2a$10$... (will be created by DataInitializer)

-- Sample logistik data
INSERT INTO logistik (
    kode_logistik, nama_barang, jenis_kegiatan, negara, 
    pelabuhan_bandara, jalur_pemeriksaan, status_kepabeanan, 
    nomor_dokumen, email_petugas, tanggal_pendaftaran, created_by
) VALUES
('NDL-BC-2045-2025', 'Elektronik Konsumer', 'IMPOR', 'China', 
 'Tanjung Priok', 'HIJAU', 'Selesai Clearance', '123456789012', 
 'ahmad@nadella.beacukai.go.id', '2025-02-01', 'admin'),

('NDL-BC-2046-2025', 'Tekstil dan Pakaian', 'EKSPOR', 'Amerika Serikat', 
 'Soekarno-Hatta', 'KUNING', 'Dalam Pemeriksaan', '234567890123', 
 'budi@nadella.beacukai.go.id', '2025-02-05', 'admin'),

('NDL-BC-2047-2025', 'Mesin Industri', 'IMPOR', 'Jerman', 
 'Tanjung Perak', 'MERAH', 'Menunggu Dokumen', '345678901234', 
 'siti@nadella.beacukai.go.id', '2025-02-08', 'petugas'),

('NDL-BC-2048-2025', 'Kopi Arabika', 'EKSPOR', 'Jepang', 
 'Soekarno-Hatta', 'HIJAU', 'Selesai Clearance', '456789012345', 
 'dewi@nadella.beacukai.go.id', '2025-02-10', 'petugas'),

('NDL-BC-2049-2025', 'Spare Part Otomotif', 'IMPOR', 'Thailand', 
 'Belawan', 'KUNING', 'Dalam Proses', '567890123456', 
 'eko@nadella.beacukai.go.id', '2025-02-12', 'admin');

-- ========================================
-- Indexes for better performance
-- ========================================
ALTER TABLE logistik ADD INDEX idx_tanggal (tanggal_pendaftaran);
ALTER TABLE logistik ADD INDEX idx_created_by (created_by);

-- ========================================
-- Views for statistics (Optional)
-- ========================================
CREATE OR REPLACE VIEW v_logistik_stats AS
SELECT 
    COUNT(*) as total_data,
    SUM(CASE WHEN jenis_kegiatan = 'IMPOR' THEN 1 ELSE 0 END) as total_impor,
    SUM(CASE WHEN jenis_kegiatan = 'EKSPOR' THEN 1 ELSE 0 END) as total_ekspor,
    SUM(CASE WHEN jalur_pemeriksaan = 'HIJAU' THEN 1 ELSE 0 END) as jalur_hijau,
    SUM(CASE WHEN jalur_pemeriksaan = 'KUNING' THEN 1 ELSE 0 END) as jalur_kuning,
    SUM(CASE WHEN jalur_pemeriksaan = 'MERAH' THEN 1 ELSE 0 END) as jalur_merah
FROM logistik;

-- ========================================
-- Query Examples
-- ========================================

-- Get all logistik data with pagination
-- SELECT * FROM logistik ORDER BY id DESC LIMIT 10 OFFSET 0;

-- Search by kode logistik
-- SELECT * FROM logistik WHERE kode_logistik LIKE '%NDL-BC-2045%';

-- Get statistics
-- SELECT * FROM v_logistik_stats;

-- Get data by jalur pemeriksaan
-- SELECT * FROM logistik WHERE jalur_pemeriksaan = 'HIJAU';

-- Get data by jenis kegiatan
-- SELECT * FROM logistik WHERE jenis_kegiatan = 'IMPOR';
