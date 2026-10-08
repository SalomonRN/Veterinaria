package com.ucompensar.veterinaria.repository;


import com.ucompensar.veterinaria.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitaRepository extends JpaRepository<Cita, Long> {
}
