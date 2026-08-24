package com.example.demo.Repositorios;

import com.example.demo.Entidades.Reserva;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReservaRepositoryImpl implements ReservaRepository {

    private final Map<Long, Reserva> datos = new LinkedHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public List<Reserva> findAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Reserva findById(Long id) {
        return datos.get(id);
    }

    @Override
    public Reserva save(Reserva reserva) {
        if (reserva.getId() == null) {
            reserva.setId(secuencia.incrementAndGet());
        }
        datos.put(reserva.getId(), reserva);
        return reserva;
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
    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return datos.values().stream()
                .filter(r -> r.getUsuario() != null && usuarioId.equals(r.getUsuario().getId()))
                .toList();
    }

    @Override
    public List<Reserva> findByEspacioId(Long espacioId) {
        return datos.values().stream()
                .filter(r -> r.getEspacio() != null && espacioId.equals(r.getEspacio().getId()))
                .toList();
    }

    @Override
    public List<Reserva> findByEstado(String estado) {
        return datos.values().stream()
                .filter(r -> r.getEstado() != null && r.getEstado().equalsIgnoreCase(estado))
                .toList();
    }

    @Override
    public List<Reserva> findByEspacioIdAndFecha(Long espacioId, LocalDate fecha) {
        return datos.values().stream()
                .filter(r -> r.getEspacio() != null && espacioId.equals(r.getEspacio().getId()))
                .filter(r -> fecha.equals(r.getFecha()))
                .toList();
    }
}
