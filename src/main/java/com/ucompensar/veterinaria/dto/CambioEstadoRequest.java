package co.edu.ucompensar.veterinaria.dto;

/**
 * Cuerpo de PUT /api/citas/{id}/estado (RF-04). Ejemplo: {"estado": "ATENDIDA"}
 */
public record CambioEstadoRequest(String estado) {
}