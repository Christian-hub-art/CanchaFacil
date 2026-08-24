package com.reservasdeportivas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

// lombok
// getters, setters, toString, equals
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    private UUID id;
    private Usuario usuario;
    private Espacio espacio;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private LocalDateTime fechaCreacion;
    private Pago pago;
    private Calificacion calificacion;
}