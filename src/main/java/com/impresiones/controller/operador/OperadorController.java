package com.impresiones.controller.operador;

import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.service.SolicitudImpresionService;

import jakarta.annotation.Resource;
import java.nio.file.Path;

import com.impresiones.service.EmailService;
import com.impresiones.repository.SolicitudImpresionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequestMapping("/operador")
public class OperadorController {

    @Autowired
    private SolicitudImpresionService solicitudService;
    @Autowired
    private SolicitudImpresionRepository solicitudRepository;

    // Mostrar todas las solicitudes
    @GetMapping("/solicitudes")
   public String listarSolicitudes(Model model) {
    List<SolicitudImpresion> solicitudes = solicitudRepository.findAll();

    solicitudes.sort(Comparator
        .comparing((SolicitudImpresion s) -> !"APROBADO".equals(s.getEstado())) // Pendientes primero
        .thenComparing(SolicitudImpresion::getFechaCreacion));

    model.addAttribute("solicitudes", solicitudes);
    return "operador/solicitudes";
}

@GetMapping("/operador/solicitudes/fragment")
public String obtenerFragmentoSolicitudes(Model model) {
    List<SolicitudImpresion> solicitudes = solicitudService.listarTodasOrdenadas();
    model.addAttribute("solicitudes", solicitudes);
    return "solicitudes :: filas"; // Thymeleaf fragment
}

@GetMapping("/operador/verArchivo/{id}")
public ResponseEntity<Resource> verArchivo(@PathVariable Integer id) throws IOException {
    SolicitudImpresion solicitud = solicitudRepository.findById(id).orElse(null);
    Path pathArchivo = Paths.get(solicitud.getRutaArchivo());

    if (!Files.exists(pathArchivo)) {
        return ResponseEntity.notFound().build();
    }

    Resource recurso = (Resource) new UrlResource(pathArchivo.toUri());

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF) // fuerza a abrir como PDF
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + ((FileSystemResource) recurso).getFilename() + "\"")
            .body(recurso);
}


    // Cambiar estado de la solicitud
    @PostMapping("/marcarImpreso/{id}")
    @ResponseBody
    public String marcarImpreso(@PathVariable int id) {
        String estado = "IMPRESO";
        String motivo = "";
        boolean actualizado = solicitudService.cambiarEstado(id, estado, motivo);
        return actualizado ? "ok" : "error";
    }


    // Descargar archivo
    @GetMapping("/descargar/{id}")
    public ResponseEntity<FileSystemResource> descargarArchivo(@PathVariable("id") int id) {
        // 1️⃣ Buscar la solicitud en la base de datos
        SolicitudImpresion solicitud = solicitudService.obtenerPorId(id).orElse(null);

        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }

        // 2️⃣ Obtener la ruta del archivo guardada en la base de datos
        String rutaArchivo = solicitud.getRutaArchivo(); // ejemplo: "C:/uploads/solicitud_12.pdf"
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 3️⃣ Preparar la respuesta para que se descargue
        FileSystemResource resource = new FileSystemResource(archivo);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    
    @GetMapping("/imprimir/{id}")
    public ResponseEntity<FileSystemResource> imprimirArchivo(@PathVariable("id") int id) {
    SolicitudImpresion solicitud = solicitudService.obtenerPorId(id).orElse(null);

    if (solicitud == null) {
        return ResponseEntity.notFound().build();
    }

    String rutaArchivo = solicitud.getRutaArchivo();
    File archivo = new File(rutaArchivo);

    if (!archivo.exists()) {
        return ResponseEntity.notFound().build();
    }

    // Devolver el archivo con el tipo de contenido correspondiente
    FileSystemResource resource = new FileSystemResource(archivo);

    // Detectar tipo MIME (siempre PDF en este caso, puedes adaptar)
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + archivo.getName() + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
}




    @GetMapping("")
    public String panelOperador() {
        return "operador/index";
    }
}
