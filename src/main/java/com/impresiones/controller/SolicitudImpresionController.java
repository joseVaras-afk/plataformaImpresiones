package com.impresiones.controller;

import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.entity.Asignatura;
import com.impresiones.repository.AsignaturaRepository;
import com.impresiones.service.SolicitudImpresionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudImpresionController {

    @Autowired
    private SolicitudImpresionService solicitudService;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    // Carpeta donde se guardarán los archivos
    private final String UPLOAD_DIR = System.getProperty("user.home") + "/impresiones-uploads/";

    // ===== FORMULARIO NUEVA SOLICITUD =====
    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        model.addAttribute("asignaturas", asignaturas);
        model.addAttribute("solicitud", new SolicitudImpresion());
        return "formularioSolicitud";
    }

    @PostMapping("/guardar")
    public String guardarSolicitud(@ModelAttribute SolicitudImpresion solicitud,
                                   @RequestParam("archivo") MultipartFile archivo,
                                   Model model) {

        // Guardar archivo
        if (!archivo.isEmpty()) {
            try {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String nombreArchivo = archivo.getOriginalFilename();
                Path rutaArchivo = Paths.get(UPLOAD_DIR, nombreArchivo);
                archivo.transferTo(rutaArchivo);

                solicitud.setNombreArchivo(nombreArchivo);
                solicitud.setRutaArchivo(rutaArchivo.toString());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("mensaje", "Error al subir archivo");
                return "formularioSolicitud";
            }
        }

        // Fecha actual como referencia (puede cambiar según requerimiento)
        solicitud.setFechaImpresion(LocalDateTime.now());

        // Guardar solicitud con estado PENDIENTE
        solicitudService.crearSolicitud(solicitud);
        return "redirect:/solicitudes/mis-solicitudes";
    }

    // ===== LISTA DE SOLICITUDES DEL PROFESOR =====
    @GetMapping("/mis-solicitudes")
    public String listarMisSolicitudes(@RequestParam("idFuncionario") Integer idFuncionario, Model model) {
        List<SolicitudImpresion> solicitudes = solicitudService.listarPorFuncionario(idFuncionario);
        model.addAttribute("solicitudes", solicitudes);
        return "misSolicitudes";
    }

    // ===== LISTA DE SOLICITUDES PENDIENTES (JEFE) =====
    @GetMapping("/pendientes")
    public String listarPendientes(Model model) {
        List<SolicitudImpresion> solicitudes = solicitudService.listarPorEstado("PENDIENTE");
        model.addAttribute("solicitudes", solicitudes);
        return "pendientes";
    }

    // ===== APROBAR SOLICITUD =====
    @PostMapping("/aprobar/{id}")
    public String aprobarSolicitud(@PathVariable Integer id) {
        solicitudService.aprobarSolicitud(id);
        return "redirect:/solicitudes/pendientes";
    }

    // ===== RECHAZAR SOLICITUD =====
    @PostMapping("/rechazar/{id}")
    public String rechazarSolicitud(@PathVariable Integer id, @RequestParam("motivo") String motivo) {
        solicitudService.rechazarSolicitud(id, motivo);
        return "redirect:/solicitudes/pendientes";
    }

    // ===== LISTA DE SOLICITUDES APROBADAS (ENCARGADO) =====
    @GetMapping("/aprobadas")
    public String listarAprobadas(Model model) {
        List<SolicitudImpresion> solicitudes = solicitudService.listarPorEstado("APROBADO");
        model.addAttribute("solicitudes", solicitudes);
        return "aprobadas";
    }

    // ===== MARCAR IMPRESO =====
    @PostMapping("/marcar-impreso/{id}")
    public String marcarImpreso(@PathVariable Integer id) {
        solicitudService.marcarImpreso(id);
        return "redirect:/solicitudes/aprobadas";
    }

    // ===== DESCARGAR ARCHIVO =====
    @GetMapping("/descargar/{id}")
    @ResponseBody
    public byte[] descargarArchivo(@PathVariable Integer id) throws IOException {
        SolicitudImpresion solicitud = solicitudService.listarPorEstado("APROBADO")
                .stream().filter(s -> s.getIdSolicitudImpresion().equals(id)).findFirst().orElse(null);

        if (solicitud != null && solicitud.getRutaArchivo() != null) {
            Path path = Paths.get(solicitud.getRutaArchivo());
            return Files.readAllBytes(path);
        }
        return null;
    }
}
