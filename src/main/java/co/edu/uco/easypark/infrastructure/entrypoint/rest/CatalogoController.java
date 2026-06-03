package co.edu.uco.easypark.infrastructure.entrypoint.rest;

import co.edu.uco.easypark.infrastructure.persistence.entity.MensajeCatalogoEntity;
import co.edu.uco.easypark.infrastructure.persistence.entity.NotificacionCatalogoEntity;
import co.edu.uco.easypark.infrastructure.persistence.entity.ParametroCatalogoEntity;
import co.edu.uco.easypark.infrastructure.persistence.repository.MensajeCatalogoRepository;
import co.edu.uco.easypark.infrastructure.persistence.repository.NotificacionCatalogoRepository;
import co.edu.uco.easypark.infrastructure.persistence.repository.ParametroCatalogoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalogos")
public class CatalogoController {

    private final ParametroCatalogoRepository parametroRepository;
    private final MensajeCatalogoRepository mensajeRepository;
    private final NotificacionCatalogoRepository notificacionRepository;

    public CatalogoController(ParametroCatalogoRepository parametroRepository,
                               MensajeCatalogoRepository mensajeRepository,
                               NotificacionCatalogoRepository notificacionRepository) {
        this.parametroRepository = parametroRepository;
        this.mensajeRepository = mensajeRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @GetMapping("/parametros")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ParametroCatalogoEntity> listarParametros() {
        return parametroRepository.findAll();
    }

    @PostMapping("/parametros")
    @PreAuthorize("hasRole('ADMIN')")
    public ParametroCatalogoEntity crearParametro(@RequestBody ParametroCatalogoEntity parametro) {
        return parametroRepository.save(parametro);
    }

    @PutMapping("/parametros/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParametroCatalogoEntity> actualizarParametro(@PathVariable Long id, @RequestBody ParametroCatalogoEntity parametro) {
        return parametroRepository.findById(id).map(p -> {
            p.setClave(parametro.getClave());
            p.setValor(parametro.getValor());
            p.setDescripcion(parametro.getDescripcion());
            p.setActivo(parametro.isActivo());
            return ResponseEntity.ok(parametroRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/parametros/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarParametro(@PathVariable Long id) {
        parametroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mensajes")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MensajeCatalogoEntity> listarMensajes() {
        return mensajeRepository.findAll();
    }

    @PostMapping("/mensajes")
    @PreAuthorize("hasRole('ADMIN')")
    public MensajeCatalogoEntity crearMensaje(@RequestBody MensajeCatalogoEntity mensaje) {
        return mensajeRepository.save(mensaje);
    }

    @PutMapping("/mensajes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MensajeCatalogoEntity> actualizarMensaje(@PathVariable Long id, @RequestBody MensajeCatalogoEntity mensaje) {
        return mensajeRepository.findById(id).map(m -> {
            m.setCodigo(mensaje.getCodigo());
            m.setMensaje(mensaje.getMensaje());
            m.setIdioma(mensaje.getIdioma());
            m.setActivo(mensaje.isActivo());
            return ResponseEntity.ok(mensajeRepository.save(m));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/mensajes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarMensaje(@PathVariable Long id) {
        mensajeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notificaciones")
    @PreAuthorize("hasRole('ADMIN')")
    public List<NotificacionCatalogoEntity> listarNotificaciones() {
        return notificacionRepository.findAll();
    }

    @PostMapping("/notificaciones")
    @PreAuthorize("hasRole('ADMIN')")
    public NotificacionCatalogoEntity crearNotificacion(@RequestBody NotificacionCatalogoEntity notificacion) {
        return notificacionRepository.save(notificacion);
    }

    @PutMapping("/notificaciones/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificacionCatalogoEntity> actualizarNotificacion(@PathVariable Long id, @RequestBody NotificacionCatalogoEntity notificacion) {
        return notificacionRepository.findById(id).map(n -> {
            n.setTipo(notificacion.getTipo());
            n.setTitulo(notificacion.getTitulo());
            n.setPlantilla(notificacion.getPlantilla());
            n.setActivo(notificacion.isActivo());
            return ResponseEntity.ok(notificacionRepository.save(n));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/notificaciones/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        notificacionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
