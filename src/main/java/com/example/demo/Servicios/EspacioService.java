package com.example.demo.Servicios;

import com.example.demo.Entidades.Espacio;
import com.example.demo.Entidades.Negocio;
import com.example.demo.Repositorios.EspacioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
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
        return espacioRepository.findById(id).orElse(null);
    }

    public List<Espacio> listarPorNegocio(Long negocioId) {
        return espacioRepository.findByNegocioId(negocioId);
    }

    public List<Espacio> buscarPorDeporte(String tipoDeporte) {
        return espacioRepository.findByTipoDeporteIgnoreCase(tipoDeporte);
    }

    /**
     * Regla de negocio: el precio por hora no puede ser negativo y el espacio
     * siempre pertenece a un negocio existente.
     */
    @Transactional
    public Espacio guardar(Espacio espacio, Long negocioId) {
        if (espacio.getPrecioHora() == null || espacio.getPrecioHora().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio por hora no puede ser negativo");
        }

        Negocio negocio = negocioService.buscarPorId(negocioId);
        if (negocio == null) {
            throw new IllegalArgumentException("El negocio con id " + negocioId + " no existe");
        }

        Espacio actual = espacio.getId() == null ? null : buscarPorId(espacio.getId());
        if (actual == null) {
            espacio.setNegocio(negocio);
            return espacioRepository.save(espacio);
        }

        // Edicion: reservas y calificaciones ya guardadas se conservan solas.
        actual.setNegocio(negocio);
        actual.setNombre(espacio.getNombre());
        actual.setTipoDeporte(espacio.getTipoDeporte());
        actual.setPrecioHora(espacio.getPrecioHora());
        actual.setCapacidad(espacio.getCapacidad());
        actual.setDescripcion(espacio.getDescripcion());
        return espacioRepository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        espacioRepository.deleteById(id);
    }
}
