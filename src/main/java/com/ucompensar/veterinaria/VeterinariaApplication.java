package com.ucompensar.veterinaria;

import com.ucompensar.veterinaria.model.Mascota;
import com.ucompensar.veterinaria.repository.MascotaRepository;
import com.ucompensar.veterinaria.service.MascotaService;
import com.ucompensar.veterinaria.service.MascotaServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class VeterinariaApplication {

    public static void main(String[] args) {


        // SpringApplication.run(VeterinariaApplication.class, args);

        ConfigurableApplicationContext context = SpringApplication.run(VeterinariaApplication.class, args);

        MascotaServiceImpl mascotaService = context.getBean(MascotaServiceImpl.class);

        Mascota mascotaDB = mascotaService.obtenerPorId(2L);

        Mascota mascota = new Mascota();

        mascota.setNombre("Pecas");
        mascota.setEspecie("Dalmata");
        mascota.setTelefonoPropietario("+91000000");
        mascota.setNombrePropietario("Jhon");

        //mascotaService.registrar(mascota);
        //mascotaService.eliminar(1L);

        mascotaDB.setEspecie("Chiguagua");

        mascotaService.actualizar(mascotaDB, 2L);
    }
}
