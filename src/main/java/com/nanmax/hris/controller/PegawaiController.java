package com.nanmax.hris.controller;
import com.nanmax.hris.entity.Pegawai;
import com.nanmax.hris.security.AuthenticationHolder;
import com.nanmax.hris.service.PegawaiService;
import com.nanmax.hris.util.EpochUtils;
import com.nanmax.hris.web.ApiBusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/pegawai")
public class PegawaiController {
    private final PegawaiService pegawaiService;
    private final AuthenticationHolder authHolder;
    public PegawaiController(PegawaiService pegawaiService, AuthenticationHolder authHolder) {
        this.pegawaiService = pegawaiService;
        this.authHolder = authHolder;
    }
    @GetMapping("/daftar")
    public ResponseEntity<Object> daftar() {
        if (!authHolder.isHRD()) {
            return ResponseEntity.status(403).body(Map.of("error", "Hanya admin atau HRD yang bisa mengakses data pegawai!"));
        }
        List<Map<String, Object>> result = pegawaiService.getAllPegawai().stream()
                .map(this::convertPegawaiToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    @PostMapping("/admin-tambah-pegawai")
    public ResponseEntity<Object> tambahPegawai(@RequestBody Map<String, Object> request) {
        if (!authHolder.isHRD()) {
            return ResponseEntity.status(403).body(Map.of("error", "Hanya admin atau HRD yang bisa menambah pegawai!"));
        }
        String password = (String) request.get("password");
        String passwordC = (String) request.get("passwordC");
        if (!password.equals(passwordC)) {
            return ResponseEntity.status(400).body(Map.of("error", "Konfirmasi password tidak cocok!"));
        }
        Long tanggalLahir = null;
        Object tanggalLahirObj = request.get("tanggalLahir");
        if (tanggalLahirObj != null) {
            if (tanggalLahirObj instanceof Number) {
                tanggalLahir = ((Number) tanggalLahirObj).longValue();
            }
        }
        Pegawai pegawai = Pegawai.builder()
                .idUser(UUID.randomUUID().toString())
                .namaLengkap((String) request.get("namaLengkap"))
                .email((String) request.get("email"))
                .tempatLahir((String) request.get("tempatLahir"))
                .tanggalLahir(tanggalLahir)
                .passwordHash(password)
                .profile("PEGAWAI")
                .build();
        pegawaiService.setPegawaiMasterData(pegawai, 
                (Integer) request.get("kdJabatan"),
                (Integer) request.get("kdDepartemen"),
                (Integer) request.get("kdUnitKerja"),
                (Integer) request.get("kdJenisKelamin"),
                (Integer) request.get("kdPendidikan"));
        pegawaiService.createPegawai(pegawai);
        return ResponseEntity.ok(Map.of("success", "Pegawai berhasil ditambahkan"));
    }
    @PostMapping("/admin-ubah-pegawai")
    public ResponseEntity<Object> ubahPegawai(@RequestBody Map<String, Object> request) {
        if (!authHolder.isHRD()) {
            return ResponseEntity.status(403).body(Map.of("error", "Hanya admin atau HRD yang bisa mengubah data pegawai!"));
        }
        String idUser = (String) request.get("idUser");
        Pegawai pegawai = pegawaiService.findByIdUser(idUser);
        if (pegawai == null) {
            throw new ApiBusinessException("Pegawai tidak ditemukan!");
        }
        String password = (String) request.get("password");
        String passwordC = (String) request.get("passwordC");
        if (!password.equals(passwordC)) {
            throw new ApiBusinessException("Konfirmasi password tidak cocok!");
        }
        pegawai.setNamaLengkap((String) request.get("namaLengkap"));
        pegawai.setEmail((String) request.get("email"));
        pegawai.setTempatLahir((String) request.get("tempatLahir"));
        pegawai.setTanggalLahir(((Integer) request.get("tanggalLahir")).longValue());
        pegawai.setPassword(password);
        pegawaiService.setPegawaiMasterData(pegawai, 
                (Integer) request.get("kdJabatan"),
                (Integer) request.get("kdDepartemen"),
                (Integer) request.get("kdUnitKerja"),
                (Integer) request.get("kdJenisKelamin"),
                (Integer) request.get("kdPendidikan"));
        pegawaiService.updatePegawai(pegawai);
        return ResponseEntity.ok(Map.of("success", "Data pegawai berhasil diubah"));
    }
    @PostMapping("/admin-ubah-photo")
    public Map<String, String> ubahPhotoAdmin(@RequestParam String idUser,
                                             @RequestParam String namaFile,
                                             @RequestParam("files") MultipartFile file) {
        if (!authHolder.isHRD()) {
            throw new ApiBusinessException("Hanya admin atau HRD yang bisa mengubah photo pegawai!");
        }
        Pegawai pegawai = pegawaiService.findByIdUser(idUser);
        if (pegawai == null) {
            throw new ApiBusinessException("Pegawai tidak ditemukan!");
        }
        String photoPath = pegawaiService.savePhoto(file, namaFile);
        pegawai.setPhoto(photoPath);
        pegawaiService.updatePegawai(pegawai);
        return Map.of("success", "Photo pegawai berhasil diubah");
    }
    @PostMapping("/ubah-photo")
    public Map<String, String> ubahPhotoSendiri(@RequestParam String namaFile,
                                               @RequestParam("files") MultipartFile file) {
        String currentUserId = authHolder.getCurrentUser().getIdUser();
        if (currentUserId == null) {
            throw new ApiBusinessException("User tidak terautentikasi!");
        }
        Pegawai pegawai = pegawaiService.findByIdUser(currentUserId);
        if (pegawai == null) {
            throw new ApiBusinessException("Data pegawai tidak ditemukan!");
        }
        String photoPath = pegawaiService.savePhoto(file, namaFile);
        pegawai.setPhoto(photoPath);
        pegawaiService.updatePegawai(pegawai);
        return Map.of("success", "Photo berhasil diubah");
    }
    private Map<String, Object> convertPegawaiToResponse(Pegawai p) {
        Map<String, Object> response = new HashMap<>();
        response.put("profile", p.getProfile());
        response.put("idUser", p.getIdUser());
        response.put("namaLengkap", p.getNamaLengkap());
        response.put("tempatLahir", p.getTempatLahir());
        response.put("tanggalLahir", p.getTanggalLahir());
        response.put("email", p.getEmail());
        response.put("password", p.getPassword());
        response.put("nikUser", p.getNikUser());
        response.put("kdJabatan", p.getKdJabatan());
        response.put("namaJabatan", p.getNamaJabatan());
        response.put("kdDepartemen", p.getKdDepartemen());
        response.put("namaDepartemen", p.getNamaDepartemen());
        response.put("kdUnitKerja", p.getKdUnitKerja());
        response.put("namaUnitKerja", p.getNamaUnitKerja());
        response.put("kdJenisKelamin", p.getKdJenisKelamin());
        response.put("namaJenisKelamin", p.getNamaJenisKelamin());
        response.put("kdPendidikan", p.getKdPendidikan());
        response.put("namaPendidikan", p.getNamaPendidikan());
        response.put("photo", p.getPhoto());
        return response;
    }
}