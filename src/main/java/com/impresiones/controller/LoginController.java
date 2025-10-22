package com.impresiones.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.impresiones.entity.Funcionario;
import com.impresiones.repository.FuncionarioRepository;
import org.springframework.ui.Model;
import com.impresiones.entity.Funcionario;

@Controller
public class LoginController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

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
        return "redirect:/login?error=sin_rol";
    }

    // ✅ Método común para agregar nombreFuncionario
    private void agregarNombreFuncionario(Authentication authentication, Model model) {
        if (authentication != null) {
            String email = authentication.getName();
            Funcionario funcionario = funcionarioRepository.findByCorreoFuncionario(email).orElse(null);
            model.addAttribute("nombreFuncionario",
                    funcionario != null ? funcionario.getNombreFuncionario() : email);
        }
    }

    @GetMapping("/admin/index")
    public String adminIndex(Authentication authentication, Model model) {
        agregarNombreFuncionario(authentication, model);
        return "index";
    }

    @GetMapping("/profesor/index")
    public String profesorIndex(Authentication authentication, Model model) {
        agregarNombreFuncionario(authentication, model);
        return "index";
    }

    @GetMapping("/operador/index")
    public String operadorIndex(Authentication authentication, Model model) {
        agregarNombreFuncionario(authentication, model);
        return "index";
    }
}
