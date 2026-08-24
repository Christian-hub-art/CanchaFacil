package com.example.demo.Entidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = "espacios")
@EqualsAndHashCode(exclude = "espacios")
@NoArgsConstructor
@AllArgsConstructor
public class Negocio {

    private Long id;
    private Usuario administrador;
    private String nombre;
    private String nit;
    private String direccion;
    private String descripcion;

    private List<Espacio> espacios = new ArrayList<>();
}
