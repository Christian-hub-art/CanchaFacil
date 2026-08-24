package com.example.demo.Repositorios;

import com.example.demo.Entidades.Espacio;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EspacioRepositoryImpl implements EspacioRepository {

    private final Map<Long, Espacio> datos = new LinkedHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public List<Espacio> findAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Espacio findById(Long id) {
        return datos.get(id);
    }

    @Override
    public Espacio save(Espacio espacio) {
        if (espacio.getId() == null) {
            espacio.setId(secuencia.incrementAndGet());
        }
        datos.put(espacio.getId(), espacio);
        return espacio;
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
    public List<Espacio> findByNegocioId(Long negocioId) {
        return datos.values().stream()
                .filter(e -> e.getNegocio() != null && negocioId.equals(e.getNegocio().getId()))
                .toList();
    }

    @Override
    public List<Espacio> findByTipoDeporte(String tipoDeporte) {
        return datos.values().stream()
                .filter(e -> e.getTipoDeporte() != null
                        && e.getTipoDeporte().equalsIgnoreCase(tipoDeporte))
                .toList();
    }
}
