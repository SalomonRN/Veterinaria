package com.ucompensar.veterinaria;

import com.ucompensar.veterinaria.model.Cita;
import com.ucompensar.veterinaria.repository.MascotaRepository;
import com.ucompensar.veterinaria.service.CitaService;
import com.ucompensar.veterinaria.service.CitaServiceImpl;
import com.ucompensar.veterinaria.service.MascotaService;
import com.ucompensar.veterinaria.exceptions.ValidacionException;
import com.ucompensar.veterinaria.model.Mascota;
import com.ucompensar.veterinaria.service.MascotaServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MascotaServiceImplTest {

    @InjectMocks
    private MascotaServiceImpl mascotaServiceImpl;


    Mascota crearMascota() {
        Mascota mascota = new Mascota();
        mascota.setNombre("Kida");
        mascota.setEspecie("Dalmata");
        mascota.setNombrePropietario("Jhon");
        mascota.setTelefonoPropietario("487512042");

        return mascota;
    }

    private Assertions Assert;
    @Autowired
    MascotaService mascotaService;
    @Autowired
    private MascotaRepository mascotaRepository;

    @Test
    void registrar_mascota_nombre_vacio(){
        Mascota mascota = new Mascota();


        mascota.setEspecie("Dalmata");
        mascota.setNombrePropietario("Jhon");
        mascota.setTelefonoPropietario("3050620362");

        Assert.assertThrows(ValidacionException.class, () -> mascotaService.registrar(mascota));
    }
    @Test
    void consultar_mascotaExistente_devuelveMascotaConCitas(){
        //Arrange

        Mascota mascota = new crearMascota();
        mascota.setId(1L);
        mascota.setCitas(List.of(new Cita()));
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));

        //Act
        Mascota resultado = mascotaService.obtenerPorId(1L);

        //Assert
        assertSame(mascota, resultado);
        assertEquals(1, resultado.getCitas().size());
    }
}
