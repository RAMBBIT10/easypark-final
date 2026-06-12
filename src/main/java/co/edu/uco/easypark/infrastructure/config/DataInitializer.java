package co.edu.uco.easypark.infrastructure.config;

import co.edu.uco.easypark.crosscutting.specification.ParameterCatalog;
import co.edu.uco.easypark.domain.model.Rol;
import co.edu.uco.easypark.infrastructure.persistence.entity.UsuarioEntity;
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

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        inicializarAdmin();
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
}