package com.impresiones.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // ⚠️ Solo para desarrollo
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers("/profesor/**").hasAuthority("PROFESOR")
                        .requestMatchers("/operador/**").hasAuthority("OPERADOR")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/index", false) // respeta la URL original
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/login?logout"))
                .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }
}
