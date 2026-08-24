package com.example.demo.Repositorios;

import com.example.demo.Entidades.Rol;
import com.example.demo.Entidades.Usuario;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementacion en memoria del contrato {@link UsuarioRepository}.
 * @Repository le dice a Spring que administre esta clase como bean de acceso a datos.
 */
@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final Map<Long, Usuario> datos = new LinkedHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Usuario findById(Long id) {
        return datos.get(id);
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(secuencia.incrementAndGet());
        }
        datos.put(usuario.getId(), usuario);
        return usuario;
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
    public Usuario findByEmail(String email) {
        return datos.values().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Usuario> findByRol(Rol rol) {
        return datos.values().stream()
                .filter(u -> u.getRol() == rol)
                .toList();
    }

    @Override
    public List<Usuario> findByNombre(String nombre) {
        return datos.values().stream()
                .filter(u -> u.getNombre() != null
                        && u.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }
}
