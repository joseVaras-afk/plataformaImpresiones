package com.impresiones.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // ⚠️ No cifra las contraseñas — solo para desarrollo
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 🔧 Desactiva CSRF solo si es necesario (útil para desarrollo)
            .authorizeHttpRequests(auth -> auth
                // Solo el ADMINISTRADOR puede acceder a rutas /admin/**
                .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")
                // El PROFESOR puede acceder a rutas /profesor/**
                .requestMatchers("/profesor/**").hasAuthority("PROFESOR")
                // Recursos públicos (CSS, JS, imágenes, etc.)
                .requestMatchers("/login", "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login") // asegura que Spring maneje el POST del formulario
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/admin", true)
                .failureUrl("/login?error") // si las credenciales fallan
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/403") // Página de error personalizada
            );

        return http.build();
    }
}
