package co.edu.ucompensar.veterinaria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.ucompensar.veterinaria.exception.RecursoNoEncontradoException;
import co.edu.ucompensar.veterinaria.exception.ReglaNegocioException;
import co.edu.ucompensar.veterinaria.model.Cita;
import co.edu.ucompensar.veterinaria.model.Mascota;
import co.edu.ucompensar.veterinaria.repository.CitaRepository;
import co.edu.ucompensar.veterinaria.repository.MascotaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CitaServiceImpl necesita dos repositorios simulados.
 * Orden de las reglas en agendar (diagrama de secuencia):
 *   1. la mascota existe  2. la fecha es futura  3. el horario está libre
 * Cada prueba prepara solo lo que su camino usa (Mockito estricto falla
 * si se prepara un when() que el código no llega a usar).
 */
@ExtendWith(MockitoExtension.class)
class CitaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private CitaServiceImpl citaService;

    // ---------- RF-03 Agendar ----------

    @Test
    @DisplayName("RF-03 mascota existente y fecha futura: guarda en estado PENDIENTE")
    void agendar_valida_quedaPendiente() {
        Cita cita = citaPara(1L, LocalDateTime.now().plusDays(1));
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(cita.getMascota()));
        when(citaRepository.existsByMascotaIdAndFecha(1L, cita.getFecha())).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        Cita resultado = citaService.agendar(cita);

        assertEquals("PENDIENTE", resultado.getEstado());
        verify(citaRepository).save(cita);
    }

    @Test
    @DisplayName("RF-03 mascota inexistente: rechaza sin guardar")
    void agendar_mascotaInexistente_rechaza() {
        Cita cita = citaPara(99L, LocalDateTime.now().plusDays(1));
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> citaService.agendar(cita));
        verify(citaRepository, never()).save(any());
    }

    /*
     * Valores límite sobre "fecha" (debe ser futura al registrar).
     * El borde exacto (now) no es determinista con LocalDateTime.now() en el servicio;
     * se prueban los valores a cada lado. Para probar el borde exacto hay que
     * inyectar un java.time.Clock fijo en el servicio.
     */
    @ParameterizedTest(name = "RF-03 fecha pasada (hace {0} minutos) se rechaza")
    @CsvSource({"1", "60", "1440"})
    void agendar_fechaPasada_rechaza(long minutosAtras) {
        Cita cita = citaPara(1L, LocalDateTime.now().minusMinutes(minutosAtras));
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(cita.getMascota()));

        assertThrows(ReglaNegocioException.class, () -> citaService.agendar(cita));
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RF-03 misma mascota en el mismo horario: rechaza la segunda cita")
    void agendar_horarioOcupado_rechaza() {
        Cita cita = citaPara(1L, LocalDateTime.now().plusDays(1));
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(cita.getMascota()));
        when(citaRepository.existsByMascotaIdAndFecha(1L, cita.getFecha())).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> citaService.agendar(cita));
        verify(citaRepository, never()).save(any());
    }

    // ---------- RF-04 Cambiar estado: tabla de decisión ----------

    /*
     * Tabla de decisión de transiciones definidas por la especificación.
     *   estado actual | nuevo estado | resultado
     *   PENDIENTE     | ATENDIDA     | permitido
     *   PENDIENTE     | CANCELADA    | permitido
     *   ATENDIDA      | CANCELADA    | rechazado (RF-04)
     * Las combinaciones que la especificación no define (CANCELADA -> ATENDIDA)
     * no se inventan: se anotan como pregunta en el plan de pruebas.
     */
    @ParameterizedTest(name = "RF-04 {0} -> {1} es permitido")
    @CsvSource({
            "PENDIENTE, ATENDIDA",
            "PENDIENTE, CANCELADA"
    })
    void cambiarEstado_transicionPermitida_actualiza(String actual, String nuevo) {
        Cita cita = citaConEstado(5L, actual);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(cita)).thenReturn(cita);

        Cita resultado = citaService.cambiarEstado(5L, nuevo);

        assertEquals(nuevo, resultado.getEstado());
    }

    @Test
    @DisplayName("RF-04 ATENDIDA -> CANCELADA: rechaza y no guarda")
    void cambiarEstado_atendidaACancelada_rechaza() {
        Cita cita = citaConEstado(5L, "ATENDIDA");
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));

        assertThrows(ReglaNegocioException.class, () -> citaService.cambiarEstado(5L, "CANCELADA"));
        assertEquals("ATENDIDA", cita.getEstado(), "El estado no debe cambiar en memoria");
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RF-04 cita inexistente: rechaza")
    void cambiarEstado_inexistente_rechaza() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> citaService.cambiarEstado(99L, "ATENDIDA"));
        verify(citaRepository, never()).save(any());
    }

    // ---------- Datos de prueba ----------

    private Cita citaPara(Long mascotaId, LocalDateTime fecha) {
        Mascota m = new Mascota();
        m.setId(mascotaId);
        m.setNombre("Luna");
        Cita c = new Cita();
        c.setFecha(fecha);
        c.setMotivo("Vacunación");
        c.setVeterinarioAsignado("Dra. Ruiz");
        c.setMascota(m);
        return c;
    }

    private Cita citaConEstado(Long id, String estado) {
        Cita c = citaPara(1L, LocalDateTime.now().plusDays(1));
        c.setId(id);
        c.setEstado(estado);
        return c;
    }
}

