package com.example.demo.Entidades;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Notificacion {

    private int id;
    private Usuario usuario;
    private String tipo;
    private String mensaje;
    private LocalDateTime fecha;
    private Boolean leido;

}