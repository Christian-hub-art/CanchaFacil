package com.example.demo.Repositorios;

import java.util.List;

/**
 * Contrato comun de la capa de repositorio.
 *
 * Una interfaz define QUE metodos deben existir; la clase que la implementa
 * escribe COMO se hacen. Hoy los datos viven en memoria (HashMap); si manana
 * se conecta una base de datos solo cambia la implementacion, no el servicio
 * ni el controlador.
 *
 * @param <T> tipo de la entidad administrada
 */
public interface CrudRepository<T> {

    List<T> findAll();

    T findById(Long id);

    T save(T entidad);

    void deleteById(Long id);

    boolean existsById(Long id);
}
