package co.edu.uco.easypark.features.reserva.application;

import co.edu.uco.easypark.crosscutting.exception.EasyParkException;
import co.edu.uco.easypark.domain.model.EstadoParqueadero;
import co.edu.uco.easypark.domain.model.EstadoReserva;
import co.edu.uco.easypark.infrastructure.cache.RedisParqueaderoService;
import co.edu.uco.easypark.infrastructure.email.EmailService;
import co.edu.uco.easypark.infrastructure.gateway.ParqueaderoWebSocketHandler;
import co.edu.uco.easypark.infrastructure.notification.FirebaseNotificationService;
import co.edu.uco.easypark.infrastructure.persistence.entity.ParqueaderoEntity;
import co.edu.uco.easypark.infrastructure.persistence.entity.ReservaEntity;
import co.edu.uco.easypark.infrastructure.persistence.entity.UsuarioEntity;
import co.edu.uco.easypark.infrastructure.persistence.repository.ParqueaderoRepository;
import co.edu.uco.easypark.infrastructure.persistence.repository.ReservaRepository;
import co.edu.uco.easypark.infrastructure.persistence.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservaInteractor implements IReservaUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReservaInteractor.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ReservaRepository reservaRepository;
    private final ParqueaderoRepository parqueaderoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RedisParqueaderoService redisService;
    private final ParqueaderoWebSocketHandler wsHandler;
    private final FirebaseNotificationService notificationService;
    private final EmailService emailService;

    public ReservaInteractor(ReservaRepository reservaRepository,
                              ParqueaderoRepository parqueaderoRepository,
                              UsuarioRepository usuarioRepository,
                              RedisParqueaderoService redisService,
                              ParqueaderoWebSocketHandler wsHandler,
                              FirebaseNotificationService notificationService,
                              EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.parqueaderoRepository = parqueaderoRepository;
        this.usuarioRepository = usuarioRepository;
        this.redisService = redisService;
        this.wsHandler = wsHandler;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public ReservaResponse crear(ReservaRequest request, String emailConductor) {
        UsuarioEntity conductor = findUsuario(emailConductor);
        ParqueaderoEntity parqueadero = findParqueadero(request.getParqueaderoId());

        if (parqueadero.getEstado() != EstadoParqueadero.APROBADO) {
            throw new EasyParkException("parqueadero.unavailable", HttpStatus.BAD_REQUEST);
        }
        if (!parqueadero.isDisponible()) {
            throw new EasyParkException("parqueadero.unavailable", HttpStatus.CONFLICT);
        }

        boolean locked = redisService.bloquear(parqueadero.getId(), 30);
        try {
            List<ReservaEntity> conflictos = reservaRepository.findConflictos(
                    parqueadero, request.getFechaInicio(), request.getFechaInicio().plusYears(1));
            if (!conflictos.isEmpty()) {
                throw new EasyParkException("reserva.conflict", HttpStatus.CONFLICT);
            }

            ReservaEntity reserva = new ReservaEntity();
            reserva.setConductor(conductor);
            reserva.setParqueadero(parqueadero);
            reserva.setPlaca(request.getPlaca());
            reserva.setFechaInicio(request.getFechaInicio());
            reserva.setFechaFin(null);
            reserva.setTotalAPagar(null);
            reserva.setEstado(EstadoReserva.EN_CURSO);
            reserva.setConductorConfirmoPago(false);
            reserva.setDuenioConfirmoPago(false);

            ReservaEntity saved = reservaRepository.save(reserva);

            parqueadero.setDisponible(false);
            parqueaderoRepository.save(parqueadero);
            redisService.eliminar(parqueadero.getId());
            wsHandler.notificarCambioDisponibilidad(parqueadero.getId(), false);

            notificationService.enviarNotificacion(
                    parqueadero.getDuenio().getTokenFcm(),
                    "Nueva reserva - Placa: " + request.getPlaca(),
                    conductor.getNombre() + " llego a \"" + parqueadero.getNombre() + "\".");

            notificationService.enviarNotificacion(
                    conductor.getTokenFcm(),
                    "Reserva activa",
                    "Estas en \"" + parqueadero.getNombre() + "\". Cuando termines presiona Finalizar estadia.");

            try {
                emailService.enviarConfirmacionReserva(
                        conductor.getEmail(),
                        conductor.getNombre(),
                        parqueadero.getNombre(),
                        request.getFechaInicio().format(FMT));
            } catch (Exception e) {
                logger.warn("No se pudo enviar email de reserva al conductor: {}", e.getMessage());
            }

            try {
                emailService.enviarNuevaReservaDuenio(
                        parqueadero.getDuenio().getEmail(),
                        parqueadero.getDuenio().getNombre(),
                        parqueadero.getNombre(),
                        conductor.getNombre() + " " + conductor.getApellido(),
                        request.getPlaca(),
                        request.getFechaInicio().format(FMT));
            } catch (Exception e) {
                logger.warn("No se pudo enviar email de reserva al duenio: {}", e.getMessage());
            }

            wsHandler.notificarReservaCreada(parqueadero.getId(), saved.getId());
            logger.info("Reserva {} creada - placa: {}", saved.getId(), request.getPlaca());
            return toResponse(saved);
        } finally {
            if (locked) redisService.liberarBloqueo(parqueadero.getId());
        }
    }

    @Override
    @Transactional
    public ReservaResponse finalizarEstadia(UUID id, String emailConductor) {
        UsuarioEntity conductor = findUsuario(emailConductor);
        ReservaEntity reserva = findReserva(id);

        if (!reserva.getConductor().getId().equals(conductor.getId())) {
            throw new EasyParkException("reserva.not.owner", HttpStatus.FORBIDDEN);
        }
        if (reserva.getEstado() != EstadoReserva.EN_CURSO) {
            throw new EasyParkException("reserva.already.cancelled", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime fechaFin = LocalDateTime.now();
        reserva.setFechaFin(fechaFin);

        long minutos = Duration.between(reserva.getFechaInicio(), fechaFin).toMinutes();
        if (minutos < 1) minutos = 1;
        BigDecimal horasFrac = BigDecimal.valueOf(minutos).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal total = reserva.getParqueadero().getPrecioPorHora()
                .multiply(horasFrac).setScale(0, RoundingMode.HALF_UP);

        reserva.setTotalAPagar(total);
        reserva.setEstado(EstadoReserva.PENDIENTE_PAGO);

        ReservaEntity saved = reservaRepository.save(reserva);

        reserva.getParqueadero().setDisponible(true);
        parqueaderoRepository.save(reserva.getParqueadero());
        redisService.eliminar(reserva.getParqueadero().getId());
        wsHandler.notificarCambioDisponibilidad(reserva.getParqueadero().getId(), true);

        notificationService.enviarNotificacion(
                conductor.getTokenFcm(),
                "Estadia finalizada",
                "Total a pagar: $" + total + ". Confirma el pago cuando lo hagas.");

        notificationService.enviarNotificacion(
                reserva.getParqueadero().getDuenio().getTokenFcm(),
                "Estadia finalizada - Placa: " + reserva.getPlaca(),
                conductor.getNombre() + " finalizo. Total a cobrar: $" + total);

        try {
            emailService.enviarEstadiaFinalizada(
                    conductor.getEmail(),
                    conductor.getNombre(),
                    reserva.getParqueadero().getNombre(),
                    total.toString(),
                    reserva.getFechaInicio().format(FMT),
                    fechaFin.format(FMT));
        } catch (Exception e) {
            logger.warn("No se pudo enviar email de finalizacion al conductor: {}", e.getMessage());
        }

        logger.info("Estadia finalizada - reserva: {}, total: ${}", id, total);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ReservaResponse conductorConfirmaPago(UUID id, String emailConductor) {
        UsuarioEntity conductor = findUsuario(emailConductor);
        ReservaEntity reserva = findReserva(id);

        if (!reserva.getConductor().getId().equals(conductor.getId())) {
            throw new EasyParkException("reserva.not.owner", HttpStatus.FORBIDDEN);
        }
        if (reserva.getEstado() != EstadoReserva.PENDIENTE_PAGO) {
            throw new EasyParkException("reserva.already.cancelled", HttpStatus.BAD_REQUEST);
        }

        reserva.setConductorConfirmoPago(true);
        verificarYConfirmarPago(reserva);
        ReservaEntity saved = reservaRepository.save(reserva);

        if (saved.getEstado() == EstadoReserva.PENDIENTE_PAGO) {
            notificationService.enviarNotificacion(
                    reserva.getParqueadero().getDuenio().getTokenFcm(),
                    "El conductor confirmo el pago",
                    conductor.getNombre() + " confirmo que pago $" + reserva.getTotalAPagar() +
                    ". Confirma tu tambien para finalizar.");
        }

        logger.info("Conductor {} confirmo pago de reserva {}", emailConductor, id);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ReservaResponse duenioConfirmaPago(UUID id, String emailDuenio) {
        UsuarioEntity duenio = findUsuario(emailDuenio);
        ReservaEntity reserva = findReserva(id);

        if (!reserva.getParqueadero().getDuenio().getId().equals(duenio.getId())) {
            throw new EasyParkException("parqueadero.not.owner", HttpStatus.FORBIDDEN);
        }
        if (reserva.getEstado() != EstadoReserva.PENDIENTE_PAGO) {
            throw new EasyParkException("reserva.already.cancelled", HttpStatus.BAD_REQUEST);
        }

        reserva.setDuenioConfirmoPago(true);
        verificarYConfirmarPago(reserva);
        ReservaEntity saved = reservaRepository.save(reserva);

        if (saved.getEstado() == EstadoReserva.PENDIENTE_PAGO) {
            notificationService.enviarNotificacion(
                    reserva.getConductor().getTokenFcm(),
                    "El dueno confirmo el pago",
                    "El dueno de \"" + reserva.getParqueadero().getNombre() +
                    "\" confirmo. Confirma tu tambien para finalizar.");
        }

        logger.info("Dueno {} confirmo pago de reserva {}", emailDuenio, id);
        return toResponse(saved);
    }

    private void verificarYConfirmarPago(ReservaEntity reserva) {
        if (reserva.isConductorConfirmoPago() && reserva.isDuenioConfirmoPago()) {
            reserva.setEstado(EstadoReserva.FINALIZADA);
            reserva.setFechaConfirmacionPago(LocalDateTime.now());

            notificationService.enviarNotificacion(
                    reserva.getConductor().getTokenFcm(),
                    "Pago confirmado - Proceso finalizado",
                    "Gracias por usar Easy Park. Total pagado: $" + reserva.getTotalAPagar());

            notificationService.enviarNotificacion(
                    reserva.getParqueadero().getDuenio().getTokenFcm(),
                    "Pago confirmado - Proceso finalizado",
                    "Pago de $" + reserva.getTotalAPagar() + " confirmado por ambas partes.");

            try {
                emailService.enviarPagoFinalizado(
                        reserva.getConductor().getEmail(),
                        reserva.getConductor().getNombre(),
                        reserva.getParqueadero().getNombre(),
                        reserva.getTotalAPagar().toString());
            } catch (Exception e) {
                logger.warn("No se pudo enviar email de pago finalizado: {}", e.getMessage());
            }

            logger.info("Reserva {} FINALIZADA - total: ${}", reserva.getId(), reserva.getTotalAPagar());
        }
    }

    @Override
    @Transactional
    public ReservaResponse cancelar(UUID id, String emailConductor) {
        UsuarioEntity conductor = findUsuario(emailConductor);
        ReservaEntity reserva = findReserva(id);

        if (!reserva.getConductor().getId().equals(conductor.getId())) {
            throw new EasyParkException("reserva.not.owner", HttpStatus.FORBIDDEN);
        }
        if (reserva.getEstado() == EstadoReserva.CANCELADA || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new EasyParkException("reserva.already.cancelled", HttpStatus.BAD_REQUEST);
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        ReservaEntity saved = reservaRepository.save(reserva);

        reserva.getParqueadero().setDisponible(true);
        parqueaderoRepository.save(reserva.getParqueadero());
        redisService.eliminar(reserva.getParqueadero().getId());
        wsHandler.notificarCambioDisponibilidad(reserva.getParqueadero().getId(), true);

        notificationService.enviarNotificacion(
                reserva.getParqueadero().getDuenio().getTokenFcm(),
                "Reserva cancelada",
                conductor.getNombre() + " cancelo su reserva en \"" +
                reserva.getParqueadero().getNombre() + "\".");

        try {
            emailService.enviarRechazoReserva(
                    conductor.getEmail(),
                    conductor.getNombre(),
                    reserva.getParqueadero().getNombre(),
                    "Cancelada por el conductor");
        } catch (Exception e) {
            logger.warn("No se pudo enviar email de cancelacion: {}", e.getMessage());
        }

        return toResponse(saved);
    }

    @Override
    public List<ReservaResponse> listarMisReservas(String emailConductor) {
        UsuarioEntity conductor = findUsuario(emailConductor);
        return reservaRepository.findByConductor(conductor)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReservaResponse> listarReservasDeMisParqueaderos(String emailDuenio) {
        UsuarioEntity duenio = findUsuario(emailDuenio);
        return parqueaderoRepository.findByDuenio(duenio).stream()
                .flatMap(p -> reservaRepository.findByParqueadero(p).stream())
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReservaResponse> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    private UsuarioEntity findUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EasyParkException("usuario.email.not.found", HttpStatus.NOT_FOUND));
    }

    private ParqueaderoEntity findParqueadero(UUID id) {
        return parqueaderoRepository.findById(id)
                .orElseThrow(() -> new EasyParkException("parqueadero.not.found", HttpStatus.NOT_FOUND));
    }

    private ReservaEntity findReserva(UUID id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new EasyParkException("reserva.not.found", HttpStatus.NOT_FOUND));
    }

    private ReservaResponse toResponse(ReservaEntity e) {
        ReservaResponse r = new ReservaResponse();
        r.setId(e.getId());
        r.setConductorId(e.getConductor().getId());
        r.setConductorNombre(e.getConductor().getNombre() + " " + e.getConductor().getApellido());
        r.setParqueaderoId(e.getParqueadero().getId());
        r.setParqueaderoNombre(e.getParqueadero().getNombre());
        r.setParqueaderoDireccion(e.getParqueadero().getDireccion());
        r.setParqueaderoMunicipio(e.getParqueadero().getMunicipio());
        r.setPlaca(e.getPlaca());
        r.setFechaInicio(e.getFechaInicio());
        r.setFechaFin(e.getFechaFin());
        r.setTotalAPagar(e.getTotalAPagar());
        r.setEstado(e.getEstado());
        r.setConductorConfirmoPago(e.isConductorConfirmoPago());
        r.setDuenioConfirmoPago(e.isDuenioConfirmoPago());
        r.setFechaConfirmacionPago(e.getFechaConfirmacionPago());
        r.setFechaCreacion(e.getFechaCreacion());
        return r;
    }
}