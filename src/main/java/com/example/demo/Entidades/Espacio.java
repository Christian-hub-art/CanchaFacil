package com.example.demo.Entidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"reservas", "calificaciones"})
@EqualsAndHashCode(exclude = {"reservas", "calificaciones"})
@NoArgsConstructor
@AllArgsConstructor
public class Espacio {

    private Long id;
    private Negocio negocio;
    private String nombre;
    private String tipoDeporte;
    private BigDecimal precioHora;
    private Integer capacidad;
    private String descripcion;

    private List<Reserva> reservas = new ArrayList<>();
    private List<Calificacion> calificaciones = new ArrayList<>();
}
