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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profesor/solicitudes")
public class ProfesorSolicitudController {

    private final AsignaturaRepository asignaturaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final SolicitudImpresionService solicitudService;
    private final AsignaturaService asignaturaService;

   private final String uploadDir = System.getProperty("user.dir") + File.separator + "Impresiones-uploads";
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
    public String mostrarFormulario(Model model) {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        model.addAttribute("asignatura", asignaturas);
        model.addAttribute("solicitud", new SolicitudImpresion());
        return "profesor/solicitudes/nueva";
    }

    // Guardar solicitud con archivo
    @PostMapping("/guardar")
    public String guardarSolicitud(@ModelAttribute SolicitudImpresion solicitud,
                                  @RequestParam("archivo") MultipartFile archivo,
                                  @RequestParam("curso") String curso,
                                  @RequestParam("asignatura") Integer asignatura,
                                  @RequestParam("fechaImpresion") String fechaImpresion,
                                  @RequestParam("cantidadCopias") Integer cantidadCopias,
                                  @AuthenticationPrincipal User user,
                                  Model model,
                                  RedirectAttributes redirectAttrs) throws IOException {

        // Asignar funcionario actual
        String correo = user.getUsername();
        Funcionario funcionario = funcionarioRepository.findByCorreoFuncionario(correo)
                .orElseThrow(() -> new RuntimeException("Funcionario no encontrado: " + correo));
        solicitud.setFuncionario(funcionario);

        // Fecha de creación
        solicitud.setFechaCreacion(LocalDate.now());

        // Fecha de impresión
        solicitud.setFechaImpresion(LocalDate.parse(fechaImpresion));

        // Asignatura
        Asignatura a = asignaturaRepository.findById(asignatura)
                .orElseThrow(() -> new RuntimeException("Asignatura no encontrada: " + asignatura));
        solicitud.setAsignatura(a);
        // Curso
        solicitud.setCurso(curso);
        
        // Cantidad de copias
        solicitud.setCantidadCopias(cantidadCopias);

        // Guardar archivo si existe
        if (archivo != null && !archivo.isEmpty()) {
            String original = StringUtils.cleanPath(archivo.getOriginalFilename());
            String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")) : "";
            String filename = System.currentTimeMillis() + "-" + original;
            Path targetPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(targetPath);
            Path filePath = targetPath.resolve(filename);
            Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Archivo recibido: " + archivo.getOriginalFilename());

            //Nombre Archivo
            solicitud.setNombreArchivo(original);
            //Ruta Archivo
            solicitud.setRutaArchivo("impresiones-uploads/" + filename);
        }
        //estado
        solicitud.setEstado("PENDIENTE");
        //Motivo Rechazo
        solicitud.setMotivoRechazo("");
        solicitudService.guardar(solicitud);
        redirectAttrs.addFlashAttribute("mensaje", "✅ Solicitud creada con éxito");
        redirectAttrs.addFlashAttribute("tipo", "exito");
        return "redirect:/profesor/solicitudes/nueva";
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
