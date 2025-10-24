package com.impresiones.controller.admin;

import com.impresiones.entity.Funcionario;
import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.service.FuncionarioService;
import com.impresiones.repository.SolicitudImpresionRepository;
import com.impresiones.service.SolicitudImpresionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private SolicitudImpresionService solicitudService;
    @Autowired
    private SolicitudImpresionRepository SolicitudImpresionRepository;

    @GetMapping("")
    public String panelAdmin() {
        return "admin/index";
    }

    // ================= FUNCIONARIOS =================
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

    // ================= SOLICITUDES =================
    @GetMapping("/solicitudes")
    public String listarSolicitudes(Model model) {
        List<SolicitudImpresion> solicitudes = solicitudService.listarTodasOrdenadas();
        model.addAttribute("solicitudes", solicitudes);
        return "admin/solicitudes";
    }
    //obetenr fragmento de solicitudes
    @GetMapping("/admin/solicitudes/fragment")
    public String obtenerFragmentoSolicitudes(Model model) {
    List<SolicitudImpresion> solicitudes = solicitudService.listarTodasOrdenadas();
    model.addAttribute("solicitudes", solicitudes);
    return "solicitudes :: filas"; // Thymeleaf fragment
}

//opcion con json
@GetMapping("/admin/solicitudes/json")
@ResponseBody
public List<SolicitudImpresion> listarSolicitudesJson() {
    return solicitudService.listarTodasOrdenadas();
}
    // Cambiar estado de la solicitud
    @PostMapping("/cambiarEstado/{id}")
@ResponseBody
public String cambiarEstado(@PathVariable int id,
                            @RequestParam String estado,
                            @RequestParam(required = false) String motivo) {
    boolean actualizado = solicitudService.cambiarEstado(id, estado, motivo);
    return actualizado ? "ok" : "error";
}

        @GetMapping("/solicitudes/rechazar/{id}")
    public String rechazarSolicitud(@PathVariable("id") int id, Model model) {
        SolicitudImpresion solicitud = solicitudService.obtenerPorId(id).orElse(null);
        solicitud.setEstado("RECHAZADO");
        model.addAttribute("solicitud", solicitud);
        return "admin/form_solicitud";
    }

    @PostMapping("/solicitudes/actualizar")
    public String actualizarSolicitud(@ModelAttribute SolicitudImpresion solicitud) {
        solicitudService.actualizarSolicitud(solicitud);
        return "redirect:/admin/solicitudes";
    }
    // Descargar archivo
    @GetMapping("/descargar/{id}")
    public ResponseEntity<FileSystemResource> descargarArchivo(@PathVariable("id") int id) {
        // 1?? Buscar la solicitud en la base de datos
        SolicitudImpresion solicitud = solicitudService.obtenerPorId(id).orElse(null);

        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }

        // 2?? Obtener la ruta del archivo guardada en la base de datos
        String rutaArchivo = solicitud.getRutaArchivo(); // ejemplo: "C:/uploads/solicitud_12.pdf"
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 3?? Preparar la respuesta para que se descargue
        FileSystemResource resource = new FileSystemResource(archivo);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    
    @GetMapping("/admin")
    public String adminHome() {
        return "admin/index"; // vista del panel admin
    }
}
