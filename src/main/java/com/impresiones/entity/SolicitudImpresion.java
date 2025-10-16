package com.impresiones.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "solicitud_impresion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudImpresion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idSolicitud_Impresion")
    private Integer idSolicitudImpresion;

    @Column(name = "Fecha_Impresion")
    private LocalDate fechaImpresion; // fecha requerida para la impresión

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "estado")
    private String estado; // "PENDIENTE", "APROBADO", "RECHAZADO", "IMPRESO"

    @Column(name = "Ruta_Archivo")
    private String rutaArchivo;

    @ManyToOne
    @JoinColumn(name = "ID_Asignatura")
    private Asignatura asignatura;

    @ManyToOne
    @JoinColumn(name = "ID_Funcionario")
    private Funcionario funcionario;

    @Column(name = "Cantidad_Copias")
    private Integer cantidadCopias;

    @Column(name = "Motivo_Rechazo")
    private String motivoRechazo;

    @Column(name = "Curso")
    private String Curso;

    @Column(name = "Fecha_Creacion")
    private LocalDate fechaCreacion; 
}
