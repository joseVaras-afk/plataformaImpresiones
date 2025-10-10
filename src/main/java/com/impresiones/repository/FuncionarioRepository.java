package com.impresiones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.impresiones.entity.Funcionario;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    Optional<Funcionario> findByCorreoFuncionario(String correoFuncionario);
}
