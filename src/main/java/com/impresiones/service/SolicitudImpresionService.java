package com.impresiones.service;

import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.entity.Funcionario;
import com.impresiones.repository.SolicitudImpresionRepository;
import com.impresiones.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.impresiones.entity.SolicitudImpresion;

import java.util.List;
import java.util.Optional;


@Service
public class SolicitudImpresionService {

    @Autowired
    private SolicitudImpresionRepository solicitudRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    // Crear nueva solicitud
    public SolicitudImpresion crearSolicitud(SolicitudImpresion solicitud) {
        solicitud.setEstado("PENDIENTE");
        return solicitudRepository.save(solicitud);
    }

    // Listar todas las solicitudes por estado (PENDIENTE, APROBADO, IMPRESO)
    public List<SolicitudImpresion> listarPorEstado(String estado) {
        return solicitudRepository.findByEstado(estado);
    }

    // Listar todas las solicitudes de un funcionario
    public List<SolicitudImpresion> listarPorFuncionario(Integer idFuncionario) {
        return solicitudRepository.findByFuncionarioIdFuncionario(idFuncionario);
    }

    // Aprobar solicitud
    public boolean aprobarSolicitud(Integer idSolicitud) {
        Optional<SolicitudImpresion> optional = solicitudRepository.findById(idSolicitud);
        if (optional.isPresent()) {
            SolicitudImpresion solicitud = optional.get();
            solicitud.setEstado("APROBADO");
            solicitud.setMotivoRechazo(null);
            solicitudRepository.save(solicitud);
            return true;
        }
        return false;
    }

    // Rechazar solicitud con motivo
    public boolean rechazarSolicitud(Integer idSolicitud, String motivo) {
        Optional<SolicitudImpresion> optional = solicitudRepository.findById(idSolicitud);
        if (optional.isPresent()) {
            SolicitudImpresion solicitud = optional.get();
            solicitud.setEstado("RECHAZADO");
            solicitud.setMotivoRechazo(motivo);
            solicitudRepository.save(solicitud);
            return true;
        }
        return false;
    }

    // Marcar solicitud como impresa
    public boolean marcarImpreso(Integer idSolicitud) {
        Optional<SolicitudImpresion> optional = solicitudRepository.findById(idSolicitud);
        if (optional.isPresent()) {
            SolicitudImpresion solicitud = optional.get();
            solicitud.setEstado("IMPRESO");
            solicitudRepository.save(solicitud);
            return true;
        }
        return false;
    }

    // ✅ Nuevo método: listar todas las solicitudes
    public List<SolicitudImpresion> obtenerTodasLasSolicitudes() {
        return solicitudRepository.findAll();
    }

    // ✅ Nuevo método: obtener solicitud por ID
    public SolicitudImpresion obtenerPorId(int id) {
        return solicitudRepository.findById(id).orElse(null);
    }

    // ✅ Nuevo método: actualizar una solicitud existente
    public void actualizarSolicitud(SolicitudImpresion solicitud) {
        solicitudRepository.save(solicitud);
    }
    


}
