package com.example.demo.Repositorios;

import com.example.demo.Entidades.Notificacion;

import java.util.List;

public interface NotificacionRepository extends CrudRepository<Notificacion> {

    List<Notificacion> findByUsuarioId(Long usuarioId);

    List<Notificacion> findByUsuarioIdYNoLeidas(Long usuarioId);
}
