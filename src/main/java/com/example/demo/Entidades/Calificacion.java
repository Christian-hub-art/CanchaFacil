package com.example.demo.Entidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString(exclude = "reserva")
@EqualsAndHashCode(exclude = "reserva")
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {

    private Long id;
    private Usuario usuario;
    private Espacio espacio;
    private Reserva reserva;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fecha;
}
