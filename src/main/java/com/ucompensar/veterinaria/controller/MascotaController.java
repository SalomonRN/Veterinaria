package co.edu.ucompensar.veterinaria.controller;

import co.edu.ucompensar.veterinaria.dto.ActualizarPropietarioRequest;
import co.edu.ucompensar.veterinaria.model.Mascota;
import co.edu.ucompensar.veterinaria.service.MascotaService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RF-01, RF-02 y RF-05.
 * El controlador no tiene reglas de negocio: recibe, delega al servicio y arma la respuesta HTTP.
 * Los errores los traduce ManejadorErrores, no este controlador.
 */
@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    /** RF-01. 201 Created con la cabecera Location apuntando al recurso nuevo. */
    @PostMapping
    public ResponseEntity<Mascota> registrar(@RequestBody Mascota mascota) {
        Mascota guardada = mascotaService.registrar(mascota);
        return ResponseEntity
                .created(URI.create("/api/mascotas/" + guardada.getId()))
                .body(guardada);
    }

    /** RF-02. 200 con la mascota y su lista de citas; 404 si no existe. */
    @GetMapping("/{id}")
    public ResponseEntity<Mascota> consultar(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.consultar(id));
    }

    /** RF-05. 200 con la mascota actualizada; 404 si no existe. */
    @PutMapping("/{id}")
    public ResponseEntity<Mascota> actualizar(@PathVariable Long id,
                                              @RequestBody ActualizarPropietarioRequest datos) {
        Mascota actualizada = mascotaService.actualizarPropietario(
                id, datos.nombrePropietario(), datos.telefonoPropietario());
        return ResponseEntity.ok(actualizada);
    }
}