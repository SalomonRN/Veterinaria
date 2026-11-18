package co.edu.ucompensar.veterinaria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.ucompensar.veterinaria.exception.RecursoNoEncontradoException;
import co.edu.ucompensar.veterinaria.exception.ValidacionException;
import co.edu.ucompensar.veterinaria.model.Cita;
import co.edu.ucompensar.veterinaria.model.Mascota;
import co.edu.ucompensar.veterinaria.repository.MascotaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Prueba unitaria pura: sin Spring y sin base de datos.
 * Se prueba MascotaServiceImpl; el repositorio es un doble de Mockito.
 */
@ExtendWith(MockitoExtension.class)
class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    // ---------- RF-01 Registrar ----------

    @Test
    @DisplayName("RF-01 datos válidos: guarda y devuelve la mascota con id")
    void registrar_valida_guardaYDevuelveId() {
        Mascota nueva = mascotaValida();
        Mascota guardada = mascotaValida();
        guardada.setId(1L);
        when(mascotaRepository.save(nueva)).thenReturn(guardada);

        Mascota resultado = mascotaService.registrar(nueva);

        assertEquals(1L, resultado.getId());
        verify(mascotaRepository).save(nueva);
    }

    /*
     * Partición de equivalencia sobre "nombre":
     *   clase válida   -> texto con contenido  (cubierta en la prueba anterior)
     *   clases inválidas -> null, "" y solo espacios
     * Si "" o "   " pasan, el servicio solo valida null: es un hallazgo
     * que se registra como incidencia o se lleva como pregunta al docente.
     */
    @ParameterizedTest(name = "RF-01 nombre inválido [{0}] se rechaza sin guardar")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void registrar_nombreInvalido_rechaza(String nombre) {
        Mascota m = mascotaValida();
        m.setNombre(nombre);

        assertThrows(ValidacionException.class, () -> mascotaService.registrar(m));
        verify(mascotaRepository, never()).save(any());
    }

    @ParameterizedTest(name = "RF-01 especie inválida [{0}] se rechaza sin guardar")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void registrar_especieInvalida_rechaza(String especie) {
        Mascota m = mascotaValida();
        m.setEspecie(especie);

        assertThrows(ValidacionException.class, () -> mascotaService.registrar(m));
        verify(mascotaRepository, never()).save(any());
    }

    // ---------- RF-02 Consultar ----------

    @Test
    @DisplayName("RF-02 id existente: devuelve la mascota con sus citas")
    void consultar_existente_devuelveConCitas() {
        Mascota luna = mascotaValida();
        luna.setId(1L);
        luna.setCitas(List.of(new Cita(), new Cita()));
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(luna));

        Mascota resultado = mascotaService.consultar(1L);

        assertSame(luna, resultado);
        assertEquals(2, resultado.getCitas().size());
    }

    @Test
    @DisplayName("RF-02 id inexistente: lanza RecursoNoEncontradoException")
    void consultar_inexistente_lanzaNoEncontrado() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException e = assertThrows(RecursoNoEncontradoException.class,
                () -> mascotaService.consultar(99L));
        assertNotNull(e.getMessage());
    }

    // ---------- RF-05 Actualizar propietario ----------

    @Test
    @DisplayName("RF-05 datos válidos: guarda los nuevos datos del propietario")
    void actualizarPropietario_valido_guardaCambios() {
        Mascota existente = mascotaValida();
        existente.setId(1L);
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));

        mascotaService.actualizarPropietario(1L, "Carlos Gómez", "3109876543");

        // verify solo dice que se llamó; el captor dice CON QUÉ datos
        ArgumentCaptor<Mascota> captor = ArgumentCaptor.forClass(Mascota.class);
        verify(mascotaRepository).save(captor.capture());
        assertEquals("Carlos Gómez", captor.getValue().getNombrePropietario());
        assertEquals("3109876543", captor.getValue().getTelefonoPropietario());
        assertEquals("Luna", captor.getValue().getNombre(), "No debe tocar los demás campos");
    }

    @Test
    @DisplayName("RF-05 id inexistente: rechaza y no guarda")
    void actualizarPropietario_inexistente_noGuarda() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> mascotaService.actualizarPropietario(99L, "X", "1"));
        verify(mascotaRepository, never()).save(any());
    }

    // ---------- Datos de prueba ----------

    private Mascota mascotaValida() {
        Mascota m = new Mascota();
        m.setNombre("Luna");
        m.setEspecie("Perro");
        m.setRaza("Criollo");
        m.setNombrePropietario("Ana Pérez");
        m.setTelefonoPropietario("3001234567");
        return m;
    }
}

