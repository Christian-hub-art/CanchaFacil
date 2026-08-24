package com.reservasdeportivas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// lombok
// getters, setters, toString, equals
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    private UUID id;
    private Reserva reserva;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaPago;
    private String referencia;
}