package com.ucompensar.veterinaria.repository;

import com.ucompensar.veterinaria.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
}
