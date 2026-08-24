package com.example.demo.Servicios;

import com.example.demo.Entidades.Rol;
import com.example.demo.Entidades.Usuario;
import com.example.demo.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Capa de servicio: aqui viven las reglas de negocio de los usuarios.
 * El controlador llama al servicio y el servicio llama al repositorio.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyeccion de dependencias por constructor: Spring entrega el repositorio.
    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    public List<Usuario> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    /**
     * Regla de negocio: el email no se puede repetir y la fecha de registro
     * se asigna sola la primera vez que se guarda el usuario.
     */
    public Usuario guardar(Usuario usuario) {
        Usuario conEseEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if (conEseEmail != null && !conEseEmail.getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email " + usuario.getEmail());
        }

        if (usuario.getId() == null) {
            usuario.setFechaRegistro(LocalDateTime.now());
        } else {
            Usuario actual = usuarioRepository.findById(usuario.getId());
            if (actual != null) {
                usuario.setFechaRegistro(actual.getFechaRegistro());
                usuario.setNegocios(actual.getNegocios());
                usuario.setReservas(actual.getReservas());
                usuario.setCalificaciones(actual.getCalificaciones());
                usuario.setNotificaciones(actual.getNotificaciones());
            }
        }
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}
