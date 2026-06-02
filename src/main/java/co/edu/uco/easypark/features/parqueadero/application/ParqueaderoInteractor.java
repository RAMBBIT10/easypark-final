package co.edu.uco.easypark.features.parqueadero.application;

import co.edu.uco.easypark.crosscutting.exception.EasyParkException;
import co.edu.uco.easypark.crosscutting.helper.OWASPSanitizerHelper;
import co.edu.uco.easypark.domain.model.EstadoParqueadero;
import co.edu.uco.easypark.domain.model.EstadoReserva;
import co.edu.uco.easypark.infrastructure.cache.RedisParqueaderoService;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ParqueaderoInteractor implements IParqueaderoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ParqueaderoInteractor.class);

    private final ParqueaderoRepository parqueaderoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final RedisParqueaderoService redisService;
    private final ParqueaderoWebSocketHandler wsHandler;
    private final FirebaseNotificationService notificationService;
    private final OWASPSanitizerHelper sanitizer;

    public ParqueaderoInteractor(ParqueaderoRepository parqueaderoRepository,
                                  UsuarioRepository usuarioRepository,
                                  ReservaRepository reservaRepository,
                                  RedisParqueaderoService redisService,
                                  ParqueaderoWebSocketHandler wsHandler,
                                  FirebaseNotificationService notificationService,
                                  OWASPSanitizerHelper sanitizer) {
        this.parqueaderoRepository = parqueaderoRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.redisService = redisService;
        this.wsHandler = wsHandler;
        this.notificationService = notificationService;
        this.sanitizer = sanitizer;
    }

    @Override
    @Transactional
    public ParqueaderoResponse crear(ParqueaderoRequest request, String emailDuenio) {
        UsuarioEntity duenio = findUsuario(emailDuenio);
        ParqueaderoEntity entity = new ParqueaderoEntity();
        entity.setNombre(sanitizer.sanitizePlainText(request.getNombre()));
        entity.setDescripcion(sanitizer.sanitize(request.getDescripcion()));
        entity.setDireccion(sanitizer.sanitizePlainText(request.getDireccion()));
        entity.setMunicipio(sanitizer.sanitizePlainText(request.getMunicipio()));
        entity.setDepartamento(sanitizer.sanitizePlainText(request.getDepartamento()));
        entity.setPrecioPorHora(request.getPrecioPorHora());
        entity.setImagenUrl(request.getImagenUrl());
        entity.setLatitud(request.getLatitud());
        entity.setLongitud(request.getLongitud());
        entity.setDisponible(true);
        entity.setEstado(EstadoParqueadero.PENDIENTE_APROBACION);
        entity.setDuenio(duenio);
        ParqueaderoEntity saved = parqueaderoRepository.save(entity);
        logger.info("Parqueadero creado: {} por {}", saved.getId(), emailDuenio);
        return toResponse(saved);
    }

    @Override
    public List<ParqueaderoResponse> listarDisponibles() {
        return parqueaderoRepository.findByEstadoAndDisponible(EstadoParqueadero.APROBADO, true)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ParqueaderoResponse> listarMisParqueaderos(String emailDuenio) {
        UsuarioEntity duenio = findUsuario(emailDuenio);
        return parqueaderoRepository.findByDuenio(duenio)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParqueaderoResponse actualizarDisponibilidad(UUID id, boolean disponible, String emailDuenio) {
        ParqueaderoEntity entity = findParqueadero(id);
        UsuarioEntity duenio = findUsuario(emailDuenio);
        if (!entity.getDuenio().getId().equals(duenio.getId())) {
            throw new EasyParkException("parqueadero.not.owner", HttpStatus.FORBIDDEN);
        }
        entity.setDisponible(disponible);
        ParqueaderoEntity saved = parqueaderoRepository.save(entity);
        redisService.eliminar(id);
        wsHandler.notificarCambioDisponibilidad(id, disponible);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ParqueaderoResponse aprobar(UUID id) {
        ParqueaderoEntity entity = findParqueadero(id);
        if (entity.getEstado() != EstadoParqueadero.PENDIENTE_APROBACION) {
            throw new EasyParkException("parqueadero.not.found", HttpStatus.BAD_REQUEST);
        }
        entity.setEstado(EstadoParqueadero.APROBADO);
        ParqueaderoEntity saved = parqueaderoRepository.save(entity);
        notificationService.enviarNotificacion(entity.getDuenio().getTokenFcm(),
                "Parqueadero aprobado!",
                "Tu parqueadero \"" + entity.getNombre() + "\" fue aprobado.");
        wsHandler.notificarNuevoParqueadero(id, entity.getNombre());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ParqueaderoResponse rechazar(UUID id, String motivo) {
        ParqueaderoEntity entity = findParqueadero(id);
        if (entity.getEstado() != EstadoParqueadero.PENDIENTE_APROBACION) {
            throw new EasyParkException("parqueadero.not.found", HttpStatus.BAD_REQUEST);
        }
        entity.setEstado(EstadoParqueadero.RECHAZADO);
        ParqueaderoEntity saved = parqueaderoRepository.save(entity);
        String mensajeNotif = motivo != null && !motivo.isBlank()
            ? "Tu parqueadero \"" + entity.getNombre() + "\" fue rechazado. Motivo: " + motivo
            : "Tu parqueadero \"" + entity.getNombre() + "\" fue rechazado.";
        notificationService.enviarNotificacion(entity.getDuenio().getTokenFcm(), "Parqueadero rechazado", mensajeNotif);
        logger.info("Parqueadero {} rechazado. Motivo: {}", id, motivo);
        return toResponse(saved);
    }

    @Override
    public List<ParqueaderoResponse> listarPendientes() {
        return parqueaderoRepository.findByEstado(EstadoParqueadero.PENDIENTE_APROBACION)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminar(UUID id, String emailDuenio) {
        ParqueaderoEntity entity = findParqueadero(id);
        UsuarioEntity duenio = findUsuario(emailDuenio);
        if (!entity.getDuenio().getId().equals(duenio.getId())) {
            throw new EasyParkException("parqueadero.not.owner", HttpStatus.FORBIDDEN);
        }
        List<ReservaEntity> reservasActivas = reservaRepository.findByParqueaderoIdAndEstado(id, EstadoReserva.EN_CURSO);
        if (!reservasActivas.isEmpty()) {
            throw new EasyParkException("parqueadero.has.active.reservations", HttpStatus.BAD_REQUEST);
        }
        List<ReservaEntity> todasLasReservas = reservaRepository.findByParqueadero(entity);
        reservaRepository.deleteAll(todasLasReservas);
        parqueaderoRepository.deleteById(id);
        logger.info("Parqueadero {} eliminado por {}", id, emailDuenio);
    }

    private UsuarioEntity findUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EasyParkException("usuario.email.not.found", HttpStatus.NOT_FOUND));
    }

    private ParqueaderoEntity findParqueadero(UUID id) {
        return parqueaderoRepository.findById(id)
                .orElseThrow(() -> new EasyParkException("parqueadero.not.found", HttpStatus.NOT_FOUND));
    }

    private ParqueaderoResponse toResponse(ParqueaderoEntity e) {
        ParqueaderoResponse r = new ParqueaderoResponse();
        r.setId(e.getId());
        r.setNombre(e.getNombre());
        r.setDescripcion(e.getDescripcion());
        r.setDireccion(e.getDireccion());
        r.setMunicipio(e.getMunicipio());
        r.setDepartamento(e.getDepartamento());
        r.setLatitud(e.getLatitud());
        r.setLongitud(e.getLongitud());
        r.setPrecioPorHora(e.getPrecioPorHora());
        r.setDisponible(e.isDisponible());
        r.setEstado(e.getEstado());
        r.setDuenioId(e.getDuenio().getId());
        r.setDuenioNombre(e.getDuenio().getNombre() + " " + e.getDuenio().getApellido());
        r.setImagenUrl(e.getImagenUrl());
        r.setFechaCreacion(e.getFechaCreacion());
        return r;
    }
}