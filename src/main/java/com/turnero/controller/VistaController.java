package com.turnero.controller;

import com.turnero.model.Empresa;
import com.turnero.repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class VistaController {

    private final EmpresaRepository empresaRepository;

    public VistaController(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    // Página de inicio: listado simple + acceso al panel de administración
    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("empresas", empresaRepository.findAll());
        return "inicio";
    }

    // Pantalla pública grande, para mostrar en un monitor de la sala de espera
    // Ej: https://tu-dominio.com/mi-cafeteria
    @GetMapping("/{slug}")
    public String pantalla(@PathVariable String slug, Model model) {
        Empresa empresa = cargarEmpresa(slug);
        model.addAttribute("empresa", empresa);
        return "pantalla";
    }

    // Página donde el cliente toca el botón para tomar su número
    // Ej: https://tu-dominio.com/mi-cafeteria/tomar
    @GetMapping("/{slug}/tomar")
    public String tomar(@PathVariable String slug, Model model) {
        Empresa empresa = cargarEmpresa(slug);
        model.addAttribute("empresa", empresa);
        return "tomar";
    }

    // Panel del receptor/mozo/vendedor para llamar al siguiente cliente
    // Ej: https://tu-dominio.com/mi-cafeteria/panel
    @GetMapping("/{slug}/panel")
    public String panel(@PathVariable String slug, Model model) {
        Empresa empresa = cargarEmpresa(slug);
        model.addAttribute("empresa", empresa);
        return "panel";
    }

    // Formulario simple para dar de alta una empresa nueva sin usar la API a mano
    @GetMapping("/admin/nueva-empresa")
    public String nuevaEmpresa() {
        return "nueva-empresa";
    }

    private Empresa cargarEmpresa(String slug) {
        return empresaRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("No existe una empresa con la URL: /" + slug));
    }
}
