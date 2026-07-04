package com.turnero.controller;

import com.turnero.model.Empresa;
import com.turnero.repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaApiController {

    private final EmpresaRepository empresaRepository;

    public EmpresaApiController(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @GetMapping
    public List<Empresa> listar() {
        return empresaRepository.findAll();
    }

    @GetMapping("/{slug}")
    public Empresa obtener(@PathVariable String slug) {
        return empresaRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("No existe la empresa: " + slug));
    }

    // Crea una nueva empresa/cliente del sistema. Así se agregan nuevos negocios sin tocar código.
    @PostMapping
    public ResponseEntity<Empresa> crear(@Valid @RequestBody Empresa empresa) {
        if (empresaRepository.existsBySlug(empresa.getSlug())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Empresa guardada = empresaRepository.save(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    // Permite actualizar el logo, colores, textos y prefijo en cualquier momento.
    @PutMapping("/{slug}")
    public Empresa actualizar(@PathVariable String slug, @RequestBody Empresa cambios) {
        Empresa empresa = empresaRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("No existe la empresa: " + slug));

        if (cambios.getNombre() != null) empresa.setNombre(cambios.getNombre());
        if (cambios.getLogoUrl() != null) empresa.setLogoUrl(cambios.getLogoUrl());
        if (cambios.getColorPrimario() != null) empresa.setColorPrimario(cambios.getColorPrimario());
        if (cambios.getColorSecundario() != null) empresa.setColorSecundario(cambios.getColorSecundario());
        if (cambios.getPrefijo() != null) empresa.setPrefijo(cambios.getPrefijo());
        if (cambios.getMensajeBienvenida() != null) empresa.setMensajeBienvenida(cambios.getMensajeBienvenida());
        empresa.setReinicioDiario(cambios.isReinicioDiario());
        empresa.setActivo(cambios.isActivo());

        return empresaRepository.save(empresa);
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> borrar(@PathVariable String slug) {
        Empresa empresa = empresaRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("No existe la empresa: " + slug));
        empresaRepository.delete(empresa);
        return ResponseEntity.noContent().build();
    }
}
