package com.nanmax.hris.controller;
import com.nanmax.hris.service.MasterDataService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/pegawai/combo")
public class ComboController {
    private final MasterDataService masterDataService;
    public ComboController(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }
    @GetMapping("/jabatan")
    public List<Map<String, Object>> getJabatan() {
        return masterDataService.getAllJabatan().stream()
                .map(j -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("kdJabatan", j.getKdJabatan());
                    map.put("namaJabatan", j.getNamaJabatan());
                    return map;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/departemen")
    public List<Map<String, Object>> getDepartemen() {
        return masterDataService.getAllDepartemen().stream()
                .map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("kdDepartemen", d.getKdDepartemen());
                    map.put("namaDepartemen", d.getNamaDepartemen());
                    return map;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/unit-kerja")
    public List<Map<String, Object>> getUnitKerja() {
        return masterDataService.getAllUnitKerja().stream()
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("kdUnitKerja", u.getKdUnitKerja());
                    map.put("namaUnitKerja", u.getNamaUnitKerja());
                    return map;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/pendidikan")
    public List<Map<String, Object>> getPendidikan() {
        return masterDataService.getAllPendidikan().stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("kdPendidikan", p.getKdPendidikan());
                    map.put("namaPendidikan", p.getNamaPendidikan()); // Fixed typo
                    return map;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/jenis-kelamin")
    public List<Map<String, Object>> getJenisKelamin() {
        return masterDataService.getAllJenisKelamin().stream()
                .map(jk -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("kdJenisKelamin", jk.getKdJenisKelamin());
                    map.put("namaJenisKelamin", jk.getNamaJenisKelamin());
                    return map;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/departemen-hrd")
    public List<Map<String, Object>> getDepartemenHRD() {
        return masterDataService.getPegawaiHRD().stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("namaLengkap", row[0]);
                    map.put("kdJabatan", row[1]);
                    map.put("namaJabatan", row[2]);
                    return map;
                })
                .collect(Collectors.toList());
    }
}