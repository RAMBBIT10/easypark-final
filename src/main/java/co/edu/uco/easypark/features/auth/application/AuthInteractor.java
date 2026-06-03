package co.edu.uco.easypark.features.auth.application;

import co.edu.uco.easypark.crosscutting.exception.EasyParkException;
import co.edu.uco.easypark.crosscutting.helper.OWASPSanitizerHelper;
import co.edu.uco.easypark.crosscutting.security.JwtService;
import co.edu.uco.easypark.infrastructure.email.EmailService;
import co.edu.uco.easypark.infrastructure.persistence.entity.UsuarioEntity;
import co.edu.uco.easypark.infrastructure.persistence.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthInteractor implements IAuthUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AuthInteractor.class);

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final OWASPSanitizerHelper sanitizer;
    private final EmailService emailService;

    public AuthInteractor(UsuarioRepository usuarioRepository,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder,
                          OWASPSanitizerHelper sanitizer,
                          EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.sanitizer = sanitizer;
        this.emailService = emailService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().trim().toLowerCase(),
                            request.getPassword()));
        } catch (AuthenticationException ex) {
            logger.warn("Failed login attempt for: {}", request.getEmail());
            throw new EasyParkException("auth.login.invalid", HttpStatus.UNAUTHORIZED);
        }

        UsuarioEntity usuario = usuarioRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new EasyParkException("auth.login.invalid", HttpStatus.UNAUTHORIZED));

        if (!usuario.isActivo()) {
            throw new EasyParkException("usuario.inactive", HttpStatus.FORBIDDEN);
        }

        String token = jwtService.generateToken(usuario, usuario.getRol().name());
        logger.info("Successful login: {}", usuario.getEmail());

        return new LoginResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol());
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new EasyParkException("auth.register.email.exists", HttpStatus.CONFLICT);
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(sanitizer.sanitizePlainText(request.getNombre()));
        usuario.setApellido(sanitizer.sanitizePlainText(request.getApellido()));
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setTipoDocumento(request.getTipoDocumento());
        usuario.setNumeroDocumento(sanitizer.sanitizePlainText(request.getNumeroDocumento()));
        usuario.setActivo(true);

        UsuarioEntity saved = usuarioRepository.save(usuario);
        logger.info("New user registered: {} with role {}", email, request.getRol());

        emailService.enviarBienvenida(email, saved.getNombre());

        String token = jwtService.generateToken(saved, saved.getRol().name());

        return new LoginResponse(
                token,
                saved.getId(),
                saved.getNombre(),
                saved.getApellido(),
                saved.getEmail(),
                saved.getRol());
    }
}
