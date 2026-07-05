package com.turnero.service;

import com.turnero.model.Empresa;
import com.turnero.model.EstadoTurno;
import com.turnero.model.Turno;
import com.turnero.repository.EmpresaRepository;
import com.turnero.repository.TurnoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ColaService {

    private final EmpresaRepository empresaRepository;
    private final TurnoRepository turnoRepository;
    private final SseService sseService;

    public ColaService(EmpresaRepository empresaRepository, TurnoRepository turnoRepository, SseService sseService) {
        this.empresaRepository = empresaRepository;
        this.turnoRepository = turnoRepository;
        this.sseService = sseService;
    }

    public Empresa buscarEmpresa(String slug) {
        return empresaRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("No existe una empresa con slug: " + slug));
    }

    /** El cliente toma un nuevo número al llegar al local, con su DNI y nombre ya confirmado/corregido. */
    @Transactional
    public Turno tomarTurno(String slug, String dni, String nombre) {
        Empresa empresa = buscarEmpresa(slug);
        LocalDate hoy = LocalDate.now();

        int siguienteNumero;
        if (empresa.isReinicioDiario()) {
            // el número vuelve a 1 cada día
            siguienteNumero = turnoRepository.findFirstByEmpresaAndFechaOrderByNumeroDesc(empresa, hoy)
                    .map(t -> t.getNumero() + 1).orElse(1);
        } else {
            // el número sigue incrementando sin importar el día
            siguienteNumero = turnoRepository.findFirstByEmpresaOrderByNumeroDesc(empresa)
                    .map(t -> t.getNumero() + 1).orElse(1);
        }

        Turno turno = new Turno();
        turno.setEmpresa(empresa);
        turno.setNumero(siguienteNumero);
        turno.setPrefijo(empresa.getPrefijo());
        turno.setDni(dni);
        turno.setNombreCliente(nombre);
        turno.setEstado(EstadoTurno.ESPERANDO);
        turno.setFecha(hoy);
        turno.setCreadoEn(LocalDateTime.now());

        Turno guardado = turnoRepository.save(turno);
        sseService.notificarCambio(slug, obtenerEstadoActual(slug));
        return guardado;
    }

    /** El receptor llama al siguiente cliente en la fila. */
    @Transactional
    public TurnoInfo llamarSiguiente(String slug, String ventanilla) {
        Empresa empresa = buscarEmpresa(slug);

        // marca como atendido el que estaba en llamada
        turnoRepository.findFirstByEmpresaAndEstadoOrderByCreadoEnDesc(empresa, EstadoTurno.LLAMANDO)
                .ifPresent(actual -> {
                    actual.setEstado(EstadoTurno.ATENDIDO);
                    actual.setAtendidoEn(LocalDateTime.now());
                    turnoRepository.save(actual);
                });

        // toma el próximo en espera (el más antiguo)
        Turno siguiente = turnoRepository
                .findFirstByEmpresaAndEstadoOrderByCreadoEnAsc(empresa, EstadoTurno.ESPERANDO)
                .orElse(null);

        if (siguiente != null) {
            siguiente.setEstado(EstadoTurno.LLAMANDO);
            siguiente.setLlamadoEn(LocalDateTime.now());
            siguiente.setVentanilla(ventanilla);
            turnoRepository.save(siguiente);
        }

        TurnoInfo estado = obtenerEstadoActual(slug);
        sseService.notificarCambio(slug, estado);
        return estado;
    }

    /** Consulta cuál es el turno que se está atendiendo ahora mismo, más cuántos esperan. */
    @Transactional(readOnly = true)
    public TurnoInfo obtenerEstadoActual(String slug) {
        Empresa empresa = buscarEmpresa(slug);
        LocalDate hoy = LocalDate.now();

        Turno actual = turnoRepository
                .findFirstByEmpresaAndEstadoOrderByCreadoEnDesc(empresa, EstadoTurno.LLAMANDO)
                .orElse(null);

        long enEspera = turnoRepository.countByEmpresaAndFechaAndEstado(empresa, hoy, EstadoTurno.ESPERANDO);

        if (actual == null) {
            return new TurnoInfo(null, "--", "SIN_TURNOS", null, (int) enEspera, null, null);
        }
        return new TurnoInfo(actual.getId(), actual.getEtiqueta(), actual.getEstado().name(),
                actual.getVentanilla(), (int) enEspera, actual.getDni(), actual.getNombreCliente());
    }
}
