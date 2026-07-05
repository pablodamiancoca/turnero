package com.turnero.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementación de EJEMPLO. No consulta ninguna base real de personas.
 *
 * Reemplazá esta clase por la integración con tu proveedor autorizado de datos
 * (por ejemplo, un servicio de validación de identidad contratado), implementando
 * la interfaz ConsultaDniService. Mientras tanto, esta versión simplemente no
 * encuentra a nadie, por lo que el cliente siempre va a tener que cargar su
 * nombre manualmente en /{slug}/tomar — el flujo de la UI funciona igual.
 */
@Service
public class ConsultaDniServiceMock implements ConsultaDniService {

    @Override
    public Optional<String> buscarNombrePorDni(String dni) {
        // A propósito no devuelve datos: evita simular una fuente real de información personal.
        return Optional.empty();
    }
}
