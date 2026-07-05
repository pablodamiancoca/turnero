package com.turnero.controller;

import com.turnero.dto.ConsultaDniResponse;
import com.turnero.dto.TomarTurnoRequest;
import com.turnero.model.Turno;
import com.turnero.service.ColaService;
import com.turnero.service.ConsultaDniService;
import com.turnero.service.SseService;
import com.turnero.service.TurnoInfo;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/{slug}")
public class TurnoApiController {

    private final ColaService colaService;
    private final SseService sseService;
    private final ConsultaDniService consultaDniService;

    public TurnoApiController(ColaService colaService, SseService sseService, ConsultaDniService consultaDniService) {
        this.colaService = colaService;
        this.sseService = sseService;
        this.consultaDniService = consultaDniService;
    }

    // Busca el nombre asociado a un DNI antes de tomar el turno.
    // Si no se encuentra, el cliente carga el nombre a mano en la UI.
    @GetMapping("/dni/{dni}")
    public ConsultaDniResponse buscarPorDni(@PathVariable String dni) {
        return consultaDniService.buscarNombrePorDni(dni)
                .map(nombre -> new ConsultaDniResponse(dni, nombre, true))
                .orElse(new ConsultaDniResponse(dni, null, false));
    }

    // El cliente presiona "Tomar turno" al llegar, ya con el DNI y el nombre confirmado/corregido
    @PostMapping("/turnos")
    public ResponseEntity<Map<String, Object>> tomarTurno(@PathVariable String slug,
                                                            @RequestBody TomarTurnoRequest cuerpo) {
        Turno turno = colaService.tomarTurno(slug, cuerpo.dni(), cuerpo.nombre());
        long posicion = colaService.obtenerEstadoActual(slug).enEspera();
        return ResponseEntity.ok(Map.of(
                "id", turno.getId(),
                "etiqueta", turno.getEtiqueta(),
                "enEspera", posicion
        ));
    }

    // El receptor llama al siguiente cliente desde uno de los botones de box (BOX 1..BOX 5)
    @PostMapping("/siguiente")
    public TurnoInfo llamarSiguiente(@PathVariable String slug,
                                      @RequestParam(required = false, defaultValue = "") String ventanilla) {
        return colaService.llamarSiguiente(slug, ventanilla.isBlank() ? null : ventanilla);
    }

    // Estado actual (útil para polling simple o al cargar la pantalla)
    @GetMapping("/actual")
    public TurnoInfo obtenerActual(@PathVariable String slug) {
        return colaService.obtenerEstadoActual(slug);
    }

    // Canal en tiempo real: la pantalla pública se suscribe acá y recibe
    // un evento cada vez que cambia el turno que se está atendiendo.
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable String slug) {
        return sseService.suscribir(slug);
    }
}

