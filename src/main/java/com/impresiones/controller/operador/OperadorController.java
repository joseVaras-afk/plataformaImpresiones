package com.impresiones.controller.operador;

import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.service.SolicitudImpresionService;
import com.impresiones.repository.SolicitudImpresionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.io.File;

@Controller
@RequestMapping("/operador")
public class OperadorController {

    @Autowired
    private SolicitudImpresionService solicitudService;

    // Mostrar todas las solicitudes
    @GetMapping("/solicitudes")
    public String listarSolicitudes(Model model) {
        model.addAttribute("solicitudes", solicitudService.listarTodasOrdenadas());
        return "operador/solicitudes";
    }

    // Cambiar estado de la solicitud
    @PostMapping("/cambiarEstado/{id}")
    public String cambiarEstado(@PathVariable int id, @RequestParam String nuevoEstado) {
        solicitudService.cambiarEstado(id, nuevoEstado);
        return "redirect:/operador/solicitudes";
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
