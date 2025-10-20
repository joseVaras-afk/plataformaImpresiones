package com.impresiones.controller.operador;

import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.service.SolicitudImpresionService;
import com.impresiones.service.EmailService;
import com.impresiones.repository.SolicitudImpresionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
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

@Controller
@RequestMapping("/operador")
public class OperadorController {

    @Autowired
    private SolicitudImpresionService solicitudService;
    @Autowired
    private SolicitudImpresionRepository solicitudRepository;
    @Autowired
    private EmailService emailService;

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

   

    // Cambiar estado de la solicitud
    @PostMapping("/cambiarEstado/{id}")
    @ResponseBody
    public boolean cambiarEstado(int id, String estado, String motivo) {
        Optional<SolicitudImpresion> opt = solicitudRepository.findById(id);
        if (opt.isPresent()) {
            SolicitudImpresion solicitud = opt.get();
            solicitud.setEstado(estado);
            solicitudRepository.save(solicitud);

            // Enviar correo
            String destinatario = solicitud.getFuncionario().getCorreoFuncionario();
            String asunto = "Actualización de estado de su solicitud de impresión";
            String mensaje = "Estimado/a " + solicitud.getFuncionario().getNombreFuncionario() + ",\n\n"
                    + "Su solicitud de impresión (ID: " + solicitud.getIdSolicitudImpresion()+")\n" +" Asignatura: "+solicitud.getAsignatura()+"\n" + 
                    " Curso: "+solicitud.getCurso()+"\n Archivo: "+solicitud.getNombreArchivo() +"\n ha sido marcada como "
                    + estado + ".\n Por favor pase a retirar el  material\n\n Escuela Arturo Alessandri Palma.\n Sistema de Impresiones\n";

            if ("RECHAZADO".equalsIgnoreCase(estado) && motivo != null && !motivo.isBlank()) {
                mensaje += "La solicitud de impresión del archivo: "+solicitud.getNombreArchivo()+" para el curso "+solicitud.getCurso()+
                "\n Ha sido RECHAZADA por el siguiente motivo: " + motivo + "\n\n";
            }

            mensaje += "Saludos cordiales,\nSistema de Impresiones";

            emailService.enviarCorreo(destinatario, asunto, mensaje);

            return true;
        }
        return false;
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
