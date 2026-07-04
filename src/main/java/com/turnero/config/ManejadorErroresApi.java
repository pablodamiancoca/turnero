package com.turnero.config;

import com.turnero.controller.EmpresaApiController;
import com.turnero.controller.TurnoApiController;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = {EmpresaApiController.class, TurnoApiController.class})
public class ManejadorErroresApi {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }
}
