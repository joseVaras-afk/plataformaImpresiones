package com.impresiones.repository;

import com.impresiones.entity.Funcionario;
import com.impresiones.entity.SolicitudImpresion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.impresiones.entity.SolicitudImpresion;

public interface SolicitudImpresionRepository extends JpaRepository<SolicitudImpresion, Integer> {
    List<SolicitudImpresion> findByEstado(String estado);
    List<SolicitudImpresion> findByFuncionarioIdFuncionario(Integer idFuncionario);
    List<SolicitudImpresion> findByFuncionario(Funcionario funcionario);
    Optional<SolicitudImpresion> findById(Integer id);
    List<SolicitudImpresion> findAll();

}