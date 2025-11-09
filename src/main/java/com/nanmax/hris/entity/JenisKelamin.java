package com.nanmax.hris.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "jenis_kelamin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JenisKelamin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer kdJenisKelamin;
    @Column(nullable = false)
    private String namaJenisKelamin;
}