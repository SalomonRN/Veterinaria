package co.edu.ucompensar.veterinaria.dto;

/**
 * Cuerpo de PUT /api/mascotas/{id} (RF-05).
 * Solo viaja lo que el requerimiento permite cambiar: los datos del propietario.
 */
public record ActualizarPropietarioRequest(String nombrePropietario, String telefonoPropietario) {
}