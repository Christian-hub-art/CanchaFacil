package com.example.demo.Entidades;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Negocio {
    private int id;
    private Usuario administrador;
    private String nombre;
    private String nit;
    private String direccion;
    private String descripcion;
    private List<Espacio> espacios;

}