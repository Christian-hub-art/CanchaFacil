package com.example.demo.Repositorios;

import com.example.demo.Entidades.Calificacion;

import java.util.List;

public interface CalificacionRepository extends CrudRepository<Calificacion> {

    List<Calificacion> findByEspacioId(Long espacioId);

    List<Calificacion> findByUsuarioId(Long usuarioId);

    Calificacion findByReservaId(Long reservaId);
}
