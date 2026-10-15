package com.ucompensar.veterinaria;

import com.ucompensar.veterinaria.model.Mascota;
import com.ucompensar.veterinaria.service.MascotaServiceImpl;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MascotaServiceImplTest {

    private final MascotaServiceImpl mascotaService;
    @Autowired
    public MascotaServiceImplTest(MascotaServiceImpl mascotaService) {
        this.mascotaService = mascotaService;
    }

    @Test
    public void nombreMascotaVacio(){
        Mascota mascota = new Mascota();

        mascota.setNombre("Pepito");
        mascota.setEspecie("Gato");

        Long idMascota = mascotaService.registrar(mascota).getId();


    }
    @Test
    public void guardarMascota(){
        Mascota mascota = new Mascota();
        mascota.setNombre("Pepito");
        mascota.setNombrePropietario("Pepe");
        mascota.setEspecie("ESPECIAL");

        assertNotNull(mascotaService.registrar(mascota));
    }
}
