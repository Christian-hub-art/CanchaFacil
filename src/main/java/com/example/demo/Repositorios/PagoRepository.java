package com.example.demo.Repositorios;

import com.example.demo.Entidades.Pago;

import java.util.List;

public interface PagoRepository extends CrudRepository<Pago> {

    Pago findByReservaId(Long reservaId);

    Pago findByReferencia(String referencia);

    List<Pago> findByEstado(String estado);
}
