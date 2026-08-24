package com.example.demo.Entidades;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Usuario {
    private int id;
    private String nombre;
    private String email;    
    
}