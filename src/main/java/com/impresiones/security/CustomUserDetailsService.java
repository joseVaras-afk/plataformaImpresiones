package com.impresiones.security;

import com.impresiones.entity.Funcionario;
import com.impresiones.repository.FuncionarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final FuncionarioRepository funcionarioRepository;

    public CustomUserDetailsService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Funcionario funcionario = funcionarioRepository.findByCorreoFuncionario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Convertimos el rol a una autoridad
        String rol = funcionario.getPerfilFuncionario();

        return new org.springframework.security.core.userdetails.User(
                funcionario.getNombreFuncionario(),
                funcionario.getContrasenaFuncionario(),
                Collections.singletonList(new SimpleGrantedAuthority(rol))
        );
    }
}
