package com.example.demo.Servicios;

import com.example.demo.Entidades.Espacio;
import com.example.demo.Entidades.Negocio;
import com.example.demo.Repositorios.EspacioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EspacioService {

    private final EspacioRepository espacioRepository;
    private final NegocioService negocioService;

    @Autowired
    public EspacioService(EspacioRepository espacioRepository, NegocioService negocioService) {
        this.espacioRepository = espacioRepository;
        this.negocioService = negocioService;
    }

    public List<Espacio> listar() {
        return espacioRepository.findAll();
    }

    public Espacio buscarPorId(Long id) {
        return espacioRepository.findById(id);
    }

    public List<Espacio> listarPorNegocio(Long negocioId) {
        return espacioRepository.findByNegocioId(negocioId);
    }

    public List<Espacio> buscarPorDeporte(String tipoDeporte) {
        return espacioRepository.findByTipoDeporte(tipoDeporte);
    }

    /**
     * Regla de negocio: el precio por hora no puede ser negativo y el espacio
     * siempre pertenece a un negocio existente.
     */
    public Espacio guardar(Espacio espacio, Long negocioId) {
        if (espacio.getPrecioHora() == null || espacio.getPrecioHora().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio por hora no puede ser negativo");
        }

        Negocio negocio = negocioService.buscarPorId(negocioId);
        if (negocio == null) {
            throw new IllegalArgumentException("El negocio con id " + negocioId + " no existe");
        }
        espacio.setNegocio(negocio);

        if (espacio.getId() != null) {
            Espacio actual = espacioRepository.findById(espacio.getId());
            if (actual != null) {
                espacio.setReservas(actual.getReservas());
                espacio.setCalificaciones(actual.getCalificaciones());
            }
        }
        return espacioRepository.save(espacio);
    }

    public void eliminar(Long id) {
        espacioRepository.deleteById(id);
    }
}
