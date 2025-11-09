package com.nanmax.hris.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "departemen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departemen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer kdDepartemen;
    @Column(nullable = false)
    private String namaDepartemen;
}