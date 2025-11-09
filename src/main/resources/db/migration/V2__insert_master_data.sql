-- Insert master data for testing

-- Insert Jabatan
INSERT INTO jabatan (nama_jabatan) VALUES 
('Manager'), 
('Supervisor'), 
('Staff'), 
('Intern'),
('Director');

-- Insert Departemen 
INSERT INTO departemen (nama_departemen) VALUES 
('HRD'), 
('IT'), 
('Finance'), 
('Marketing'), 
('Operations');

-- Insert Unit Kerja
INSERT INTO unit_kerja (nama_unit_kerja) VALUES 
('Head Office'), 
('Branch A'), 
('Branch B'), 
('Remote');

-- Insert Pendidikan
INSERT INTO pendidikan (nama_pendidikan) VALUES 
('SD'), 
('SMP'), 
('SMA'), 
('D3'), 
('S1'), 
('S2'), 
('S3');

-- Insert Jenis Kelamin
INSERT INTO jenis_kelamin (nama_jenis_kelamin) VALUES 
('Laki-laki'), 
('Perempuan');

-- Insert Status Absen
INSERT INTO status_absen (nama_status) VALUES 
('Hadir'), 
('Sakit'), 
('Izin'), 
('Cuti'), 
('Alpha');

-- Insert default admin user
INSERT INTO pegawai (id_user, profile, nama_lengkap, email, password_hash, kd_jabatan, kd_departemen, kd_unit_kerja, kd_jenis_kelamin, kd_pendidikan) 
VALUES (
    'admin-001', 
    'ADMIN', 
    'Administrator Jasamedika', 
    'admin@jasamedika.com', 
    'admin123', 
    1, 
    1, 
    1, 
    1, 
    5
);