package com.impresiones.controller.Profesor;

import com.impresiones.entity.Asignatura;
import com.impresiones.entity.Funcionario;
import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.repository.AsignaturaRepository;
import com.impresiones.repository.SolicitudImpresionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/profesor")
public class ProfesorController {

    private final AsignaturaRepository asignaturaRepository;
    private final SolicitudImpresionRepository solicitudImpresionRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public ProfesorController(AsignaturaRepository asignaturaRepository,
                              SolicitudImpresionRepository solicitudImpresionRepository) {
        this.asignaturaRepository = asignaturaRepository;
        this.solicitudImpresionRepository = solicitudImpresionRepository;
    }

    @GetMapping("/nueva-solicitud")
    public String mostrarFormulario(Model model) {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        model.addAttribute("asignatura", asignaturas);
        model.addAttribute("solicitud", new SolicitudImpresion());
        return "profesor/solicitudes/nueva";
    }

    @PostMapping("/guardar-solicitud")
    public String guardarSolicitud(
            @ModelAttribute SolicitudImpresion solicitud,
            @RequestParam("archivo") MultipartFile archivo,
            HttpSession session,
            Model model) {

        try {
            // Obtener el profesor desde sesión (simulado)
            Funcionario profesor = (Funcionario) session.getAttribute("usuario");
            if (profesor == null) {
                model.addAttribute("error", "Debe iniciar sesión para crear solicitudes.");
                return "redirect:/login";
            }

            solicitud.setFuncionario(profesor);
            solicitud.setEstado("Pendiente");
            solicitud.setFechaImpresion(solicitud.getFechaImpresion());

            // Manejo del archivo
            if (!archivo.isEmpty()) {
                String nombreOriginal = archivo.getOriginalFilename();
                String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
                String nombreArchivo = UUID.randomUUID() + extension;
                File directorio = new File(uploadDir);
                if (!directorio.exists()) directorio.mkdirs();

                String rutaCompleta = uploadDir + File.separator + nombreArchivo;
                archivo.transferTo(new File(rutaCompleta));

                solicitud.setNombreArchivo(nombreOriginal);
                solicitud.setRutaArchivo(rutaCompleta);
            }

            solicitudImpresionRepository.save(solicitud);
            model.addAttribute("success", "Solicitud creada correctamente.");
        } catch (IOException e) {
            model.addAttribute("error", "Error al subir el archivo: " + e.getMessage());
        }

        return "redirect:/profesor/ver-solicitudes";
    }

    @GetMapping("/ver-solicitudes")
    public String verSolicitudes(Model model, HttpSession session) {
        Funcionario profesor = (Funcionario) session.getAttribute("usuario");
        List<SolicitudImpresion> solicitudes = solicitudImpresionRepository.findAll()
                .stream()
                .filter(s -> s.getFuncionario().getIdFuncionario().equals(profesor.getIdFuncionario()))
                .toList();

        model.addAttribute("solicitudes", solicitudes);
        return "profesor/ver-solicitudes";
    }
    
        @GetMapping
    public String homeProfesor() {
        return "redirect:/profesor/solicitudes";
    }
}
