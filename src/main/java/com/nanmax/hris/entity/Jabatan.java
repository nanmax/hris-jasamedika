package com.nanmax.hris.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "jabatan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jabatan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer kdJabatan;
    @Column(nullable = false)
    private String namaJabatan;
}