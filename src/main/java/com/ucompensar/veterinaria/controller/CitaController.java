package co.edu.ucompensar.veterinaria.controller;

import co.edu.ucompensar.veterinaria.dto.CambioEstadoRequest;
import co.edu.ucompensar.veterinaria.model.Cita;
import co.edu.ucompensar.veterinaria.service.CitaService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RF-03 y RF-04.
 * Cuerpo de ejemplo para agendar:
 * {"fecha":"2026-11-05T10:00:00","motivo":"Vacunación","veterinarioAsignado":"Dra. Ruiz","mascota":{"id":1}}
 */
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    /** RF-03. 201 con la cita en estado PENDIENTE; 404 mascota inexistente; 409 regla de negocio. */
    @PostMapping
    public ResponseEntity<Cita> agendar(@RequestBody Cita cita) {
        Cita agendada = citaService.agendar(cita);
        return ResponseEntity
                .created(URI.create("/api/citas/" + agendada.getId()))
                .body(agendada);
    }

    /** RF-04. 200 con el nuevo estado; 404 cita inexistente; 409 transición no permitida. */
    @PutMapping("/{id}/estado")
    public ResponseEntity<Cita> actualizarEstado(@PathVariable Long id,
                                                 @RequestBody CambioEstadoRequest cambio) {
        return ResponseEntity.ok(citaService.cambiarEstado(id, cambio.estado()));
    }
}
