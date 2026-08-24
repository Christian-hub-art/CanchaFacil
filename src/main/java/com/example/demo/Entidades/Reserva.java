package com.example.demo.Entidades;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    private Long id;
    private Usuario usuario;
    private Espacio espacio;
    // Convierte el valor de <input type="date"> (2026-08-24) a LocalDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;
    // Convierte el valor de <input type="time"> (18:00) a LocalTime
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime horaInicio;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime horaFin;
    private String estado;
    private LocalDateTime fechaCreacion;
    private Pago pago;
    private Calificacion calificacion;
}
