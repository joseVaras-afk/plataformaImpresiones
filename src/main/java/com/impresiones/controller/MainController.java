package com.impresiones.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index"; // Carga la vista principal
    }

    @GetMapping("/solicitudes")
    public String solicitudes() {
        return "solicitudes"; // Carga la vista de solicitudes
    }

    @GetMapping("/detalle-solicitud")
    public String detalleSolicitud() {
        return "detalle-solicitud"; // Carga la vista de detalle
    }
}
