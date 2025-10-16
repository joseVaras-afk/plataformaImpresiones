package com.impresiones.controller.Profesor;

import com.impresiones.entity.Asignatura;
import com.impresiones.entity.Funcionario;
import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.repository.AsignaturaRepository;
import com.impresiones.repository.SolicitudImpresionRepository;
import com.impresiones.repository.FuncionarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/profesor")
public class ProfesorController {

    private final AsignaturaRepository asignaturaRepository;
    private final SolicitudImpresionRepository solicitudImpresionRepository;
    private final FuncionarioRepository funcionarioRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public ProfesorController(AsignaturaRepository asignaturaRepository,
                              SolicitudImpresionRepository solicitudImpresionRepository, FuncionarioRepository funcionarioRepository) {
        this.asignaturaRepository = asignaturaRepository;
        this.funcionarioRepository = funcionarioRepository; 
        this.solicitudImpresionRepository = solicitudImpresionRepository;
    }




    @GetMapping("/ver_solicitud")
    public String verSolicitudes(Model model, HttpSession session, @AuthenticationPrincipal User user) {
        String correo = user.getUsername();
        Funcionario func = funcionarioRepository.findByCorreoFuncionario(correo)
                .orElseThrow(() -> new RuntimeException("Funcionario no encontrado: " + correo));
        List<SolicitudImpresion> solicitudes = solicitudImpresionRepository.findAll();
        model.addAttribute("solicitudes", solicitudes);
        return "profesor/solicitudes";
    }
    
       @GetMapping("/Profesor")
    public String homeProfesor() {
        return "redirect:/profesor/index";
    } 
    @GetMapping("")
    public String panelAdmin() {
        return "profesor/index";
    }
}
