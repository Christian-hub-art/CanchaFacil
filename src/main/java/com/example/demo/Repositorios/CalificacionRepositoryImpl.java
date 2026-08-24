package com.example.demo.Repositorios;

import com.example.demo.Entidades.Calificacion;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CalificacionRepositoryImpl implements CalificacionRepository {

    private final Map<Long, Calificacion> datos = new LinkedHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public List<Calificacion> findAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Calificacion findById(Long id) {
        return datos.get(id);
    }

    @Override
    public Calificacion save(Calificacion calificacion) {
        if (calificacion.getId() == null) {
            calificacion.setId(secuencia.incrementAndGet());
        }
        datos.put(calificacion.getId(), calificacion);
        return calificacion;
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
    public List<Calificacion> findByEspacioId(Long espacioId) {
        return datos.values().stream()
                .filter(c -> c.getEspacio() != null && espacioId.equals(c.getEspacio().getId()))
                .toList();
    }

    @Override
    public List<Calificacion> findByUsuarioId(Long usuarioId) {
        return datos.values().stream()
                .filter(c -> c.getUsuario() != null && usuarioId.equals(c.getUsuario().getId()))
                .toList();
    }

    @Override
    public Calificacion findByReservaId(Long reservaId) {
        return datos.values().stream()
                .filter(c -> c.getReserva() != null && reservaId.equals(c.getReserva().getId()))
                .findFirst()
                .orElse(null);
    }
}
