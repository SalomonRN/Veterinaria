package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.model.Mascota;

public class MascotaServiceImpl implements MascotaService {

    @Override
    public Mascota registrar(Mascota mascota) {

        if(mascota.getNombre().isBlank()){
            return null;
        }

        if(mascota.getEspecie().isBlank()){
            return null;
        }

        return mascota;
    }
}
