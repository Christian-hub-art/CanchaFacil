package com.example.demo.Repositorios;

import com.example.demo.Entidades.Notificacion;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class NotificacionRepositoryImpl implements NotificacionRepository {

    private final Map<Long, Notificacion> datos = new LinkedHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public List<Notificacion> findAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Notificacion findById(Long id) {
        return datos.get(id);
    }

    @Override
    public Notificacion save(Notificacion notificacion) {
        if (notificacion.getId() == null) {
            notificacion.setId(secuencia.incrementAndGet());
        }
        datos.put(notificacion.getId(), notificacion);
        return notificacion;
    }

    @Override
    public void deleteById(Long id) {
        datos.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return datos.containsKey(id);
    }

    @Override
    public List<Notificacion> findByUsuarioId(Long usuarioId) {
        return datos.values().stream()
                .filter(n -> n.getUsuario() != null && usuarioId.equals(n.getUsuario().getId()))
                .toList();
    }

    @Override
    public List<Notificacion> findByUsuarioIdYNoLeidas(Long usuarioId) {
        return findByUsuarioId(usuarioId).stream()
                .filter(n -> !Boolean.TRUE.equals(n.getLeido()))
                .toList();
    }
}
