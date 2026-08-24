package com.example.demo.Entidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    private Long id;
    private Usuario usuario;
    private String tipo;
    private String mensaje;
    private LocalDateTime fecha;
    private Boolean leido;
}
