package com.example.demo.Repositorios;

import com.example.demo.Entidades.Pago;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PagoRepositoryImpl implements PagoRepository {

    private final Map<Long, Pago> datos = new LinkedHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public List<Pago> findAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Pago findById(Long id) {
        return datos.get(id);
    }

    @Override
    public Pago save(Pago pago) {
        if (pago.getId() == null) {
            pago.setId(secuencia.incrementAndGet());
        }
        datos.put(pago.getId(), pago);
        return pago;
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
    public Pago findByReservaId(Long reservaId) {
        return datos.values().stream()
                .filter(p -> p.getReserva() != null && reservaId.equals(p.getReserva().getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Pago findByReferencia(String referencia) {
        return datos.values().stream()
                .filter(p -> p.getReferencia() != null && p.getReferencia().equalsIgnoreCase(referencia))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Pago> findByEstado(String estado) {
        return datos.values().stream()
                .filter(p -> p.getEstado() != null && p.getEstado().equalsIgnoreCase(estado))
                .toList();
    }
}
