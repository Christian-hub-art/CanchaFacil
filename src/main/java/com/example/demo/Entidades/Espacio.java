package com.example.demo.Entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "espacios")
@Data
@ToString(exclude = {"negocio", "reservas", "calificaciones"})
@EqualsAndHashCode(exclude = {"negocio", "reservas", "calificaciones"})
@NoArgsConstructor
@AllArgsConstructor
public class Espacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "tipo_deporte", length = 60)
    private String tipoDeporte;

    // precision/scale = NUMERIC(10,2) en PostgreSQL: dinero sin errores de redondeo.
    @Column(name = "precio_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioHora;

    private Integer capacidad;

    @Column(length = 500)
    private String descripcion;

    @OneToMany(mappedBy = "espacio", cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "espacio", cascade = CascadeType.ALL)
    private List<Calificacion> calificaciones = new ArrayList<>();
}
