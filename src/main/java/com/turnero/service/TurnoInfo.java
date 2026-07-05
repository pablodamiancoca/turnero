package com.turnero.service;

/**
 * Representación liviana de un turno para enviar por JSON / SSE,
 * evita exponer directamente la entidad JPA (y problemas de serialización lazy).
 */
public record TurnoInfo(
        Long id,
        String etiqueta,
        String estado,
        String ventanilla,
        int enEspera,
        String dni,
        String nombreCliente
) {}
