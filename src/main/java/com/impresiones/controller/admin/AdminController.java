package com.impresiones.controller.admin;

import com.impresiones.entity.Funcionario;
import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.service.FuncionarioService;
import com.impresiones.service.SolicitudImpresionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private SolicitudImpresionService solicitudService;

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
        List<SolicitudImpresion> solicitudes = solicitudService.obtenerTodasLasSolicitudes();
        model.addAttribute("solicitudes", solicitudes);
        return "admin/solicitudes";
    }

    @GetMapping("/solicitudes/editar/{id}")
    public String editarSolicitud(@PathVariable("id") int id, Model model) {
        SolicitudImpresion solicitud = solicitudService.obtenerPorId(id);
        model.addAttribute("solicitud", solicitud);
        return "admin/form_solicitud";
    }

    @PostMapping("/solicitudes/actualizar")
    public String actualizarSolicitud(@ModelAttribute SolicitudImpresion solicitud) {
        solicitudService.actualizarSolicitud(solicitud);
        return "redirect:/admin/solicitudes";
    }
    
    @GetMapping("/admin")
    public String adminHome() {
        return "admin/index"; // vista del panel admin
    }
}
