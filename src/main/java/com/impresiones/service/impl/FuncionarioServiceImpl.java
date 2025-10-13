package com.impresiones.service.impl;

import com.impresiones.entity.Funcionario;
import com.impresiones.repository.FuncionarioRepository;
import com.impresiones.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.findAll();
    }

    @Override
    public void guardar(Funcionario funcionario) {
        funcionarioRepository.save(funcionario);
    }

    @Override
    public void eliminar(Long id) {
        funcionarioRepository.deleteById(id);
    }
}

