package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.model.Cita;
import com.ucompensar.veterinaria.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    @Autowired
    public CitaServiceImpl(CitaRepository citaRepository) {this.citaRepository = citaRepository;}

    @Override
    public Cita registrar(Cita cita) {
        return citaRepository.save(cita);
    }

    @Override
    public Cita actualizar(Cita cita, Long id) {return citaRepository.save(cita);}

    @Override
    public boolean eliminar(Long id) {
        if(obtenerPorId(id) != null){
            citaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Cita obtenerPorId(Long id) {return citaRepository.getReferenceById(id);}

    @Override
    public List<Cita> listarTodo() {
        return citaRepository.findAll();
    }
}
