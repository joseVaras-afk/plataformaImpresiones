package com.impresiones.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // archivo login.html en templates
    }

    @GetMapping("/admin/index")
    public String mostrarInicio() {
        return "index"; // página principal administrador
    }

        @GetMapping("/profesor/index")
    public String mostrarInicioProfesor() {
        return "index"; // página principal PROFESOR
    }
}
