package com.example.demo.Repositorios;

import com.example.demo.Entidades.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findByEspacioId(Long espacioId);

    List<Reserva> findByEstadoIgnoreCase(String estado);

    /** Las reservas de un espacio en un dia concreto: base del control de cruces. */
    List<Reserva> findByEspacioIdAndFecha(Long espacioId, LocalDate fecha);
}
