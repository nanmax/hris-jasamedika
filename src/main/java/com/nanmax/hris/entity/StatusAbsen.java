package com.nanmax.hris.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "status_absen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusAbsen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer kdStatus;
    @Column(nullable = false)
    private String namaStatus;
}