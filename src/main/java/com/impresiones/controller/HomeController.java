package com.impresiones.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
public String home() {
    return "login"; // muestra index.html o index.html de templates
}

/*@GetMapping("/login")
public String login() {
    return "login"; // muestra login.html
}*/
}
