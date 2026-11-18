package co.edu.ucompensar.veterinaria.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.ucompensar.veterinaria.exception.RecursoNoEncontradoException;
import co.edu.ucompensar.veterinaria.exception.ValidacionException;
import co.edu.ucompensar.veterinaria.model.Cita;
import co.edu.ucompensar.veterinaria.model.Mascota;
import co.edu.ucompensar.veterinaria.service.MascotaService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Prueba de la capa web aislada: @WebMvcTest levanta solo MascotaController
 * y ManejadorErrores. El servicio es un doble (@MockitoBean): aquí no se prueban
 * reglas de negocio, se prueba el contrato HTTP (estado, JSON, cabeceras).
 *
 * Spring Boot 4: @WebMvcTest viene en spring-boot-starter-webmvc-test y @MockBean ya no existe; se usa @MockitoBean.
 */
@WebMvcTest(MascotaController.class)
class MascotaControllerTest {

    private static final String JSON_NUEVA = """
            {"nombre":"Luna","especie":"Perro","raza":"Criollo",
             "fechaNacimiento":"2022-03-10",
             "nombrePropietario":"Ana Pérez","telefonoPropietario":"3001234567"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MascotaService mascotaService;

    // ---------- RF-01 Registrar ----------

    @Test
    @DisplayName("POST /api/mascotas válido -> 201, Location y JSON con id")
    void registrar_valida_devuelve201() throws Exception {
        when(mascotaService.registrar(any(Mascota.class))).thenReturn(mascota(1L, "Luna"));

        mockMvc.perform(post("/api/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_NUEVA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/mascotas/1")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Luna"));
    }

    @Test
    @DisplayName("POST /api/mascotas sin nombre -> 400 con mensaje de error")
    void registrar_sinNombre_devuelve400() throws Exception {
        when(mascotaService.registrar(any(Mascota.class)))
                .thenThrow(new ValidacionException("El nombre es obligatorio"));

        mockMvc.perform(post("/api/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"especie\":\"Perro\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nombre es obligatorio"));
    }

    @Test
    @DisplayName("POST /api/mascotas con JSON mal formado -> 400 sin llegar al servicio")
    void registrar_jsonMalFormado_devuelve400() throws Exception {
        mockMvc.perform(post("/api/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": "))
                .andExpect(status().isBadRequest());
    }

    // ---------- RF-02 Consultar ----------

    @Test
    @DisplayName("GET /api/mascotas/{id} existente -> 200 con sus citas")
    void consultar_existente_devuelve200ConCitas() throws Exception {
        Mascota luna = mascota(1L, "Luna");
        Cita cita = new Cita();
        cita.setId(10L);
        cita.setFecha(LocalDateTime.of(2026, 11, 5, 10, 0));
        cita.setMotivo("Vacunación");
        cita.setEstado("PENDIENTE");
        cita.setMascota(luna);              // referencia circular a propósito
        luna.setCitas(List.of(cita));
        when(mascotaService.consultar(1L)).thenReturn(luna);

        // Si la entidad no corta el ciclo Mascota -> Cita -> Mascota,
        // esta prueba falla por recursión infinita al serializar.
        mockMvc.perform(get("/api/mascotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.citas.length()").value(1))
                .andExpect(jsonPath("$.citas[0].motivo").value("Vacunación"))
                .andExpect(jsonPath("$.citas[0].estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("GET /api/mascotas/{id} inexistente -> 404")
    void consultar_inexistente_devuelve404() throws Exception {
        when(mascotaService.consultar(99L))
                .thenThrow(new RecursoNoEncontradoException("Mascota no existe: 99"));

        mockMvc.perform(get("/api/mascotas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Mascota no existe: 99"));
    }

    // ---------- RF-05 Actualizar propietario ----------

    @Test
    @DisplayName("PUT /api/mascotas/{id} válido -> 200 y el servicio recibe los datos nuevos")
    void actualizar_valido_devuelve200() throws Exception {
        Mascota actualizada = mascota(1L, "Luna");
        actualizada.setNombrePropietario("Carlos Gómez");
        actualizada.setTelefonoPropietario("3109876543");
        when(mascotaService.actualizarPropietario(1L, "Carlos Gómez", "3109876543"))
                .thenReturn(actualizada);

        mockMvc.perform(put("/api/mascotas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombrePropietario":"Carlos Gómez","telefonoPropietario":"3109876543"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombrePropietario").value("Carlos Gómez"))
                .andExpect(jsonPath("$.telefonoPropietario").value("3109876543"));

        // El controlador pasó el id de la ruta y los campos del cuerpo, no otros
        verify(mascotaService).actualizarPropietario(eq(1L), eq("Carlos Gómez"), eq("3109876543"));
    }

    @Test
    @DisplayName("PUT /api/mascotas/{id} inexistente -> 404")
    void actualizar_inexistente_devuelve404() throws Exception {
        when(mascotaService.actualizarPropietario(eq(99L), anyString(), anyString()))
                .thenThrow(new RecursoNoEncontradoException("Mascota no existe: 99"));

        mockMvc.perform(put("/api/mascotas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombrePropietario\":\"X\",\"telefonoPropietario\":\"1\"}"))
                .andExpect(status().isNotFound());
    }

    // ---------- Datos de prueba ----------

    private Mascota mascota(Long id, String nombre) {
        Mascota m = new Mascota();
        m.setId(id);
        m.setNombre(nombre);
        m.setEspecie("Perro");
        m.setNombrePropietario("Ana Pérez");
        m.setTelefonoPropietario("3001234567");
        return m;
    }
}
