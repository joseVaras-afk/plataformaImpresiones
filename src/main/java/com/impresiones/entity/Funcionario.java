package com.impresiones.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "Funcionario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Funcionario")
    private Integer idFuncionario;

    @Column(name = "Nombre_Funcionario")
    private String nombreFuncionario;

    @Column(name = "Correo_Funcionario")
    private String correoFuncionario;

    @Column(name = "Contraseña_Funcionario")
    private String contrasenaFuncionario; // guarda hash más adelante

    @Column(name = "Perfil_Funcionario")
    private String perfilFuncionario; // e.g. "PROFESOR", "ADMINISTRADOR","OPERADOR"
}
