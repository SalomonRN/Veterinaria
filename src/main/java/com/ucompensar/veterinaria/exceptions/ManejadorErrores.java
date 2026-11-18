package co.edu.ucompensar.veterinaria.exceptions;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones del dominio a códigos HTTP en un solo lugar.
 * Así el servicio no conoce HTTP y el controlador no tiene try/catch.
 *
 *   RecursoNoEncontradoException -> 404 Not Found
 *   ValidacionException          -> 400 Bad Request
 *   ReglaNegocioException        -> 409 Conflict
 *
 * Cuerpo de error uniforme: {"error": "mensaje"}
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(RecursoNoEncontradoException e) {
        return respuesta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<Map<String, String>> validacion(ValidacionException e) {
        return respuesta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> reglaNegocio(ReglaNegocioException e) {
        return respuesta(HttpStatus.CONFLICT, e.getMessage());
    }

    private ResponseEntity<Map<String, String>> respuesta(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of("error", mensaje == null ? "" : mensaje));
    }
}
