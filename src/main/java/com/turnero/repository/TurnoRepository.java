package com.turnero.repository;

import com.turnero.model.Empresa;
import com.turnero.model.EstadoTurno;
import com.turnero.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    Optional<Turno> findFirstByEmpresaAndEstadoOrderByCreadoEnDesc(Empresa empresa, EstadoTurno estado);

    Optional<Turno> findFirstByEmpresaAndEstadoOrderByCreadoEnAsc(Empresa empresa, EstadoTurno estado);

    List<Turno> findByEmpresaAndFechaOrderByCreadoEnAsc(Empresa empresa, LocalDate fecha);

    Optional<Turno> findFirstByEmpresaAndFechaOrderByNumeroDesc(Empresa empresa, LocalDate fecha);

    Optional<Turno> findFirstByEmpresaOrderByNumeroDesc(Empresa empresa);

    long countByEmpresaAndFechaAndEstado(Empresa empresa, LocalDate fecha, EstadoTurno estado);
}
