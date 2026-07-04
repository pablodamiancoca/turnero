package com.turnero.config;

import com.turnero.controller.VistaController;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = VistaController.class)
public class ManejadorErroresWeb {

    @ExceptionHandler(EntityNotFoundException.class)
    public String noEncontrado(EntityNotFoundException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error-empresa";
    }
}
