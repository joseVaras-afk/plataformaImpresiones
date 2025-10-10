package com.impresiones.entity;

import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "Asignatura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Asignatura")
    private Integer idAsignatura;

    @Column(name = "Nombre_Asignatura")
    private String nombreAsignatura;
}
