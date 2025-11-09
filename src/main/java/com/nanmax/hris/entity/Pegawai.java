package com.nanmax.hris.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity
@Table(name = "pegawai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pegawai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String idUser;
    private String profile;
    private String namaLengkap;
    private String tempatLahir;
    @Column(unique = true)
    private String email;
    private String passwordHash;
    private String nikUser;
    private Long tanggalLahir; // epoch seconds
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kd_jabatan")
    private Jabatan jabatan;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kd_departemen")
    private Departemen departemen;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kd_unit_kerja")
    private UnitKerja unitKerja;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kd_jenis_kelamin")
    private JenisKelamin jenisKelamin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kd_pendidikan")
    private Pendidikan pendidikan;
    private String photo;
    private Instant createdAt;
    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
    public Integer getKdJabatan() {
        return jabatan != null ? jabatan.getKdJabatan() : null;
    }
    public String getNamaJabatan() {
        return jabatan != null ? jabatan.getNamaJabatan() : null;
    }
    public Integer getKdDepartemen() {
        return departemen != null ? departemen.getKdDepartemen() : null;
    }
    public String getNamaDepartemen() {
        return departemen != null ? departemen.getNamaDepartemen() : null;
    }
    public Integer getKdUnitKerja() {
        return unitKerja != null ? unitKerja.getKdUnitKerja() : null;
    }
    public String getNamaUnitKerja() {
        return unitKerja != null ? unitKerja.getNamaUnitKerja() : null;
    }
    public Integer getKdJenisKelamin() {
        return jenisKelamin != null ? jenisKelamin.getKdJenisKelamin() : null;
    }
    public String getNamaJenisKelamin() {
        return jenisKelamin != null ? jenisKelamin.getNamaJenisKelamin() : null;
    }
    public Integer getKdPendidikan() {
        return pendidikan != null ? pendidikan.getKdPendidikan() : null;
    }
    public String getNamaPendidikan() {
        return pendidikan != null ? pendidikan.getNamaPendidikan() : null;
    }
    public String getPassword() {
        return passwordHash;
    }
    public void setPassword(String password) {
        this.passwordHash = password;
    }
    public void setKdJabatan(Integer kdJabatan) {
    }
    public void setKdDepartemen(Integer kdDepartemen) {
    }
    public void setKdUnitKerja(Integer kdUnitKerja) {
    }
    public void setKdJenisKelamin(Integer kdJenisKelamin) {
    }
    public void setKdPendidikan(Integer kdPendidikan) {
    }
}