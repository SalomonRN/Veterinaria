package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.model.Mascota;
import com.ucompensar.veterinaria.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaServiceImpl implements MascotaService {

    public MascotaRepository mascotaRepository;

    @Autowired
    public MascotaServiceImpl(MascotaRepository mascotaRepository){
        this.mascotaRepository = mascotaRepository;
    }

    @Override
    public Mascota registrar(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    @Override
    public Mascota actualizar(Mascota mascota, Long id) {
        return mascotaRepository.save(mascota);
    }

    @Override
    public boolean eliminar(Long id) {
        if(obtenerPorId(id) != null){
            mascotaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Mascota obtenerPorId(Long id) {
        return mascotaRepository.getReferenceById(id);
    }

    @Override
    public List<Mascota> listarTodo() {
        return mascotaRepository.findAll();

    }
}
