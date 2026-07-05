package com.turnero.service;

import java.util.Optional;

/**
 * Punto de conexión con un proveedor externo de datos de personas físicas (DNI -> nombre).
 *
 * IMPORTANTE: no existe una API pública/gratuita "oficial" del RENAPER/ANSES para esto.
 * Antes de usar esto en producción hay que contratar un proveedor autorizado (por ejemplo,
 * un servicio de validación de identidad/KYC) y cumplir con la Ley 25.326 de Protección de
 * Datos Personales: informar al titular, pedir consentimiento cuando corresponda, y resguardar
 * el dato (el DNI es un dato personal).
 *
 * Esta interfaz permite reemplazar la implementación de ejemplo (ConsultaDniServiceMock)
 * por la de tu proveedor real sin tocar el resto de la aplicación.
 */
public interface ConsultaDniService {
    Optional<String> buscarNombrePorDni(String dni);
}
