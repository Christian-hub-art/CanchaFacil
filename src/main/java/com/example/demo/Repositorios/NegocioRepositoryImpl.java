package com.example.demo.Repositorios;

import com.example.demo.Entidades.Negocio;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class NegocioRepositoryImpl implements NegocioRepository {

    private final Map<Long, Negocio> datos = new LinkedHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public List<Negocio> findAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Negocio findById(Long id) {
        return datos.get(id);
    }

    @Override
    public Negocio save(Negocio negocio) {
        if (negocio.getId() == null) {
            negocio.setId(secuencia.incrementAndGet());
        }
        datos.put(negocio.getId(), negocio);
        return negocio;
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
    public Negocio findByNit(String nit) {
        return datos.values().stream()
                .filter(n -> n.getNit() != null && n.getNit().equalsIgnoreCase(nit))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Negocio> findByAdministradorId(Long administradorId) {
        return datos.values().stream()
                .filter(n -> n.getAdministrador() != null
                        && administradorId.equals(n.getAdministrador().getId()))
                .toList();
    }

    @Override
    public List<Negocio> findByNombre(String nombre) {
        return datos.values().stream()
                .filter(n -> n.getNombre() != null
                        && n.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }
}
