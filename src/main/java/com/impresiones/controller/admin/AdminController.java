package com.impresiones.controller.admin;

import com.impresiones.entity.Funcionario;
import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.service.FuncionarioService;
import com.impresiones.service.SolicitudImpresionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private SolicitudImpresionService solicitudService;

    // ========= Helpers =========
    private Path resolverRuta(String valorBD) {
        // Por seguridad, quédate solo con el nombre
        String fileName = Paths.get(valorBD).getFileName().toString();
        return Paths.get(uploadDir).resolve(fileName).toAbsolutePath().normalize();
    }

    // ========= Vistas generales =========
    @GetMapping("")
    public String panelAdmin() {
        return "admin/index";
    }

    @GetMapping("/admin") // opcional: alias
    public String adminHome() {
        return "admin/index";
    }

    // ========= FUNCIONARIOS =========
    @GetMapping("/funcionarios")
    public String listarFuncionarios(Model model) {
        List<Funcionario> funcionarios = funcionarioService.listarFuncionarios();
        model.addAttribute("funcionarios", funcionarios);
        return "admin/funcionarios";
    }

    @GetMapping("/funcionarios/nuevo")
    public String nuevoFuncionario(Model model) {
        model.addAttribute("funcionario", new Funcionario());
        return "admin/form_funcionario";
    }

    @PostMapping("/funcionarios/guardar")
    public String guardarFuncionario(@ModelAttribute Funcionario funcionario) {
        funcionarioService.guardar(funcionario);
        return "redirect:/admin/funcionarios";
    }

    @GetMapping("/funcionarios/eliminar/{id}")
    public String eliminarFuncionario(@PathVariable("id") int id) {
        funcionarioService.eliminar(id);
        return "redirect:/admin/funcionarios";
    }

    // ========= SOLICITUDES =========
    @GetMapping("/solicitudes")
    public String listarSolicitudes(Model model) {
        List<SolicitudImpresion> solicitudes = solicitudService.listarTodasOrdenadas();
        model.addAttribute("solicitudes", solicitudes);
        return "admin/solicitudes";
    }

    // Fragmento para refrescar tabla por Ajax (¡sin /admin extra!)
    @GetMapping("/solicitudes/fragment")
    public String obtenerFragmentoSolicitudes(Model model) {
        List<SolicitudImpresion> solicitudes = solicitudService.listarTodasOrdenadas();
        model.addAttribute("solicitudes", solicitudes);
        return "solicitudes :: filas";
    }

    // JSON opcional (¡sin /admin extra!)
    @GetMapping("/solicitudes/json")
    @ResponseBody
    public List<SolicitudImpresion> listarSolicitudesJson() {
        return solicitudService.listarTodasOrdenadas();
    }

    // Cambiar estado
    @PostMapping("/cambiarEstado/{id}")
    @ResponseBody
    public String cambiarEstado(@PathVariable int id,
                                @RequestParam String estado,
                                @RequestParam(required = false) String motivo) {
        boolean actualizado = solicitudService.cambiarEstado(id, estado, motivo);
        return actualizado ? "ok" : "error";
    }

    // Rechazar (muestra formulario con estado ya en RECHAZADO)
    @GetMapping("/solicitudes/rechazar/{id}")
    public String rechazarSolicitud(@PathVariable("id") int id, Model model) {
        SolicitudImpresion solicitud = solicitudService.obtenerPorId(id).orElse(null);
        if (solicitud != null) {
            solicitud.setEstado("RECHAZADO");
            model.addAttribute("solicitud", solicitud);
        }
        return "admin/form_solicitud";
    }

    @PostMapping("/solicitudes/actualizar")
    public String actualizarSolicitud(@ModelAttribute SolicitudImpresion solicitud) {
        solicitudService.actualizarSolicitud(solicitud);
        return "redirect:/admin/solicitudes";
    }

    // ========= Archivos =========

    // Descargar
    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable("id") int id) {
        var solicitud = solicitudService.obtenerPorId(id).orElse(null);
        if (solicitud == null || solicitud.getRutaArchivo() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = resolverRuta(solicitud.getRutaArchivo());
        try {
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            String fileName = path.getFileName().toString();
            String mime = Files.probeContentType(path);
            if (mime == null) mime = "application/octet-stream";

            FileSystemResource resource = new FileSystemResource(path.toFile());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType(mime))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Ver/Imprimir (inline)
    @GetMapping("/imprimir/{id}")
    public ResponseEntity<Resource> imprimirArchivo(@PathVariable("id") int id) {
        var solicitud = solicitudService.obtenerPorId(id).orElse(null);
        if (solicitud == null || solicitud.getRutaArchivo() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = resolverRuta(solicitud.getRutaArchivo());
        try {
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            Resource recurso = new UrlResource(path.toUri());
            String fileName = path.getFileName().toString();
            String mime = Files.probeContentType(path);
            if (mime == null) mime = "application/pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType(mime))
                    .body(recurso);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
