package com.turnero.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Representa a cada negocio/cliente que usa el turnero.
 * Todo lo personalizable (logo, colores, textos, prefijo) vive acá,
 * de modo que agregar una empresa nueva no requiere tocar código.
 */
@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador amigable usado en la URL, ej: /mi-cafeteria
    @NotBlank
    @Column(unique = true, nullable = false, length = 80)
    private String slug;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombre;

    // URL de la imagen del logo (puede ser externa o subida aparte)
    @Column(length = 500)
    private String logoUrl;

    // Colores en formato hex, ej: #2563eb
    @Column(length = 20)
    private String colorPrimario = "#2563eb";

    @Column(length = 20)
    private String colorSecundario = "#1e293b";

    // Prefijo que antecede al número, ej: "A" -> A-001
    @Column(length = 10)
    private String prefijo = "A";

    @Column(length = 300)
    private String mensajeBienvenida = "Bienvenido, tome su turno";

    // Idioma en el que se pronuncia el nombre del cliente al llamarlo (código BCP-47 para
    // el sintetizador de voz del navegador). Español (Argentina) es el valor por defecto.
    @Column(length = 15)
    private String idioma = "es-AR";

    // Si el número diario se reinicia a 1 cada día
    private boolean reinicioDiario = true;

    private boolean activo = true;

    public Empresa() {}

    public Empresa(String slug, String nombre) {
        this.slug = slug;
        this.nombre = nombre;
    }

    // --- getters y setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getColorPrimario() { return colorPrimario; }
    public void setColorPrimario(String colorPrimario) { this.colorPrimario = colorPrimario; }

    public String getColorSecundario() { return colorSecundario; }
    public void setColorSecundario(String colorSecundario) { this.colorSecundario = colorSecundario; }

    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }

    public String getMensajeBienvenida() { return mensajeBienvenida; }
    public void setMensajeBienvenida(String mensajeBienvenida) { this.mensajeBienvenida = mensajeBienvenida; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public boolean isReinicioDiario() { return reinicioDiario; }
    public void setReinicioDiario(boolean reinicioDiario) { this.reinicioDiario = reinicioDiario; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
