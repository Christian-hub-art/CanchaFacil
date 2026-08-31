package com.example.demo.Servicios;

import com.example.demo.Entidades.Rol;
import com.example.demo.Entidades.Usuario;
import com.example.demo.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Capa de servicio: aqui viven las reglas de negocio de los usuarios.
 * El controlador llama al servicio y el servicio llama al repositorio.
 *
 * @Transactional(readOnly = true) a nivel de clase: todas las consultas viajan en
 * una transaccion de solo lectura; los metodos que escriben lo sobreescriben con
 * su propio @Transactional.
 */
@Service
@Transactional(readOnly = true)
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

    /**
     * JpaRepository.findById devuelve Optional, que obliga a pensar en el caso
     * "no existe". Aqui se traduce a null para no cambiar el contrato que ya
     * usan los controladores.
     */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email);
    }

    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Usuario> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    /**
     * Regla de negocio: el email no se puede repetir y la fecha de registro
     * se asigna sola la primera vez que se guarda el usuario.
     *
     * En la edicion se copian los campos sobre la entidad que ya esta en la BD
     * (la "gestionada" por JPA) en vez de guardar el objeto suelto del formulario.
     * Asi se conservan la fecha de registro y las listas de negocios, reservas,
     * calificaciones y notificaciones sin tocarlas.
     */
    @Transactional
    public Usuario guardar(Usuario usuario) {
        Usuario conEseEmail = usuarioRepository.findByEmailIgnoreCase(usuario.getEmail());
        if (conEseEmail != null && !conEseEmail.getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email " + usuario.getEmail());
        }

        Usuario actual = usuario.getId() == null ? null : buscarPorId(usuario.getId());
        if (actual == null) {
            usuario.setFechaRegistro(LocalDateTime.now());
            return usuarioRepository.save(usuario);
        }

        actual.setNombre(usuario.getNombre());
        actual.setEmail(usuario.getEmail());
        actual.setPassword(usuario.getPassword());
        actual.setTelefono(usuario.getTelefono());
        actual.setRol(usuario.getRol());
        return usuarioRepository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}
