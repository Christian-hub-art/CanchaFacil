package com.example.demo.Repositorios;

import com.example.demo.Entidades.Negocio;

import java.util.List;

public interface NegocioRepository extends CrudRepository<Negocio> {

    Negocio findByNit(String nit);

    List<Negocio> findByAdministradorId(Long administradorId);

    List<Negocio> findByNombre(String nombre);
}
