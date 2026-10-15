package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.model.Cita;
import com.ucompensar.veterinaria.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitaServiceImpl implements CitaService {

    private CitaRepository citaRepository;

    public CitaServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public Cita registrar(Cita cita) {
        return null;
    }

    @Override
    public Cita editar(Cita cita) {
        return null;
    }

    @Override
    public Cita actualizar(Cita cita, Long id) {
        Cita cita1 = consultarPorId(id);
        cita1.setId(cita.getId());
        cita1.setFecha(cita.getFecha());

        return null;
    }

    @Override
    public boolean eliminar(Long id) {
        return false;
    }

    @Override
    public Cita consultarPorId(Long id) {
        return citaRepository.findById(id).get();
    }

    @Override
    public List<Cita> consultarTodas() {
        return List.of();
    }
}
