package com.example.demo.Servicios;

import com.example.demo.Entidades.Espacio;
import com.example.demo.Entidades.Reserva;
import com.example.demo.Entidades.Usuario;
import com.example.demo.Repositorios.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservaService {

    /** Estados posibles de una reserva. */
    public static final String PENDIENTE = "PENDIENTE";
    public static final String CONFIRMADA = "CONFIRMADA";
    public static final String CANCELADA = "CANCELADA";
    public static final String COMPLETADA = "COMPLETADA";

    private final ReservaRepository reservaRepository;
    private final UsuarioService usuarioService;
    private final EspacioService espacioService;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioService usuarioService,
                          EspacioService espacioService) {
        this.reservaRepository = reservaRepository;
        this.usuarioService = usuarioService;
        this.espacioService = espacioService;
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }

    public List<Reserva> listarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    public List<Reserva> listarPorEspacio(Long espacioId) {
        return reservaRepository.findByEspacioId(espacioId);
    }

    public List<Reserva> listarPorEstado(String estado) {
        return reservaRepository.findByEstadoIgnoreCase(estado);
    }

    /**
     * Reglas de negocio de una reserva:
     * 1. La hora de inicio debe ser anterior a la hora de fin.
     * 2. El espacio no puede tener otra reserva activa que se cruce con ese horario.
     */
    @Transactional
    public Reserva guardar(Reserva reserva, Long usuarioId, Long espacioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario con id " + usuarioId + " no existe");
        }
        Espacio espacio = espacioService.buscarPorId(espacioId);
        if (espacio == null) {
            throw new IllegalArgumentException("El espacio con id " + espacioId + " no existe");
        }

        if (reserva.getHoraInicio() == null || reserva.getHoraFin() == null
                || !reserva.getHoraInicio().isBefore(reserva.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }

        if (hayCruce(espacioId, reserva)) {
            throw new IllegalArgumentException("El espacio ya esta reservado en ese horario");
        }

        Reserva actual = reserva.getId() == null ? null : buscarPorId(reserva.getId());
        if (actual == null) {
            reserva.setUsuario(usuario);
            reserva.setEspacio(espacio);
            reserva.setFechaCreacion(LocalDateTime.now());
            reserva.setEstado(PENDIENTE);
            return reservaRepository.save(reserva);
        }

        // Edicion: fecha de creacion, estado, pago y calificacion se conservan
        // porque se escribe sobre la fila que ya existe.
        actual.setUsuario(usuario);
        actual.setEspacio(espacio);
        actual.setFecha(reserva.getFecha());
        actual.setHoraInicio(reserva.getHoraInicio());
        actual.setHoraFin(reserva.getHoraFin());
        return reservaRepository.save(actual);
    }

    @Transactional
    public Reserva cambiarEstado(Long id, String estado) {
        Reserva reserva = buscarPorId(id);
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva con id " + id + " no existe");
        }
        reserva.setEstado(estado);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva cancelar(Long id) {
        return cambiarEstado(id, CANCELADA);
    }

    @Transactional
    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    /**
     * Devuelve true si el espacio ya tiene una reserva no cancelada cuyo horario
     * se cruza con el de la reserva recibida. Se ignora la propia reserva cuando
     * se esta editando.
     */
    private boolean hayCruce(Long espacioId, Reserva reserva) {
        LocalDate fecha = reserva.getFecha();
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de la reserva es obligatoria");
        }

        return reservaRepository.findByEspacioIdAndFecha(espacioId, fecha).stream()
                .filter(otra -> !otra.getId().equals(reserva.getId()))
                .filter(otra -> !CANCELADA.equalsIgnoreCase(otra.getEstado()))
                .anyMatch(otra -> otra.getHoraInicio().isBefore(reserva.getHoraFin())
                        && otra.getHoraFin().isAfter(reserva.getHoraInicio()));
    }
}
