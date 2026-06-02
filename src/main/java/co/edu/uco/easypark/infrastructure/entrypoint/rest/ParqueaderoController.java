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
    @Operation(summary = "Listar parqueaderos disponibles")
    public ResponseEntity<List<ParqueaderoResponse>> listarDisponibles() {
        return ResponseEntity.ok(parqueaderoUseCase.listarDisponibles());
    }

    @PostMapping
    @PreAuthorize("hasRole('DUENO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear parqueadero")
    public ResponseEntity<ParqueaderoResponse> crear(
            @Valid @RequestBody ParqueaderoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parqueaderoUseCase.crear(request, userDetails.getUsername()));
    }

    @GetMapping("/mis-parqueaderos")
    @PreAuthorize("hasRole('DUENO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mis parqueaderos")
    public ResponseEntity<List<ParqueaderoResponse>> misParqueaderos(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(parqueaderoUseCase.listarMisParqueaderos(userDetails.getUsername()));
    }

    @PatchMapping("/{id}/disponibilidad")
    @PreAuthorize("hasRole('DUENO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar disponibilidad")
    public ResponseEntity<ParqueaderoResponse> actualizarDisponibilidad(
            @PathVariable UUID id,
            @RequestParam boolean disponible,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                parqueaderoUseCase.actualizarDisponibilidad(id, disponible, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DUENO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar parqueadero", description = "Solo si no tiene reservas activas")
    public ResponseEntity<Void> eliminar(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        parqueaderoUseCase.eliminar(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/{id}/aprobar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Aprobar parqueadero")
    public ResponseEntity<ParqueaderoResponse> aprobar(@PathVariable UUID id) {
        return ResponseEntity.ok(parqueaderoUseCase.aprobar(id));
    }

    @PatchMapping("/admin/{id}/rechazar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Rechazar parqueadero")
    public ResponseEntity<ParqueaderoResponse> rechazar(
            @PathVariable UUID id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(parqueaderoUseCase.rechazar(id, motivo));
    }

    @GetMapping("/admin/pendientes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Parqueaderos pendientes")
    public ResponseEntity<List<ParqueaderoResponse>> listarPendientes() {
        return ResponseEntity.ok(parqueaderoUseCase.listarPendientes());
    }
}