package com.nanmax.hris.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterData {
    private Integer id;
    private String nama;
    public MasterData(String nama) {
        this.nama = nama;
    }
}