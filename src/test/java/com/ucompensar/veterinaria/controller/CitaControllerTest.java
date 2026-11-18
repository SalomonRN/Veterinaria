package co.edu.ucompensar.veterinaria.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.ucompensar.veterinaria.exception.RecursoNoEncontradoException;
import co.edu.ucompensar.veterinaria.exception.ReglaNegocioException;
import co.edu.ucompensar.veterinaria.model.Cita;
import co.edu.ucompensar.veterinaria.model.Mascota;
import co.edu.ucompensar.veterinaria.service.CitaService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Contrato HTTP de /api/citas. El servicio está simulado.
 */
@WebMvcTest(CitaController.class)
class CitaControllerTest {

    private static final String JSON_CITA = """
            {"fecha":"2026-11-05T10:00:00","motivo":"Vacunación",
             "veterinarioAsignado":"Dra. Ruiz","mascota":{"id":1}}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CitaService citaService;

    // ---------- RF-03 Agendar ----------

    @Test
    @DisplayName("POST /api/citas válida -> 201 con estado PENDIENTE")
    void agendar_valida_devuelve201Pendiente() throws Exception {
        when(citaService.agendar(any(Cita.class))).thenReturn(cita(10L, "PENDIENTE"));

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CITA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/citas/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("POST /api/citas: el id de la mascota del JSON llega al servicio")
    void agendar_deserializaMascotaYFecha() throws Exception {
        when(citaService.agendar(any(Cita.class))).thenReturn(cita(10L, "PENDIENTE"));

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CITA))
                .andExpect(status().isCreated());

        // Si Cita.mascota tiene @JsonBackReference o @JsonIgnore, la mascota llega en null
        ArgumentCaptor<Cita> captor = ArgumentCaptor.forClass(Cita.class);
        verify(citaService).agendar(captor.capture());
        Cita recibida = captor.getValue();
        assertNotNull(recibida.getMascota(),
                "La mascota no se deserializó: revise las anotaciones JSON de Cita.mascota");
        assertEquals(1L, recibida.getMascota().getId());
        assertEquals(
                LocalDateTime.of(2026, 11, 5, 10, 0), recibida.getFecha());
    }

    @Test
    @DisplayName("POST /api/citas con mascota inexistente -> 404")
    void agendar_mascotaInexistente_devuelve404() throws Exception {
        when(citaService.agendar(any(Cita.class)))
                .thenThrow(new RecursoNoEncontradoException("Mascota no existe: 1"));

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CITA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Mascota no existe: 1"));
    }

    @Test
    @DisplayName("POST /api/citas en horario ocupado -> 409")
    void agendar_horarioOcupado_devuelve409() throws Exception {
        when(citaService.agendar(any(Cita.class)))
                .thenThrow(new ReglaNegocioException("La mascota ya tiene una cita en ese horario"));

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CITA))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("La mascota ya tiene una cita en ese horario"));
    }

    // ---------- RF-04 Cambiar estado ----------

    @Test
    @DisplayName("PUT /api/citas/{id}/estado PENDIENTE -> ATENDIDA -> 200")
    void cambiarEstado_valido_devuelve200() throws Exception {
        when(citaService.cambiarEstado(10L, "ATENDIDA")).thenReturn(cita(10L, "ATENDIDA"));

        mockMvc.perform(put("/api/citas/10/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"ATENDIDA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATENDIDA"));
    }

    @Test
    @DisplayName("PUT /api/citas/{id}/estado ATENDIDA -> CANCELADA -> 409")
    void cambiarEstado_atendidaACancelada_devuelve409() throws Exception {
        when(citaService.cambiarEstado(10L, "CANCELADA"))
                .thenThrow(new ReglaNegocioException("Una cita ATENDIDA no puede cancelarse"));

        mockMvc.perform(put("/api/citas/10/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CANCELADA\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /api/citas/{id}/estado con cita inexistente -> 404")
    void cambiarEstado_inexistente_devuelve404() throws Exception {
        when(citaService.cambiarEstado(99L, "ATENDIDA"))
                .thenThrow(new RecursoNoEncontradoException("Cita no existe: 99"));

        mockMvc.perform(put("/api/citas/99/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"ATENDIDA\"}"))
                .andExpect(status().isNotFound());
    }

    // ---------- Datos de prueba ----------

    private Cita cita(Long id, String estado) {
        Mascota m = new Mascota();
        m.setId(1L);
        m.setNombre("Luna");
        Cita c = new Cita();
        c.setId(id);
        c.setFecha(LocalDateTime.of(2026, 11, 5, 10, 0));
        c.setMotivo("Vacunación");
        c.setVeterinarioAsignado("Dra. Ruiz");
        c.setEstado(estado);
        c.setMascota(m);
        return c;
    }
}