package com.nanmax.hris.repository;
import com.nanmax.hris.entity.Pegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface PegawaiRepository extends JpaRepository<Pegawai, Long> {
    Pegawai findByEmail(String email);
    Pegawai findByIdUser(String idUser);
    List<Pegawai> findByProfile(String profile);
    @Query("SELECT p FROM Pegawai p WHERE p.departemen.namaDepartemen = :departemenName")
    List<Pegawai> findByDepartemen_NamaDepartemen(String departemenName);
}