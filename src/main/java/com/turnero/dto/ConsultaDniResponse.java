package com.turnero.dto;

/**
 * Respuesta de la búsqueda por DNI.
 * "encontrado" indica si el proveedor de datos devolvió un nombre asociado;
 * si es false, el frontend debe pedirle al cliente que cargue el nombre a mano.
 */
public record ConsultaDniResponse(String dni, String nombre, boolean encontrado) {}
