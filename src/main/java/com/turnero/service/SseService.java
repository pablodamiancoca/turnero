package com.turnero.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mantiene, por cada empresa (slug), la lista de "suscriptores" (pantallas)
 * abiertos vía Server-Sent Events, y permite avisarles cuando cambia el turno actual.
 */
@Service
public class SseService {

    private final Map<String, List<SseEmitter>> emittersPorEmpresa = new ConcurrentHashMap<>();

    public SseEmitter suscribir(String slug) {
        SseEmitter emitter = new SseEmitter(0L); // sin timeout
        List<SseEmitter> lista = emittersPorEmpresa.computeIfAbsent(slug, k -> new CopyOnWriteArrayList<>());
        lista.add(emitter);

        emitter.onCompletion(() -> lista.remove(emitter));
        emitter.onTimeout(() -> lista.remove(emitter));
        emitter.onError(e -> lista.remove(emitter));

        return emitter;
    }

    public void notificarCambio(String slug, Object datos) {
        List<SseEmitter> lista = emittersPorEmpresa.getOrDefault(slug, List.of());
        for (SseEmitter emitter : lista) {
            try {
                emitter.send(SseEmitter.event().name("turno-actualizado").data(datos));
            } catch (IOException e) {
                emitter.complete();
                lista.remove(emitter);
            }
        }
    }
}
