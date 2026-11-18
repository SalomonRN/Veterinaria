package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.model.Cita;
import java.util.List;
public interface CitaService {

    Cita registrar(Cita cita);
    Cita editar(Cita cita);
    Cita actualizar(Cita cita, Long id);
    boolean eliminar(Long id);

    Cita consultarPorId(Long id);
    List<Cita> consultarTodas();
}
