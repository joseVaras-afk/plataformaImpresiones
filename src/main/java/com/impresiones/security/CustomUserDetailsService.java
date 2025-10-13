package com.impresiones.security;

import com.impresiones.entity.Funcionario;
import com.impresiones.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Funcionario funcionario = funcionarioRepository.findByCorreoFuncionario(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(funcionario.getCorreoFuncionario())
                .password(funcionario.getContrasenaFuncionario())
                .roles(funcionario.getPerfilFuncionario()) // ejemplo: ADMINISTRADOR o PROFESOR
                .build();
    }
}

