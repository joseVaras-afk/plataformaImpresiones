package com.impresiones.controller.Profesor;

import com.impresiones.entity.*;
import com.impresiones.repository.*;
import com.impresiones.service.AsignaturaService;
import com.impresiones.service.SolicitudImpresionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profesor/solicitudes")
public class ProfesorSolicitudController {

    private final AsignaturaRepository asignaturaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final SolicitudImpresionService solicitudService;
    private final AsignaturaService asignaturaService;

    @Value("${file.upload-dir:${user.home}/impresiones-uploads}")
    private String uploadDir;

    public ProfesorSolicitudController(AsignaturaRepository asignaturaRepository,
                                       FuncionarioRepository funcionarioRepository,
                                       SolicitudImpresionService solicitudService,
                                       AsignaturaService asignaturaService) {
        this.asignaturaRepository = asignaturaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.solicitudService = solicitudService;
        this.asignaturaService = asignaturaService;
    }

    // Listar solicitudes propias
    @GetMapping
    public String listarSolicitudes(Model model, @AuthenticationPrincipal User user) {
        String correo = user.getUsername();
        Funcionario func = funcionarioRepository.findByCorreoFuncionario(correo)
                .orElseThrow(() -> new RuntimeException("Funcionario no encontrado: " + correo));

        List<SolicitudImpresion> solicitudes = solicitudService.listarPorFuncionario(func.getIdFuncionario());
        model.addAttribute("solicitudes", solicitudes);
        return "profesor/solicitudes"; // ruta de la vista
    }

    // Formulario nueva solicitud
    @GetMapping("/nueva")
    public String nuevaSolicitudForm(Model model) {
        model.addAttribute("solicitud", new SolicitudImpresion());
        model.addAttribute("asignaturas", asignaturaService.listarTodas());
        model.addAttribute("cursos", List.of("1°A","1°B","1°C","2°A","2°B","2°C","3°A","3°B","3°C","4°A","4°B","4°C","5°A","5°B","5°C","6°A","6°B","6°C","7°A","7°B","7°C","8°A","8°B","8°C")); // vacío hasta seleccionar asignatura
        return "profesor/solicitudes/nueva";
    }


    // Guardar solicitud con archivo
    @PostMapping("/guardar")
    public String guardarSolicitud(@ModelAttribute SolicitudImpresion solicitud,
                                  @RequestParam("archivo") MultipartFile archivo,
                                  @RequestParam("asignaturaId") Integer asignaturaId,
                                  @RequestParam(value="cursoId", required=false) Integer cursoId,
                                  @AuthenticationPrincipal User user,
                                  Model model) throws IOException {

        // Asignar funcionario actual
        String correo = user.getUsername();
        Funcionario funcionario = funcionarioRepository.findByCorreoFuncionario(correo)
                .orElseThrow(() -> new RuntimeException("Funcionario no encontrado: " + correo));
        solicitud.setFuncionario(funcionario);

        // Fecha de creación
        solicitud.setFechaCreacion(LocalDateTime.now());

        // Asignatura
        asignaturaRepository.findById(asignaturaId).ifPresent(solicitud::setAsignatura);

    

        // Guardar archivo si existe
        if (archivo != null && !archivo.isEmpty()) {
            String original = StringUtils.cleanPath(archivo.getOriginalFilename());
            String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")) : "";
            String filename = System.currentTimeMillis() + "-" + original;
            Path targetPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(targetPath);
            Path filePath = targetPath.resolve(filename);
            Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            solicitud.setNombreArchivo(original);
            solicitud.setRutaArchivo(filePath.toString());
        }

        solicitud.setEstado("PENDIENTE");
        solicitudService.guardar(solicitud);
        return "redirect:/profesor/solicitudes";
    }

    // Ver detalle
    @GetMapping("/ver/{id}")
    public String verDetalle(@PathVariable Integer id, Model model, @AuthenticationPrincipal User user) {
        Optional<SolicitudImpresion> sOpt = solicitudService.obtenerPorId(id);
        if (sOpt.isEmpty()) {
            return "redirect:/profesor/solicitudes";
        }
        SolicitudImpresion s = sOpt.get();
        model.addAttribute("solicitud", s);
        return "profesor/ver_solicitud";
    }
}
