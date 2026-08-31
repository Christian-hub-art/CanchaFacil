package com.example.demo.Servicios;

import com.example.demo.Entidades.Negocio;
import com.example.demo.Entidades.Usuario;
import com.example.demo.Repositorios.NegocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NegocioService {

    private final NegocioRepository negocioRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public NegocioService(NegocioRepository negocioRepository, UsuarioService usuarioService) {
        this.negocioRepository = negocioRepository;
        this.usuarioService = usuarioService;
    }

    public List<Negocio> listar() {
        return negocioRepository.findAll();
    }

    public Negocio buscarPorId(Long id) {
        return negocioRepository.findById(id).orElse(null);
    }

    public List<Negocio> buscarPorNombre(String nombre) {
        return negocioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Negocio> listarPorAdministrador(Long administradorId) {
        return negocioRepository.findByAdministradorId(administradorId);
    }

    /**
     * Regla de negocio: el NIT es unico y todo negocio debe tener un administrador
     * que exista realmente.
     */
    @Transactional
    public Negocio guardar(Negocio negocio, Long administradorId) {
        Negocio conEseNit = negocioRepository.findByNitIgnoreCase(negocio.getNit());
        if (conEseNit != null && !conEseNit.getId().equals(negocio.getId())) {
            throw new IllegalArgumentException("Ya existe un negocio con el NIT " + negocio.getNit());
        }

        Usuario administrador = usuarioService.buscarPorId(administradorId);
        if (administrador == null) {
            throw new IllegalArgumentException("El administrador con id " + administradorId + " no existe");
        }

        Negocio actual = negocio.getId() == null ? null : buscarPorId(negocio.getId());
        if (actual == null) {
            negocio.setAdministrador(administrador);
            return negocioRepository.save(negocio);
        }

        // Edicion: se actualiza la fila que ya existe y su lista de espacios queda intacta.
        actual.setAdministrador(administrador);
        actual.setNombre(negocio.getNombre());
        actual.setNit(negocio.getNit());
        actual.setDireccion(negocio.getDireccion());
        actual.setDescripcion(negocio.getDescripcion());
        return negocioRepository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        negocioRepository.deleteById(id);
    }
}
