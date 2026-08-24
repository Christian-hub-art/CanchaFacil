package com.example.demo.Servicios;

import com.example.demo.Entidades.Pago;
import com.example.demo.Entidades.Reserva;
import com.example.demo.Repositorios.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    /** Estados posibles de un pago. */
    public static final String PENDIENTE = "PENDIENTE";
    public static final String APROBADO = "APROBADO";
    public static final String RECHAZADO = "RECHAZADO";
    public static final String REEMBOLSADO = "REEMBOLSADO";

    private final PagoRepository pagoRepository;
    private final ReservaService reservaService;

    @Autowired
    public PagoService(PagoRepository pagoRepository, ReservaService reservaService) {
        this.pagoRepository = pagoRepository;
        this.reservaService = reservaService;
    }

    public List<Pago> listar() {
        return pagoRepository.findAll();
    }

    public Pago buscarPorId(Long id) {
        return pagoRepository.findById(id);
    }

    public Pago buscarPorReserva(Long reservaId) {
        return pagoRepository.findByReservaId(reservaId);
    }

    public List<Pago> listarPorEstado(String estado) {
        return pagoRepository.findByEstado(estado);
    }

    /**
     * Reglas de negocio: el monto no puede ser negativo y una reserva solo puede
     * tener un pago asociado.
     */
    public Pago guardar(Pago pago, Long reservaId) {
        if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto del pago no puede ser negativo");
        }

        Reserva reserva = reservaService.buscarPorId(reservaId);
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva con id " + reservaId + " no existe");
        }

        Pago existente = pagoRepository.findByReservaId(reservaId);
        if (existente != null && !existente.getId().equals(pago.getId())) {
            throw new IllegalArgumentException("La reserva " + reservaId + " ya tiene un pago registrado");
        }

        pago.setReserva(reserva);

        if (pago.getId() == null) {
            pago.setFechaPago(LocalDateTime.now());
            pago.setEstado(PENDIENTE);
        } else {
            Pago actual = pagoRepository.findById(pago.getId());
            if (actual != null) {
                pago.setFechaPago(actual.getFechaPago());
                pago.setEstado(actual.getEstado());
            }
        }

        Pago guardado = pagoRepository.save(pago);
        reserva.setPago(guardado);
        return guardado;
    }

    /**
     * Al aprobar el pago la reserva queda confirmada; si se rechaza o se
     * reembolsa, la reserva se cancela.
     */
    public Pago cambiarEstado(Long id, String estado) {
        Pago pago = pagoRepository.findById(id);
        if (pago == null) {
            throw new IllegalArgumentException("El pago con id " + id + " no existe");
        }
        pago.setEstado(estado);
        pagoRepository.save(pago);

        if (pago.getReserva() != null) {
            if (APROBADO.equalsIgnoreCase(estado)) {
                reservaService.cambiarEstado(pago.getReserva().getId(), ReservaService.CONFIRMADA);
            } else if (RECHAZADO.equalsIgnoreCase(estado) || REEMBOLSADO.equalsIgnoreCase(estado)) {
                reservaService.cambiarEstado(pago.getReserva().getId(), ReservaService.CANCELADA);
            }
        }
        return pago;
    }

    public void eliminar(Long id) {
        Pago pago = pagoRepository.findById(id);
        if (pago != null && pago.getReserva() != null) {
            pago.getReserva().setPago(null);
        }
        pagoRepository.deleteById(id);
    }
}
