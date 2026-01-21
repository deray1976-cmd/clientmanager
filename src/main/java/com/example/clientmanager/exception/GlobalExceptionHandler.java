package com.example.clientmanager.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {


@ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<String> handleClientNotFound(ClientNotFoundException ex) {
        // Retorna exactament el missatge que espera el test
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Client amb id " + ex.getId() + " no trobat");
    }
@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
    String error = ex.getBindingResult()
                     .getFieldErrors()
                     .get(0)
                     .getDefaultMessage();

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    @ExceptionHandler(Exception.class)
public ResponseEntity<String> handleGeneric(Exception ex) {
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error intern del servidor");
}
}
