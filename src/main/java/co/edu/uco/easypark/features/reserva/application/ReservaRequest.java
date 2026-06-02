package co.edu.uco.easypark.features.reserva.application;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReservaRequest {

    @NotNull(message = "El parqueadero es requerido")
    private UUID parqueaderoId;

    @NotBlank(message = "La placa es requerida")
    @Pattern(regexp = "^[A-Z]{3}[0-9]{3}$|^[A-Z]{3}[0-9]{2}[A-Z]$",
             message = "Formato de placa inválido (ej: ABC123 o ABC12D)")
    private String placa;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDateTime fechaInicio;

    public UUID getParqueaderoId() { return parqueaderoId; }
    public void setParqueaderoId(UUID parqueaderoId) { this.parqueaderoId = parqueaderoId; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa != null ? placa.toUpperCase() : null; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
}