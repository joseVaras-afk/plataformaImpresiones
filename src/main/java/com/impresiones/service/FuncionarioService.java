package com.impresiones.service;

import com.impresiones.entity.Funcionario;
import java.util.List;

public interface FuncionarioService {
    List<Funcionario> listarFuncionarios();
    void guardar(Funcionario funcionario);
    void eliminar(Long id);
}
