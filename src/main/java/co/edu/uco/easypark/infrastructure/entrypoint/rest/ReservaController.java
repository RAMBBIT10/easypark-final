package co.edu.uco.easypark.infrastructure.entrypoint.rest;

import co.edu.uco.easypark.features.reserva.application.IReservaUseCase;
import co.edu.uco.easypark.features.reserva.application.ReservaRequest;
import co.edu.uco.easypark.features.reserva.application.ReservaResponse;
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
@RequestMapping("/reservas")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reservas", description = "Gestión de reservas con finalización automática y confirmación doble de pago")
public class ReservaController {

    private final IReservaUseCase reservaUseCase;

    public ReservaController(IReservaUseCase reservaUseCase) {
        this.reservaUseCase = reservaUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('CONDUCTOR')")
    @Operation(summary = "Crear reserva", description = "CONDUCTOR crea reserva. Estado → EN_CURSO automáticamente.")
    public ResponseEntity<ReservaResponse> crear(
            @Valid @RequestBody ReservaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaUseCase.crear(request, userDetails.getUsername()));
    }

    @GetMapping("/mis-reservas")
    @PreAuthorize("hasRole('CONDUCTOR')")
    @Operation(summary = "Mis reservas")
    public ResponseEntity<List<ReservaResponse>> misReservas(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservaUseCase.listarMisReservas(userDetails.getUsername()));
    }

    @PatchMapping("/{id}/finalizar-estadia")
    @PreAuthorize("hasRole('CONDUCTOR')")
    @Operation(summary = "Finalizar estadía",
               description = "El conductor presiona 'Finalizar estadía'. " +
                             "Sistema registra hora fin y calcula total. Estado → PENDIENTE_PAGO.")
    public ResponseEntity<ReservaResponse> finalizarEstadia(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservaUseCase.finalizarEstadia(id, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/conductor-confirma-pago")
    @PreAuthorize("hasRole('CONDUCTOR')")
    @Operation(summary = "Conductor confirma pago",
               description = "Si el dueño también confirmó → estado FINALIZADA ✅")
    public ResponseEntity<ReservaResponse> conductorConfirmaPago(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservaUseCase.conductorConfirmaPago(id, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/duenio-confirma-pago")
    @PreAuthorize("hasRole('DUENO')")
    @Operation(summary = "Dueño confirma pago",
               description = "Si el conductor también confirmó → estado FINALIZADA ✅")
    public ResponseEntity<ReservaResponse> duenioConfirmaPago(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservaUseCase.duenioConfirmaPago(id, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CONDUCTOR')")
    @Operation(summary = "Cancelar reserva")
    public ResponseEntity<ReservaResponse> cancelar(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservaUseCase.cancelar(id, userDetails.getUsername()));
    }

    @GetMapping("/mis-parqueaderos-reservas")
    @PreAuthorize("hasRole('DUENO')")
    @Operation(summary = "Reservas de mis parqueaderos")
    public ResponseEntity<List<ReservaResponse>> reservasDeMisParqueaderos(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservaUseCase.listarReservasDeMisParqueaderos(userDetails.getUsername()));
    }

    @GetMapping("/admin/todas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Todas las reservas")
    public ResponseEntity<List<ReservaResponse>> listarTodas() {
        return ResponseEntity.ok(reservaUseCase.listarTodas());
    }
}