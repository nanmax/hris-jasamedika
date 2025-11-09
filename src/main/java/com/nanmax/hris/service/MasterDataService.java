package com.nanmax.hris.service;
import com.nanmax.hris.entity.*;
import com.nanmax.hris.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class MasterDataService {
    private final JabatanRepository jabatanRepo;
    private final DepartemenRepository departemenRepo;
    private final UnitKerjaRepository unitKerjaRepo;
    private final PendidikanRepository pendidikanRepo;
    private final JenisKelaminRepository jenisKelaminRepo;
    private final StatusAbsenRepository statusAbsenRepo;
    private final PegawaiService pegawaiService;
    public MasterDataService(JabatanRepository jabatanRepo, 
                           DepartemenRepository departemenRepo,
                           UnitKerjaRepository unitKerjaRepo, 
                           PendidikanRepository pendidikanRepo,
                           JenisKelaminRepository jenisKelaminRepo, 
                           StatusAbsenRepository statusAbsenRepo,
                           PegawaiService pegawaiService) {
        this.jabatanRepo = jabatanRepo;
        this.departemenRepo = departemenRepo;
        this.unitKerjaRepo = unitKerjaRepo;
        this.pendidikanRepo = pendidikanRepo;
        this.jenisKelaminRepo = jenisKelaminRepo;
        this.statusAbsenRepo = statusAbsenRepo;
        this.pegawaiService = pegawaiService;
    }
    public List<Jabatan> getAllJabatan() {
        return jabatanRepo.findAll();
    }
    public List<Departemen> getAllDepartemen() {
        return departemenRepo.findAll();
    }
    public List<UnitKerja> getAllUnitKerja() {
        return unitKerjaRepo.findAll();
    }
    public List<Pendidikan> getAllPendidikan() {
        return pendidikanRepo.findAll();
    }
    public List<JenisKelamin> getAllJenisKelamin() {
        return jenisKelaminRepo.findAll();
    }
    public List<StatusAbsen> getAllStatusAbsen() {
        return statusAbsenRepo.findAll();
    }
    public List<Object[]> getPegawaiHRD() {
        List<Pegawai> pegawaiHRD = pegawaiService.findByDepartemenName("HRD");
        return pegawaiHRD.stream()
                .map(p -> new Object[]{p.getNamaLengkap(), p.getKdJabatan(), p.getNamaJabatan()})
                .collect(java.util.stream.Collectors.toList());
    }
}