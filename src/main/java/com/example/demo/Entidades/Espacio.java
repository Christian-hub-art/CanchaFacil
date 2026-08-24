package com.reservasdeportivas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// lombok

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Espacio {

    private UUID id;
    private Negocio negocio;
    private String nombre;
    private String tipoDeporte;
    private BigDecimal precioHora;
    private Integer capacidad;
    private String descripcion;
    private List<Reserva> reservas;
    private List<Calificacion> calificaciones;
}