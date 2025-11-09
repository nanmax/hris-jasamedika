package com.nanmax.hris.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "unit_kerja")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitKerja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer kdUnitKerja;
    @Column(nullable = false)
    private String namaUnitKerja;
}