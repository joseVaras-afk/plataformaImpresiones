
package com.impresiones.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.impresiones.entity.Asignatura;


public interface AsignaturaRepository extends JpaRepository<Asignatura, Integer> { 
    List<Asignatura> findAll();
    Optional<Asignatura> findById(Integer idAsignatura);
    Optional<Asignatura> findByNombreAsignatura(String nombreAsignatura);
}

