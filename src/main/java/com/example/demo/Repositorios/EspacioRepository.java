package com.example.demo.Repositorios;

import com.example.demo.Entidades.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Long> {

    List<Espacio> findByNegocioId(Long negocioId);

    List<Espacio> findByTipoDeporteIgnoreCase(String tipoDeporte);
}
