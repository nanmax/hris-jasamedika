package com.nanmax.hris.repository;
import com.nanmax.hris.entity.Presensi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface PresensiRepository extends JpaRepository<Presensi, Long> {
    List<Presensi> findByIdUserAndTglAbsensiBetween(Long idUser, LocalDate startDate, LocalDate endDate);
    List<Presensi> findByTglAbsensiBetween(LocalDate startDate, LocalDate endDate);
    List<Presensi> findByIdUserAndTglAbsensi(Long idUser, LocalDate tglAbsensi);
}