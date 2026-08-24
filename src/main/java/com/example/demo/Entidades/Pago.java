package com.example.demo.Entidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString(exclude = "reserva")
@EqualsAndHashCode(exclude = "reserva")
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    private Long id;
    private Reserva reserva;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaPago;
    private String referencia;
}
