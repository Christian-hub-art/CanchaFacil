package com.example.demo.Servicios;

import com.example.demo.Entidades.Notificacion;
import com.example.demo.Entidades.Usuario;
import com.example.demo.Repositorios.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public NotificacionService(NotificacionRepository notificacionRepository, UsuarioService usuarioService) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioService = usuarioService;
    }

    public List<Notificacion> listar() {
        return notificacionRepository.findAll();
    }

    public Notificacion buscarPorId(Long id) {
        return notificacionRepository.findById(id);
    }

    public List<Notificacion> listarPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioId(usuarioId);
    }

    public List<Notificacion> listarNoLeidas(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdYNoLeidas(usuarioId);
    }

    /** Toda notificacion nace no leida y con la fecha del momento en que se crea. */
    public Notificacion guardar(Notificacion notificacion, Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario con id " + usuarioId + " no existe");
        }
        notificacion.setUsuario(usuario);

        if (notificacion.getId() == null) {
            notificacion.setFecha(LocalDateTime.now());
            notificacion.setLeido(false);
        } else {
            Notificacion actual = notificacionRepository.findById(notificacion.getId());
            if (actual != null) {
                notificacion.setFecha(actual.getFecha());
                notificacion.setLeido(actual.getLeido());
            }
        }
        return notificacionRepository.save(notificacion);
    }

    public Notificacion marcarComoLeida(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id);
        if (notificacion == null) {
            throw new IllegalArgumentException("La notificacion con id " + id + " no existe");
        }
        notificacion.setLeido(true);
        return notificacionRepository.save(notificacion);
    }

    public void eliminar(Long id) {
        notificacionRepository.deleteById(id);
    }
}
