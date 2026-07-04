package com.turnero.model;

public enum EstadoTurno {
    ESPERANDO,   // el cliente tomó número y espera en la fila
    LLAMANDO,    // un receptor lo está llamando / atendiendo ahora
    ATENDIDO,    // ya fue atendido
    CANCELADO    // se saltó o canceló
}
