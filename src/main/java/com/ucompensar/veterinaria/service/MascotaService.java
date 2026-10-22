package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.model.Mascota;

import java.util.List;

public interface MascotaService {

    public Mascota registrar(Mascota mascota);
    public Mascota actualizar(Mascota mascota, Long id);
    public boolean eliminar(Long id);

    public Mascota obtenerPorId(Long id);
    public List<Mascota> listarTodo();
}
