package com.impresiones.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Página de login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // templates/login.html
    }

    // Redirección genérica según el rol del usuario
    @GetMapping("/index")
    public String redirigirSegunRol(Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        for (GrantedAuthority authority : auth.getAuthorities()) {
            String rol = authority.getAuthority();
            switch (rol) {
                case "ADMINISTRADOR":
                    return "redirect:/admin/index";
                case "PROFESOR":
                    return "redirect:/profesor/index";
                case "OPERADOR":
                    return "redirect:/operador/index";
            }
        }

        // Si no tiene un rol válido, vuelve al login
        return "redirect:/login?error=sin_rol";
    }

    // Puedes mantener estos mappings si quieres rutas directas
    @GetMapping("/admin/index")
    public String adminIndex() {
        return "index";
    }

    @GetMapping("/profesor/index")
    public String profesorIndex() {
        return "index";
    }

    @GetMapping("/operador/index")
    public String operadorIndex() {
        return "index";
    }
}
