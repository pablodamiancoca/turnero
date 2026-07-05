package com.turnero.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private Integer numero;

    @Column(length = 10)
    private String prefijo;

    @Column(length = 20)
    private String dni;

    @Column(length = 150)
    private String nombreCliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTurno estado = EstadoTurno.ESPERANDO;

    @Column(nullable = false)
    private LocalDate fecha = LocalDate.now();

    @Column(nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime llamadoEn;

    private LocalDateTime atendidoEn;

    // Identifica quién/qué ventanilla o receptor llamó al cliente (opcional)
    @Column(length = 80)
    private String ventanilla;

    public Turno() {}

    public String getEtiqueta() {
        return prefijo + "-" + String.format("%03d", numero);
    }

    // --- getters y setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public EstadoTurno getEstado() { return estado; }
    public void setEstado(EstadoTurno estado) { this.estado = estado; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getLlamadoEn() { return llamadoEn; }
    public void setLlamadoEn(LocalDateTime llamadoEn) { this.llamadoEn = llamadoEn; }

    public LocalDateTime getAtendidoEn() { return atendidoEn; }
    public void setAtendidoEn(LocalDateTime atendidoEn) { this.atendidoEn = atendidoEn; }

    public String getVentanilla() { return ventanilla; }
    public void setVentanilla(String ventanilla) { this.ventanilla = ventanilla; }
}
