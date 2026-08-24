package com.example.demo.Entidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Lombok: @Data genera getters, setters, toString, equals y hashCode.
// @NoArgsConstructor lo necesita Thymeleaf/Spring para crear el objeto del formulario.
@Data
@ToString(exclude = {"negocios", "reservas", "calificaciones", "notificaciones"})
@EqualsAndHashCode(exclude = {"negocios", "reservas", "calificaciones", "notificaciones"})
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private Rol rol;
    private LocalDateTime fechaRegistro;

    private List<Negocio> negocios = new ArrayList<>();
    private List<Reserva> reservas = new ArrayList<>();
    private List<Calificacion> calificaciones = new ArrayList<>();
    private List<Notificacion> notificaciones = new ArrayList<>();
}
