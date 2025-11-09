package com.nanmax.hris.controller;
import com.nanmax.hris.dto.LoginRequest;
import com.nanmax.hris.entity.Pegawai;
import com.nanmax.hris.security.AuthenticationHolder;
import com.nanmax.hris.service.JwtService;
import com.nanmax.hris.service.PegawaiService;
import com.nanmax.hris.web.ApiBusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final PegawaiService pegawaiService;
    private final JwtService jwtService;
    private final AuthenticationHolder authHolder;
    public AuthController(PegawaiService pegawaiService, JwtService jwtService, AuthenticationHolder authHolder) {
        this.pegawaiService = pegawaiService;
        this.jwtService = jwtService;
        this.authHolder = authHolder;
    }
    @PostMapping("/init-data")
    public Map<String, Object> initData(@RequestParam String namaAdmin, @RequestParam String perusahaan) {
        List<Pegawai> admins = pegawaiService.findByProfile("ADMIN");
        if (!admins.isEmpty()) {
            throw new ApiBusinessException("Data admin sudah ada, tidak bisa membuat ulang!");
        }
        String email = "admin@" + perusahaan.toLowerCase().replaceAll("[^a-z0-9]", "") + ".local";
        String password = generatePassword();
        Pegawai admin = Pegawai.builder()
                .idUser(UUID.randomUUID().toString())
                .profile("ADMIN")
                .namaLengkap(namaAdmin)
                .email(email)
                .passwordHash(password) // In real app, this should be hashed
                .build();
        pegawaiService.createPegawai(admin);
        return Map.of(
                "email", email,
                "password", password,
                "profile", "ADMIN"
        );
    }
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam String email, 
                                   @RequestParam String password, 
                                   @RequestParam String profile) {
        Pegawai pegawai = pegawaiService.findByEmail(email);
        if (pegawai == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Email tidak ditemukan!"));
        }
        if (!pegawai.getPasswordHash().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Password salah!"));
        }
        String token = jwtService.generateToken(pegawai);
        Map<String, Object> info = new HashMap<>();
        info.put("profile", pegawai.getProfile());
        info.put("idUser", pegawai.getIdUser());
        info.put("namaLengkap", pegawai.getNamaLengkap());
        info.put("tempatLahir", pegawai.getTempatLahir() != null ? pegawai.getTempatLahir() : "");
        info.put("tanggalLahir", pegawai.getTanggalLahir() != null ? pegawai.getTanggalLahir() : 0);
        info.put("email", pegawai.getEmail());
        info.put("password", pegawai.getPasswordHash());
        info.put("nikUser", pegawai.getNikUser() != null ? pegawai.getNikUser() : "");
        info.put("kdJabatan", pegawai.getKdJabatan() != null ? pegawai.getKdJabatan() : 0);
        info.put("namaJabatan", pegawai.getNamaJabatan() != null ? pegawai.getNamaJabatan() : "");
        info.put("kdDepartemen", pegawai.getKdDepartemen() != null ? pegawai.getKdDepartemen() : 0);
        info.put("namaDepartemen", pegawai.getNamaDepartemen() != null ? pegawai.getNamaDepartemen() : "");
        info.put("kdUnitKerja", pegawai.getKdUnitKerja() != null ? pegawai.getKdUnitKerja() : 0);
        info.put("namaUnitKerja", pegawai.getNamaUnitKerja() != null ? pegawai.getNamaUnitKerja() : "");
        info.put("kdJenisKelamin", pegawai.getKdJenisKelamin() != null ? pegawai.getKdJenisKelamin() : 0);
        info.put("namaJenisKelamin", pegawai.getNamaJenisKelamin() != null ? pegawai.getNamaJenisKelamin() : "");
        info.put("kdPendidikan", pegawai.getKdPendidikan() != null ? pegawai.getKdPendidikan() : 0);
        info.put("namaPendidikan", pegawai.getNamaPendidikan() != null ? pegawai.getNamaPendidikan() : "");
        info.put("photo", pegawai.getPhoto() != null ? pegawai.getPhoto() : "");
        Map<String, Object> hasil = new HashMap<>();
        hasil.put("token", token);
        hasil.put("info", info);
        Map<String, Object> response = new HashMap<>();
        response.put("hasil", hasil);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/ubah-password-sendiri")
    public ResponseEntity<Object> ubahPasswordSendiri(@RequestParam String passwordAsli,
                                                   @RequestParam String passwordBaru1,
                                                   @RequestParam String passwordBaru2) {
        if (!authHolder.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User belum login!"));
        }
        if (!passwordBaru1.equals(passwordBaru2)) {
            return ResponseEntity.status(400).body(Map.of("error", "Password baru tidak sama!"));
        }
        if (passwordBaru1.length() < 6) {
            return ResponseEntity.status(400).body(Map.of("error", "Password baru minimal 6 karakter!"));
        }
        String currentUserId = authHolder.getCurrentUser().getIdUser();
        Pegawai pegawai = pegawaiService.findByIdUser(currentUserId);
        if (pegawai == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User tidak ditemukan!"));
        }
        if (!pegawai.getPasswordHash().equals(passwordAsli)) {
            return ResponseEntity.status(400).body(Map.of("error", "Password lama salah!"));
        }
        pegawai.setPasswordHash(passwordBaru1);
        pegawaiService.updatePegawai(pegawai);
        return ResponseEntity.ok(Map.of("success", "Password berhasil diubah"));
    }
    @PostMapping(value = "/login-json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Object> loginJson(@RequestBody LoginRequest loginRequest) {
        try {
            Pegawai pegawai = pegawaiService.findByEmail(loginRequest.getEmail());
            if (pegawai == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Email tidak ditemukan!"));
            }
            if (!pegawai.getPasswordHash().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Password salah!"));
            }
            String token = jwtService.generateToken(pegawai);
            Map<String, Object> info = new HashMap<>();
            info.put("profile", pegawai.getProfile() != null ? pegawai.getProfile() : "USER");
            info.put("idUser", pegawai.getIdUser() != null ? pegawai.getIdUser() : "");
            info.put("namaLengkap", pegawai.getNamaLengkap() != null ? pegawai.getNamaLengkap() : "");
            info.put("tempatLahir", pegawai.getTempatLahir() != null ? pegawai.getTempatLahir() : "");
            info.put("tanggalLahir", pegawai.getTanggalLahir() != null ? pegawai.getTanggalLahir() : 0);
            info.put("email", pegawai.getEmail() != null ? pegawai.getEmail() : "");
            info.put("nikUser", pegawai.getNikUser() != null ? pegawai.getNikUser() : "");
            info.put("kdJabatan", pegawai.getJabatan() != null ? pegawai.getJabatan().getKdJabatan() : 0);
            info.put("namaJabatan", pegawai.getJabatan() != null ? pegawai.getJabatan().getNamaJabatan() : "");
            info.put("kdDepartemen", pegawai.getDepartemen() != null ? pegawai.getDepartemen().getKdDepartemen() : 0);
            info.put("namaDepartemen", pegawai.getDepartemen() != null ? pegawai.getDepartemen().getNamaDepartemen() : "");
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", info
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody com.nanmax.hris.dto.RegisterRequest registerRequest) {
        Pegawai existingPegawai = pegawaiService.findByEmail(registerRequest.getEmail());
        if (existingPegawai != null) {
            return ResponseEntity.status(400).body(Map.of("error", "Email sudah terdaftar!"));
        }
        if (registerRequest.getNamaLengkap() == null || registerRequest.getNamaLengkap().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Nama lengkap harus diisi!"));
        }
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Email harus diisi!"));
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Password harus diisi!"));
        }
        Pegawai newPegawai = Pegawai.builder()
                .idUser(UUID.randomUUID().toString())
                .profile(registerRequest.getProfile() != null ? registerRequest.getProfile() : "KARYAWAN")
                .namaLengkap(registerRequest.getNamaLengkap().trim())
                .email(registerRequest.getEmail().trim())
                .passwordHash(registerRequest.getPassword()) // In real app, this should be hashed
                .nikUser(registerRequest.getNikUser())
                .tempatLahir(registerRequest.getTempatLahir())
                .tanggalLahir(registerRequest.getTanggalLahir())
                .build();
        pegawaiService.createPegawai(newPegawai);
        String token = jwtService.generateToken(newPegawai);
        Map<String, Object> info = new HashMap<>();
        info.put("profile", newPegawai.getProfile());
        info.put("idUser", newPegawai.getIdUser());
        info.put("namaLengkap", newPegawai.getNamaLengkap());
        info.put("email", newPegawai.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Registrasi berhasil!",
                "token", token,
                "user", info
        ));
    }
    private String generatePassword() {
        return "pass" + System.currentTimeMillis() % 10000;
    }
    @PostMapping("/master/{type}")
    public ResponseEntity<Map<String, Object>> createMasterData(@PathVariable String type, @RequestBody Map<String, Object> request) {
        String nama = (String) request.get("nama");
        if (nama == null || nama.trim().isEmpty()) {
            throw new ApiBusinessException("Nama harus diisi");
        }
        Map<String, Object> response = Map.of(
                "message", "Data " + type + " berhasil dibuat",
                "id", System.currentTimeMillis() % 1000,
                "nama", nama
        );
        return ResponseEntity.ok(response);
    }
    @PutMapping("/master/{type}/{id}")
    public ResponseEntity<Map<String, Object>> updateMasterData(@PathVariable String type, @PathVariable Integer id, @RequestBody Map<String, Object> request) {
        String nama = (String) request.get("nama");
        if (nama == null || nama.trim().isEmpty()) {
            throw new ApiBusinessException("Nama harus diisi");
        }
        Map<String, Object> response = Map.of(
                "message", "Data " + type + " berhasil diupdate",
                "id", id,
                "nama", nama
        );
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/master/{type}/{id}")
    public ResponseEntity<Map<String, Object>> deleteMasterData(@PathVariable String type, @PathVariable Integer id) {
        Map<String, Object> response = Map.of(
                "message", "Data " + type + " berhasil dihapus",
                "id", id
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping("/combo/{type}")
    public ResponseEntity<List<Map<String, Object>>> getComboData(@PathVariable String type) {
        List<Map<String, Object>> data = new ArrayList<>();
        switch (type) {
            case "jabatan":
                data.add(Map.of("id", 1, "nama", "Manager"));
                data.add(Map.of("id", 2, "nama", "Staff"));
                data.add(Map.of("id", 3, "nama", "Supervisor"));
                break;
            case "departemen":
                data.add(Map.of("id", 1, "nama", "IT"));
                data.add(Map.of("id", 2, "nama", "HR"));
                data.add(Map.of("id", 3, "nama", "Finance"));
                break;
            case "unitkerja":
                data.add(Map.of("id", 1, "nama", "Development"));
                data.add(Map.of("id", 2, "nama", "Operations"));
                data.add(Map.of("id", 3, "nama", "Support"));
                break;
            case "jeniskelamin":
                data.add(Map.of("id", 1, "nama", "Laki-laki"));
                data.add(Map.of("id", 2, "nama", "Perempuan"));
                break;
            case "pendidikan":
                data.add(Map.of("id", 1, "nama", "S1"));
                data.add(Map.of("id", 2, "nama", "S2"));
                data.add(Map.of("id", 3, "nama", "D3"));
                data.add(Map.of("id", 4, "nama", "SMA"));
                break;
            default:
                data.add(Map.of("id", 1, "nama", "Data " + type));
        }
        return ResponseEntity.ok(data);
    }
}