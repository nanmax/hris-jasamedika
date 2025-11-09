package com.nanmax.hris.service;
import com.nanmax.hris.entity.Pegawai;
import com.nanmax.hris.entity.Presensi;
import com.nanmax.hris.entity.StatusAbsen;
import com.nanmax.hris.repository.PresensiRepository;
import com.nanmax.hris.repository.StatusAbsenRepository;
import com.nanmax.hris.web.ApiBusinessException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Service
public class PresensiService {
    private final PresensiRepository presensiRepo;
    private final StatusAbsenRepository statusAbsenRepo;
    private final PegawaiService pegawaiService;
    public PresensiService(PresensiRepository presensiRepo, 
                         StatusAbsenRepository statusAbsenRepo,
                         PegawaiService pegawaiService) {
        this.presensiRepo = presensiRepo;
        this.statusAbsenRepo = statusAbsenRepo;
        this.pegawaiService = pegawaiService;
    }
    public Presensi savePresensi(Presensi p) { 
        return presensiRepo.save(p); 
    }
    public List<Presensi> getAllPresensi() { 
        return presensiRepo.findAll(); 
    }
    public List<Presensi> getPresensiByDateRange(LocalDate startDate, LocalDate endDate) {
        return presensiRepo.findByTglAbsensiBetween(startDate, endDate);
    }
    public List<Presensi> getPresensiByUserAndDateRange(String idUser, LocalDate startDate, LocalDate endDate) {
        Pegawai pegawai = pegawaiService.findByIdUser(idUser);
        if (pegawai == null) {
            throw new ApiBusinessException("Pegawai tidak ditemukan!");
        }
        return presensiRepo.findByIdUserAndTglAbsensiBetween(pegawai.getId(), startDate, endDate);
    }
    public String checkIn(String idUser) {
        Pegawai pegawai = pegawaiService.findByIdUser(idUser);
        if (pegawai == null) {
            throw new ApiBusinessException("Pegawai tidak ditemukan!");
        }
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        List<Presensi> todayPresensi = presensiRepo.findByIdUserAndTglAbsensi(pegawai.getId(), today);
        if (!todayPresensi.isEmpty()) {
            Presensi existing = todayPresensi.get(0);
            if (existing.getJamMasuk() != null) {
                throw new ApiBusinessException("Anda sudah melakukan check-in hari ini!");
            }
            existing.setJamMasuk(currentTime);
            presensiRepo.save(existing);
        } else {
            Presensi presensi = Presensi.builder()
                    .idUser(pegawai.getId())
                    .tglAbsensi(today)
                    .jamMasuk(currentTime)
                    .build();
            presensiRepo.save(presensi);
        }
        return currentTime.toString();
    }
    public String checkOut(String idUser) {
        Pegawai pegawai = pegawaiService.findByIdUser(idUser);
        if (pegawai == null) {
            throw new ApiBusinessException("Pegawai tidak ditemukan!");
        }
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        List<Presensi> todayPresensi = presensiRepo.findByIdUserAndTglAbsensi(pegawai.getId(), today);
        if (todayPresensi.isEmpty()) {
            throw new ApiBusinessException("Anda belum melakukan check-in hari ini!");
        }
        Presensi presensi = todayPresensi.get(0);
        if (presensi.getJamKeluar() != null) {
            throw new ApiBusinessException("Anda sudah melakukan check-out hari ini!");
        }
        presensi.setJamKeluar(currentTime);
        presensiRepo.save(presensi);
        return currentTime.toString();
    }
    public void createAbsensi(String idUser, LocalDate tanggal, Integer kdStatus) {
        Pegawai pegawai = pegawaiService.findByIdUser(idUser);
        if (pegawai == null) {
            throw new ApiBusinessException("Pegawai tidak ditemukan!");
        }
        StatusAbsen statusAbsen = statusAbsenRepo.findById(kdStatus)
                .orElseThrow(() -> new ApiBusinessException("Status absen tidak valid!"));
        List<Presensi> existing = presensiRepo.findByIdUserAndTglAbsensi(pegawai.getId(), tanggal);
        if (!existing.isEmpty()) {
            throw new ApiBusinessException("Data presensi untuk tanggal ini sudah ada!");
        }
        Presensi presensi = Presensi.builder()
                .idUser(pegawai.getId())
                .tglAbsensi(tanggal)
                .statusAbsen(statusAbsen)
                .build();
        presensiRepo.save(presensi);
    }
    public String getNamaLengkapByUserId(Long userId) {
        return pegawaiService.getAllPegawai().stream()
                .filter(p -> p.getId().equals(userId))
                .map(Pegawai::getNamaLengkap)
                .findFirst()
                .orElse("Unknown");
    }
}