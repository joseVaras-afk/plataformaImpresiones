package com.impresiones.service;

import com.impresiones.entity.Funcionario;
import com.impresiones.repository.FuncionarioRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final FuncionarioRepository funcionarioRepository;

    public CustomUserDetailsService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Funcionario funcionario = funcionarioRepository.findByCorreoFuncionario(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(funcionario.getCorreoFuncionario())
                .password(funcionario.getContrasenaFuncionario())
                .roles(funcionario.getPerfilFuncionario()) // PROFESOR, JEFE, ENCARGADO
                .build();
    }
}
