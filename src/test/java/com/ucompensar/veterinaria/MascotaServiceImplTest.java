package com.ucompensar.veterinaria;

import com.ucompensar.veterinaria.model.Mascota;
import com.ucompensar.veterinaria.service.MascotaServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MascotaServiceImplTest {

    @Test
    public void nombreMascotaVacio(){
        Mascota mascota = new Mascota();
        MascotaServiceImpl mascotaService = new MascotaServiceImpl();
        mascota.setNombre("Pepito");
        mascota.setEspecie("Gato");

        Long idMascota = mascotaService.registrar(mascota).getId();
        assertEquals(1L, idMascota);


    }
}
