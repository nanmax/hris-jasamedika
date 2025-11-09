package com.nanmax.hris.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "pendidikan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pendidikan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer kdPendidikan;
    @Column(nullable = false)
    private String namaPendidikan;
}