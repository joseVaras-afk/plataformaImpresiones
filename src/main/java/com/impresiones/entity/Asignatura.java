package com.impresiones.entity;

import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "asignatura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Asignatura")
    private Integer idAsignatura;

    @Column(name = "nombre_asignatura")
    private String nombreAsignatura;

    // Getters y Setters
    public Integer getIdAsignatura() {
        return idAsignatura;
    }
    public void setIdAsignatura(Integer idAsignatura) {
        this.idAsignatura = idAsignatura;
    }
    public String getNombreAsignatura() {
        return nombreAsignatura;
    }
    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }
}
   
