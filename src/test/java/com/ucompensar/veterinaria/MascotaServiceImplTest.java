package com.ucompensar.veterinaria;

public class MascotaServiceImplTest {
    void registrarDatosMascota(){
        Mascota gato= new Mascota();
        gato.setNombre("Gato");
        gato.setEspecie("gato");
        gato.setNombrePropietario("Andres");
        gato.setTelefonoPropietario("32364598");
        //emular el registro en BD
        when(MascotaRepository.save(gato)).thenReturn(gato);

        MascotaServiceImpl mascotaService = new MascotaServiceImpl() {
            Mascota p=MascotaService.registrar(gato);
    Assertions.assertEquals(1l, p.getId());

        }
    }
}
