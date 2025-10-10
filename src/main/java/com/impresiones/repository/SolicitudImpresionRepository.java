package com.impresiones.repository;

import com.impresiones.entity.SolicitudImpresion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.impresiones.entity.SolicitudImpresion;

public interface SolicitudImpresionRepository extends JpaRepository<SolicitudImpresion, Integer> {
    List<SolicitudImpresion> findByEstado(String estado);
    List<SolicitudImpresion> findByFuncionarioIdFuncionario(Integer idFuncionario);
}