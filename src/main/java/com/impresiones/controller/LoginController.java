package com.impresiones.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // archivo login.html en templates
    }

    @GetMapping("/inicio")
    public String mostrarInicio() {
        return "inicio"; // página principal tras iniciar sesión
    }
}
