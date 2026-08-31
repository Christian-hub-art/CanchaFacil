package com.example.demo.Servicios;

import com.example.demo.Entidades.Calificacion;
import com.example.demo.Entidades.Reserva;
import com.example.demo.Repositorios.CalificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final ReservaService reservaService;

    @Autowired
    public CalificacionService(CalificacionRepository calificacionRepository, ReservaService reservaService) {
        this.calificacionRepository = calificacionRepository;
        this.reservaService = reservaService;
    }

    public List<Calificacion> listar() {
        return calificacionRepository.findAll();
    }

    public Calificacion buscarPorId(Long id) {
        return calificacionRepository.findById(id).orElse(null);
    }

    public List<Calificacion> listarPorEspacio(Long espacioId) {
        return calificacionRepository.findByEspacioId(espacioId);
    }

    public List<Calificacion> listarPorUsuario(Long usuarioId) {
        return calificacionRepository.findByUsuarioId(usuarioId);
    }

    /** Promedio de puntuacion de un espacio; 0 si todavia no tiene calificaciones. */
    public double promedioPorEspacio(Long espacioId) {
        return calificacionRepository.findByEspacioId(espacioId).stream()
                .filter(c -> c.getPuntuacion() != null)
                .mapToInt(Calificacion::getPuntuacion)
                .average()
                .orElse(0.0);
    }

    /**
     * Reglas de negocio: la puntuacion va de 1 a 5 y cada reserva se califica
     * una sola vez. La calificacion hereda el usuario y el espacio de la reserva.
     */
    @Transactional
    public Calificacion guardar(Calificacion calificacion, Long reservaId) {
        if (calificacion.getPuntuacion() == null
                || calificacion.getPuntuacion() < 1 || calificacion.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuacion debe estar entre 1 y 5");
        }

        Reserva reserva = reservaService.buscarPorId(reservaId);
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva con id " + reservaId + " no existe");
        }

        Calificacion existente = calificacionRepository.findByReservaId(reservaId);
        if (existente != null && !existente.getId().equals(calificacion.getId())) {
            throw new IllegalArgumentException("La reserva " + reservaId + " ya fue calificada");
        }

        Calificacion actual = calificacion.getId() == null ? null : buscarPorId(calificacion.getId());
        if (actual == null) {
            calificacion.setReserva(reserva);
            calificacion.setUsuario(reserva.getUsuario());
            calificacion.setEspacio(reserva.getEspacio());
            calificacion.setFecha(LocalDateTime.now());
            Calificacion guardada = calificacionRepository.save(calificacion);
            reserva.setCalificacion(guardada);
            return guardada;
        }

        // Edicion: la fecha original se conserva porque se escribe sobre la fila existente.
        actual.setReserva(reserva);
        actual.setUsuario(reserva.getUsuario());
        actual.setEspacio(reserva.getEspacio());
        actual.setPuntuacion(calificacion.getPuntuacion());
        actual.setComentario(calificacion.getComentario());
        Calificacion guardada = calificacionRepository.save(actual);
        reserva.setCalificacion(guardada);
        return guardada;
    }

    @Transactional
    public void eliminar(Long id) {
        Calificacion calificacion = buscarPorId(id);
        if (calificacion == null) {
            return;
        }
        if (calificacion.getReserva() != null) {
            calificacion.getReserva().setCalificacion(null);
        }
        calificacionRepository.delete(calificacion);
    }
}
