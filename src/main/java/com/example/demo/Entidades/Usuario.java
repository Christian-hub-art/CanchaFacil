package com.example.demo.Entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Lombok: @Data genera getters, setters, toString, equals y hashCode.
// @NoArgsConstructor lo necesita Thymeleaf/Spring (y tambien JPA) para crear el objeto vacio.
//
// JPA: @Entity marca la clase como tabla; @Table le pone el nombre.
// Las asociaciones se excluyen de toString/equals para no provocar recursion
// infinita entre padre e hijo ni disparar cargas perezosas sin querer.
@Entity
@Table(name = "usuarios")
@Data
@ToString(exclude = {"negocios", "reservas", "calificaciones", "notificaciones"})
@EqualsAndHashCode(exclude = {"negocios", "reservas", "calificaciones", "notificaciones"})
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    // IDENTITY = la secuencia la genera PostgreSQL con una columna BIGSERIAL.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    // El email es unico: la regla de negocio del servicio queda respaldada por la BD.
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(length = 30)
    private String telefono;

    // STRING guarda "CLIENTE"/"ADMINISTRADOR" en vez de 0/1: si manana se agrega
    // un rol en medio del enum, los datos ya guardados siguen siendo correctos.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // mappedBy = el dueno de la relacion es el campo "administrador" de Negocio,
    // o sea la columna administrador_id vive en la tabla negocios.
    @OneToMany(mappedBy = "administrador", cascade = CascadeType.ALL)
    private List<Negocio> negocios = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Calificacion> calificaciones = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Notificacion> notificaciones = new ArrayList<>();
}
