package com.turnero.dto;

/** Cuerpo que envía el cliente al tomar un turno, ya con el DNI y el nombre confirmado/corregido. */
public record TomarTurnoRequest(String dni, String nombre) {}
