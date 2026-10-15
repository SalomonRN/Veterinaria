package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.model.Mascota;

import java.util.List;

public interface MascotaService {
    Mascota registrar(Mascota mascota);
    Mascota actualizar(Mascota mascota, Long id);
    boolean eliminar(Long id);

    Mascota obtenerPorId(Long id);
    List<Mascota> listarTodo();
}
