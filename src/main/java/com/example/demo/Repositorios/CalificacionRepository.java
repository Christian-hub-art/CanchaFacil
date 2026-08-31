package com.example.demo.Repositorios;

import com.example.demo.Entidades.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findByEspacioId(Long espacioId);

    List<Calificacion> findByUsuarioId(Long usuarioId);

    Calificacion findByReservaId(Long reservaId);
}
