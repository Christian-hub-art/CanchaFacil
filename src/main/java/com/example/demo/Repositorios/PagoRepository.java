package com.example.demo.Repositorios;

import com.example.demo.Entidades.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Pago findByReservaId(Long reservaId);

    Pago findByReferenciaIgnoreCase(String referencia);

    List<Pago> findByEstadoIgnoreCase(String estado);
}
