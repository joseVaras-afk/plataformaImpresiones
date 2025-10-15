package com.impresiones.repository;

import com.impresiones.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    Optional<Funcionario> findByCorreoFuncionario(String correoFuncionario);
    Optional<Funcionario> findByNombreFuncionario(String nombreFuncionario);
    Optional<Funcionario> findById(Integer idFuncionario);
}
