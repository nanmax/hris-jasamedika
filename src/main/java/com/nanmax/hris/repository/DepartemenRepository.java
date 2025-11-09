package com.nanmax.hris.repository;
import com.nanmax.hris.entity.Departemen;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DepartemenRepository extends JpaRepository<Departemen, Integer> {
}