package co.edu.uco.easypark.infrastructure.entrypoint.rest;

import co.edu.uco.easypark.features.parqueadero.application.IParqueaderoUseCase;
import co.edu.uco.easypark.features.parqueadero.application.ParqueaderoRequest;
import co.edu.uco.easypark.features.parqueadero.application.ParqueaderoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/parqueaderos")
@Tag(name = "Parqueaderos", description = "Gestión de parqueaderos privados")
public class ParqueaderoController {

    private final IParqueaderoUseCase parqueaderoUseCase;

    public ParqueaderoController(IParqueaderoUseCase parqueaderoUseCase) {
        this.parqueaderoUseCase = parqueaderoUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar parqueaderos disponibles", description = "Público - muestra parqueaderos aprobados y disponibles")
    public ResponseEntity<List<ParqueaderoResponse>> listarDisponibles() {
        return ResponseEntity.ok(parqueaderoUseCase.listarDisponibles());
    }

    @PostMapping
    @PreAuthorize("hasRole('DUENO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear parqueadero", description = "Solo DUENO - queda en estado PENDIENTE_APROBACION")
    public ResponseEntity<ParqueaderoResponse> crear(
            @Valid @RequestBody ParqueaderoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ParqueaderoResponse response = parqueaderoUseCase.crear(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mis-parqueaderos")
    @PreAuthorize("hasRole('DUENO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mis parqueaderos", description = "Lista parqueaderos del dueño autenticado")
    public ResponseEntity<List<ParqueaderoResponse>> misParqueaderos(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(parqueaderoUseCase.listarMisParqueaderos(userDetails.getUsername()));
    }

    @PatchMapping("/{id}/disponibilidad")
    @PreAuthorize("hasRole('DUENO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar disponibilidad", description = "El dueño activa/desactiva su parqueadero")
    public ResponseEntity<ParqueaderoResponse> actualizarDisponibilidad(
            @PathVariable UUID id,
            @RequestParam boolean disponible,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                parqueaderoUseCase.actualizarDisponibilidad(id, disponible, userDetails.getUsername()));
    }

    @PatchMapping("/admin/{id}/aprobar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Aprobar parqueadero", description = "ADMINISTRADOR aprueba un parqueadero pendiente")
    public ResponseEntity<ParqueaderoResponse> aprobar(@PathVariable UUID id) {
        return ResponseEntity.ok(parqueaderoUseCase.aprobar(id));
    }

    @PatchMapping("/admin/{id}/rechazar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Rechazar parqueadero", description = "ADMINISTRADOR rechaza un parqueadero con motivo opcional")
    public ResponseEntity<ParqueaderoResponse> rechazar(
            @PathVariable UUID id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(parqueaderoUseCase.rechazar(id, motivo));
    }

    @GetMapping("/admin/pendientes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Parqueaderos pendientes", description = "ADMINISTRADOR lista parqueaderos en espera de aprobación")
    public ResponseEntity<List<ParqueaderoResponse>> listarPendientes() {
        return ResponseEntity.ok(parqueaderoUseCase.listarPendientes());
    }
}