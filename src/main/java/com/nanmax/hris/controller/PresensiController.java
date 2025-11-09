package com.nanmax.hris.controller;
import com.nanmax.hris.entity.Presensi;
import com.nanmax.hris.security.AuthenticationHolder;
import com.nanmax.hris.service.MasterDataService;
import com.nanmax.hris.service.PresensiService;
import com.nanmax.hris.util.EpochUtils;
import com.nanmax.hris.web.ApiBusinessException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/presensi")
public class PresensiController {
    private final PresensiService presensiService;
    private final MasterDataService masterDataService;
    private final AuthenticationHolder authHolder;
    public PresensiController(PresensiService presensiService, 
                            MasterDataService masterDataService,
                            AuthenticationHolder authHolder) {
        this.presensiService = presensiService;
        this.masterDataService = masterDataService;
        this.authHolder = authHolder;
    }
    @GetMapping("/combo/status-absen")
    public List<Map<String, Object>> getStatusAbsen(@RequestParam Long tglAwal, 
                                                   @RequestParam Long tglAkhir) {
        return masterDataService.getAllStatusAbsen().stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("kdStatus", s.getKdStatus());
                    map.put("namaStatus", s.getNamaStatus());
                    return map;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/daftar/admin")
    public List<Map<String, Object>> daftarAdmin(@RequestParam Long tglAwal, 
                                                @RequestParam Long tglAkhir) {
        if (!authHolder.isHRD()) {
            throw new ApiBusinessException("Hanya admin atau HRD yang bisa mengakses data ini!");
        }
        LocalDate startDate = EpochUtils.epochToLocalDate(tglAwal);
        LocalDate endDate = EpochUtils.epochToLocalDate(tglAkhir);
        return presensiService.getPresensiByDateRange(startDate, endDate).stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idUser", p.getIdUser());
                    map.put("namaLengkap", presensiService.getNamaLengkapByUserId(p.getIdUser()));
                    map.put("tglAbsensi", EpochUtils.localDateToEpoch(p.getTglAbsensi()));
                    map.put("jamMasuk", p.getJamMasuk() != null ? p.getJamMasuk().toString() : "");
                    map.put("jamKeluar", p.getJamKeluar() != null ? p.getJamKeluar().toString() : "");
                    map.put("namaStatus", p.getNamaStatus());
                    return map;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/daftar/pegawai")
    public List<Map<String, Object>> daftarPegawai(@RequestParam Long tglAwal, 
                                                  @RequestParam Long tglAkhir) {
        String currentUserId = authHolder.getCurrentUser().getIdUser();
        LocalDate startDate = EpochUtils.epochToLocalDate(tglAwal);
        LocalDate endDate = EpochUtils.epochToLocalDate(tglAkhir);
        return presensiService.getPresensiByUserAndDateRange(currentUserId, startDate, endDate).stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("tglAbsensi", EpochUtils.localDateToEpoch(p.getTglAbsensi()));
                    map.put("jamMasuk", p.getJamMasuk() != null ? p.getJamMasuk().toString() : "");
                    map.put("jamKeluar", p.getJamKeluar() != null ? p.getJamKeluar().toString() : "");
                    map.put("namaStatus", p.getNamaStatus());
                    return map;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/in")
    public Map<String, String> checkIn() {
        String currentUserId = authHolder.getCurrentUser().getIdUser();
        String jamMasuk = presensiService.checkIn(currentUserId);
        return Map.of("jamMasuk", jamMasuk);
    }
    @GetMapping("/out")
    public Map<String, String> checkOut() {
        String currentUserId = authHolder.getCurrentUser().getIdUser();
        String jamKeluar = presensiService.checkOut(currentUserId);
        return Map.of("jamKeluar", jamKeluar);
    }
    @PostMapping("/abseni")
    public Map<String, String> absen(@RequestParam Long tglAbsensi, 
                                   @RequestParam Integer kdStatus) {
        String currentUserId = authHolder.getCurrentUser().getIdUser();
        LocalDate tanggal = EpochUtils.epochToLocalDate(tglAbsensi);
        presensiService.createAbsensi(currentUserId, tanggal, kdStatus);
        return Map.of("success", "Data absen/izin berhasil disimpan");
    }
}