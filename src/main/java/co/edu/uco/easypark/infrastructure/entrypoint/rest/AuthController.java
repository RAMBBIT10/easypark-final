package co.edu.uco.easypark.infrastructure.entrypoint.rest;

import co.edu.uco.easypark.features.auth.application.IAuthUseCase;
import co.edu.uco.easypark.features.auth.application.LoginRequest;
import co.edu.uco.easypark.features.auth.application.LoginResponse;
import co.edu.uco.easypark.features.auth.application.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints de login y registro")
public class AuthController {

    private final IAuthUseCase authUseCase;

    public AuthController(IAuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Retorna JWT con datos del usuario")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authUseCase.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Roles: CONDUCTOR, DUENO")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authUseCase.register(request));
    }
}