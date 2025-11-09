package com.nanmax.hris.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Table(name = "presensi")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Presensi {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idUser; // reference to Pegawai.id
    private LocalDate tglAbsensi;
    private LocalTime jamMasuk;
    private LocalTime jamKeluar;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kd_status")
    private StatusAbsen statusAbsen;
    private Instant createdAt;
    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
    public Integer getKdStatus() {
        return statusAbsen != null ? statusAbsen.getKdStatus() : null;
    }
    public String getNamaStatus() {
        return statusAbsen != null ? statusAbsen.getNamaStatus() : null;
    }
}