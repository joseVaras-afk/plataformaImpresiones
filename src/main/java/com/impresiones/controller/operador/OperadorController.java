package com.impresiones.controller.operador;

import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.repository.SolicitudImpresionRepository;
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
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/operador")
public class OperadorController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private SolicitudImpresionService solicitudService;

    @Autowired
    private SolicitudImpresionRepository solicitudRepository;

    // ===== Helpers =====
    private Path resolverRuta(String valorBD) {
        // Acepta solo el nombre de archivo por seguridad
        String fileName = Paths.get(valorBD).getFileName().toString();
        return Paths.get(uploadDir).resolve(fileName).toAbsolutePath().normalize();
    }

    // ===== Vistas =====
    @GetMapping("/solicitudes")
    public String listarSolicitudes(Model model) {
        List<SolicitudImpresion> solicitudes = solicitudRepository.findAll();

        solicitudes.sort(
                Comparator.comparing((SolicitudImpresion s) -> !"APROBADO".equals(s.getEstado()))
                          .thenComparing(SolicitudImpresion::getFechaCreacion)
        );

        model.addAttribute("solicitudes", solicitudes);
        return "operador/solicitudes";
    }


    @GetMapping("/solicitudes/fragment")
    public String obtenerFragmentoSolicitudes(Model model) {
        List<SolicitudImpresion> solicitudes = solicitudService.listarTodasOrdenadas();
        model.addAttribute("solicitudes", solicitudes);
        return "solicitudes :: filas"; // Thymeleaf fragment
    }

    @GetMapping("")
    public String panelOperador() {
        return "operador/index";
    }

    // ===== Acciones =====
    @PostMapping("/marcarImpreso/{id}")
    @ResponseBody
    public String marcarImpreso(@PathVariable int id) {
        boolean ok = solicitudService.cambiarEstado(id, "IMPRESO", "");
        return ok ? "ok" : "error";
    }

    // ===== Archivos =====

    // Ver en navegador (PDF inline)
    @GetMapping("/verArchivo/{id}")
    public ResponseEntity<Resource> verArchivo(@PathVariable Integer id) throws Exception {
        SolicitudImpresion solicitud = solicitudRepository.findById(id).orElse(null);
        if (solicitud == null || solicitud.getRutaArchivo() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = resolverRuta(solicitud.getRutaArchivo());
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
    }

    // Descargar
    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable("id") int id) {
        var solicitud = solicitudService.obtenerPorId(id).orElse(null);
        if (solicitud == null || solicitud.getRutaArchivo() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = resolverRuta(solicitud.getRutaArchivo());
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        String fileName = path.getFileName().toString();
        String mime;
        try {
            mime = Files.probeContentType(path);
        } catch (Exception e) {
            mime = null;
        }
        if (mime == null) mime = "application/octet-stream";

        FileSystemResource resource = new FileSystemResource(path.toFile());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(mime))
                .body(resource);
    }

    // Imprimir (inline en navegador, PDF)
    @GetMapping("/imprimir/{id}")
    public ResponseEntity<Resource> imprimirArchivo(@PathVariable("id") int id) throws Exception {
        var solicitud = solicitudService.obtenerPorId(id).orElse(null);
        if (solicitud == null || solicitud.getRutaArchivo() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = resolverRuta(solicitud.getRutaArchivo());
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
    }
}
