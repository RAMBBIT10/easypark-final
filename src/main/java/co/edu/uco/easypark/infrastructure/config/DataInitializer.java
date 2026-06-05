package co.edu.uco.easypark.infrastructure.config;
import co.edu.uco.easypark.crosscutting.specification.ParameterCatalog;
import co.edu.uco.easypark.domain.model.Rol;
import co.edu.uco.easypark.infrastructure.persistence.entity.MensajeCatalogoEntity;
import co.edu.uco.easypark.infrastructure.persistence.entity.NotificacionCatalogoEntity;
import co.edu.uco.easypark.infrastructure.persistence.entity.ParametroCatalogoEntity;
import co.edu.uco.easypark.infrastructure.persistence.entity.UsuarioEntity;
import co.edu.uco.easypark.infrastructure.persistence.repository.MensajeCatalogoRepository;
import co.edu.uco.easypark.infrastructure.persistence.repository.NotificacionCatalogoRepository;
import co.edu.uco.easypark.infrastructure.persistence.repository.ParametroCatalogoRepository;
import co.edu.uco.easypark.infrastructure.persistence.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MensajeCatalogoRepository mensajeRepository;
    private final ParametroCatalogoRepository parametroRepository;
    private final NotificacionCatalogoRepository notificacionRepository;
    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            MensajeCatalogoRepository mensajeRepository, ParametroCatalogoRepository parametroRepository,
            NotificacionCatalogoRepository notificacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mensajeRepository = mensajeRepository;
        this.parametroRepository = parametroRepository;
        this.notificacionRepository = notificacionRepository;
    }
    @Override
    public void run(String... args) {
        inicializarAdmin();
        inicializarMensajes();
        inicializarParametros();
        inicializarNotificaciones();
    }
    private void inicializarAdmin() {
        if (!usuarioRepository.existsByEmail(ParameterCatalog.DEFAULT_ADMIN_EMAIL)) {
            UsuarioEntity admin = new UsuarioEntity();
            admin.setNombre("Admin");
            admin.setApellido("EasyPark");
            admin.setEmail(ParameterCatalog.DEFAULT_ADMIN_EMAIL);
            admin.setPassword(passwordEncoder.encode(ParameterCatalog.DEFAULT_ADMIN_PASSWORD_PLAIN));
            admin.setRol(Rol.ADMINISTRADOR);
            admin.setActivo(true);
            usuarioRepository.save(admin);
            logger.info("Admin por defecto creado: {}", ParameterCatalog.DEFAULT_ADMIN_EMAIL);
        } else {
            logger.info("Admin por defecto ya existe, omitiendo creacion");
        }
    }
    private void inicializarMensajes() {
        if (mensajeRepository.count() > 0) return;
        crearMensaje("AUTH_REGISTRO_EXITOSO", "Usuario registrado exitosamente en EasyPark");
        crearMensaje("AUTH_LOGIN_EXITOSO", "Inicio de sesion exitoso");
        crearMensaje("AUTH_CREDENCIALES_INVALIDAS", "Credenciales invalidas, verifique su email y contrasena");
        crearMensaje("AUTH_USUARIO_INACTIVO", "Su cuenta se encuentra inactiva, contacte al administrador");
        crearMensaje("RESERVA_CREADA", "Reserva creada exitosamente");
        crearMensaje("RESERVA_CANCELADA", "Reserva cancelada exitosamente");
        crearMensaje("RESERVA_CONFIRMADA", "Reserva confirmada por el propietario");
        crearMensaje("RESERVA_RECHAZADA", "Reserva rechazada por el propietario");
        crearMensaje("PARQUEADERO_CREADO", "Parqueadero registrado exitosamente");
        crearMensaje("PARQUEADERO_ACTUALIZADO", "Parqueadero actualizado exitosamente");
        crearMensaje("ERROR_VALIDACION", "Error de validacion en los datos ingresados");
        crearMensaje("ERROR_INTERNO", "Error interno del servidor, intente mas tarde");
        logger.info("Mensajes de catalogo inicializados");
    }
    private void crearMensaje(String codigo, String mensaje) {
        MensajeCatalogoEntity e = new MensajeCatalogoEntity();
        e.setCodigo(codigo);
        e.setMensaje(mensaje);
        e.setIdioma("es");
        e.setActivo(true);
        mensajeRepository.save(e);
    }
    private void inicializarParametros() {
        if (parametroRepository.count() > 0) return;
        crearParametro("PRECIO_MINIMO_PARQUEADERO", "1000", "Precio minimo por hora en pesos colombianos");
        crearParametro("PRECIO_MAXIMO_PARQUEADERO", "50000", "Precio maximo por hora en pesos colombianos");
        crearParametro("DURACION_MINIMA_RESERVA", "1", "Duracion minima de reserva en horas");
        crearParametro("DURACION_MAXIMA_RESERVA", "24", "Duracion maxima de reserva en horas");
        crearParametro("MAX_INTENTOS_LOGIN", "5", "Maximo de intentos de login fallidos antes de bloqueo");
        crearParametro("VERSION_APP", "1.0.0", "Version actual de la aplicacion EasyPark");
        logger.info("Parametros de catalogo inicializados");
    }
    private void crearParametro(String clave, String valor, String descripcion) {
        ParametroCatalogoEntity e = new ParametroCatalogoEntity();
        e.setClave(clave);
        e.setValor(valor);
        e.setDescripcion(descripcion);
        e.setActivo(true);
        parametroRepository.save(e);
    }
    private void inicializarNotificaciones() {
        if (notificacionRepository.count() > 0) return;
        crearNotificacion("BIENVENIDA", "Bienvenido a EasyPark", "Hola {nombre}, tu cuenta ha sido creada exitosamente en EasyPark.");
        crearNotificacion("RESERVA_CONFIRMADA", "Reserva Confirmada", "Tu reserva en {parqueadero} para el {fecha} ha sido confirmada.");
        crearNotificacion("RESERVA_RECHAZADA", "Reserva Rechazada", "Tu reserva en {parqueadero} ha sido rechazada. Motivo: {motivo}.");
        crearNotificacion("RESERVA_CANCELADA", "Reserva Cancelada", "Tu reserva en {parqueadero} para el {fecha} ha sido cancelada.");
        crearNotificacion("NUEVA_RESERVA", "Nueva Reserva Recibida", "Has recibido una nueva reserva de {conductor} para el {fecha}.");
        crearNotificacion("RECORDATORIO_RESERVA", "Recordatorio de Reserva", "Tu reserva en {parqueadero} comienza en 1 hora.");
        logger.info("Notificaciones de catalogo inicializadas");
    }
    private void crearNotificacion(String tipo, String titulo, String plantilla) {
        NotificacionCatalogoEntity e = new NotificacionCatalogoEntity();
        e.setTipo(tipo);
        e.setTitulo(titulo);
        e.setPlantilla(plantilla);
        e.setActivo(true);
        notificacionRepository.save(e);
    }
}