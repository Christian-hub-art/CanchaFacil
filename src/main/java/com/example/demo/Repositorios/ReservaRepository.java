package com.example.demo.Repositorios;

import com.example.demo.Entidades.Reserva;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends CrudRepository<Reserva> {

    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findByEspacioId(Long espacioId);

    List<Reserva> findByEstado(String estado);

    List<Reserva> findByEspacioIdAndFecha(Long espacioId, LocalDate fecha);
}
