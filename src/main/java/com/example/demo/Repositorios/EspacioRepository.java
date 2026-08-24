package com.example.demo.Repositorios;

import com.example.demo.Entidades.Espacio;

import java.util.List;

public interface EspacioRepository extends CrudRepository<Espacio> {

    List<Espacio> findByNegocioId(Long negocioId);

    List<Espacio> findByTipoDeporte(String tipoDeporte);
}
