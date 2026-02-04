package com.example.clientmanager.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {


//@ExceptionHandler(ClientNotFoundException.class)
/*     public ResponseEntity<String> handleClientNotFound(ClientNotFoundException ex) {
        // Retorna exactament el missatge que espera el test
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Client amb id " + ex.getId() + " no trobat");
    }
*/
@ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ApiError> handleClientNotFound(ClientNotFoundException ex,
                                                         HttpServletRequest request) {
        ApiError error = new ApiError(
                LocalDateTime.now(),     // timestamp
                request.getRequestURI(), // path
                ex.getMessage()          // missatge
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }




/*@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
    String error = ex.getBindingResult()
                     .getFieldErrors()
                     .get(0)
                     .getDefaultMessage();

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }*/

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, Object>> handleValidationErrors(
        MethodArgumentNotValidException ex,
        HttpServletRequest request) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", Instant.now());
    body.put("path", request.getRequestURI());
    body.put("errors", errors);

    return ResponseEntity.badRequest().body(body);
}

   /*  @ExceptionHandler(Exception.class)
public ResponseEntity<String> handleGeneralException(Exception ex) {
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error intern del servidor");
}*/

@ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                LocalDateTime.now(),
                request.getRequestURI(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
