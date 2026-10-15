package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.model.Mascota;
import com.ucompensar.veterinaria.repository.MascotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

    @Service
public class MascotaServiceImpl implements MascotaService {

    private MascotaRepository mascotaRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    @Override
    public Mascota registrar(Mascota mascota) {

        return mascotaRepository.save(mascota);
    }

    @Override
    public Mascota actualizar(Mascota mascota, Long id) {

        Mascota mascota1 = mascotaRepository.findById(id).get();
        mascota1.setNombre(mascota.getNombre());
        mascota1.setEspecie(mascota.getEspecie());
        mascota1.setTelefonoPropietario(mascota.getNombre());
        mascota1.setNombrePropietario(mascota.getEspecie());

        return mascotaRepository.save(mascota1);
    }

    @Override
    public boolean eliminar(Long id) {
        Mascota mascota1 = obtenerPorId(id);
        if(mascota1 !=null){
            mascotaRepository.delete(mascota1);
            return true;
        }
        return false;

    }

    @Override
    public Mascota obtenerPorId(Long id) {

        return mascotaRepository.findById(id).get();

    }

    @Override
    public List<Mascota> listarTodo() {
        return List.of();
    }
}
