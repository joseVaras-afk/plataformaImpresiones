package com.impresiones.service;

import com.impresiones.entity.SolicitudImpresion;
import com.impresiones.service.EmailService;
import com.impresiones.repository.SolicitudImpresionRepository;
import com.impresiones.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @Autowired
    private EmailService emailService;

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
    public Optional<SolicitudImpresion> obtenerPorId(int id) {
        return solicitudRepository.findById(id);
    }

    // ✅ Nuevo método: actualizar una solicitud existente
    public void actualizarSolicitud(SolicitudImpresion solicitud) {
        solicitudRepository.save(solicitud);
    }
    
    public SolicitudImpresion guardar(SolicitudImpresion s) {
        return solicitudRepository.save(s);
    }

    public void eliminar(Integer id) {
        solicitudRepository.deleteById(id);
    }

    public List<SolicitudImpresion> listarTodasOrdenadas() {
        return solicitudRepository.findAllByOrderByFechaCreacionAsc();
    }

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
                    + "Su solicitud de impresión del curso"+solicitud.getCurso()+" asignatura "+ 
                    solicitud.getAsignatura().getNombreAsignatura()+ " ha sido marcada como "
                    + estado + ".\n\n";

            if ("RECHAZADO".equalsIgnoreCase(estado) && motivo != null && !motivo.isBlank()) {
                mensaje += "Motivo del rechazo: " + motivo + "\n\n";
            }

            mensaje += "Saludos cordiales,\nSistema de Impresiones";

            emailService.enviarCorreo(destinatario, asunto, mensaje);

            return true;
        }
        return false;
    }


}
