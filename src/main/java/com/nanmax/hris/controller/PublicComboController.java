package com.nanmax.hris.controller;
import com.nanmax.hris.service.MasterDataService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/combo")
public class PublicComboController {
    private final MasterDataService masterDataService;
    public PublicComboController(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }
    @GetMapping("/jabatan")
    public List<Map<String, Object>> getJabatan() {
        try {
            return masterDataService.getAllJabatan().stream()
                    .map(j -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", j.getKdJabatan());
                        map.put("nama", j.getNamaJabatan());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Arrays.asList(
                Map.of("id", 1, "nama", "Manajer"),
                Map.of("id", 2, "nama", "Staf"), 
                Map.of("id", 3, "nama", "Supervisor")
            );
        }
    }
    @GetMapping("/departemen")
    public List<Map<String, Object>> getDepartemen() {
        try {
            return masterDataService.getAllDepartemen().stream()
                    .map(d -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", d.getKdDepartemen());
                        map.put("nama", d.getNamaDepartemen());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Arrays.asList(
                Map.of("id", 1, "nama", "Teknologi Informasi"),
                Map.of("id", 2, "nama", "Sumber Daya Manusia"),
                Map.of("id", 3, "nama", "Keuangan")
            );
        }
    }
    @GetMapping("/unitkerja")
    public List<Map<String, Object>> getUnitKerja() {
        try {
            return masterDataService.getAllUnitKerja().stream()
                    .map(u -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", u.getKdUnitKerja());
                        map.put("nama", u.getNamaUnitKerja());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Arrays.asList(
                Map.of("id", 1, "nama", "Pengembangan"),
                Map.of("id", 2, "nama", "Operasional"),
                Map.of("id", 3, "nama", "Dukungan")
            );
        }
    }
    @GetMapping("/pendidikan")
    public List<Map<String, Object>> getPendidikan() {
        try {
            return masterDataService.getAllPendidikan().stream()
                    .map(p -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", p.getKdPendidikan());
                        map.put("nama", p.getNamaPendidikan());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Arrays.asList(
                Map.of("id", 1, "nama", "Sarjana (S1)"),
                Map.of("id", 2, "nama", "Magister (S2)"),
                Map.of("id", 3, "nama", "Diploma III (D3)"),
                Map.of("id", 4, "nama", "SMA/SMK")
            );
        }
    }
    @GetMapping("/jeniskelamin")
    public List<Map<String, Object>> getJenisKelamin() {
        try {
            return masterDataService.getAllJenisKelamin().stream()
                    .map(jk -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", jk.getKdJenisKelamin());
                        map.put("nama", jk.getNamaJenisKelamin());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Arrays.asList(
                Map.of("id", 1, "nama", "Laki-laki"),
                Map.of("id", 2, "nama", "Perempuan")
            );
        }
    }
    @GetMapping("/{type}")
    public List<Map<String, Object>> getComboData(@PathVariable String type) {
        switch (type) {
            case "jabatan":
                return getJabatan();
            case "departemen":
                return getDepartemen();
            case "unitkerja":
                return getUnitKerja();
            case "pendidikan":
                return getPendidikan();
            case "jeniskelamin":
                return getJenisKelamin();
            default:
                return Arrays.asList(Map.of("id", 1, "nama", "Data " + type));
        }
    }
}