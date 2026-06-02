package co.edu.uco.easypark.features.reserva.application;

import co.edu.uco.easypark.domain.model.EstadoReserva;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReservaResponse {

    private UUID id;
    private UUID conductorId;
    private String conductorNombre;
    private UUID parqueaderoId;
    private String parqueaderoNombre;
    private String parqueaderoDireccion;
    private String parqueaderoMunicipio;
    private String placa;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal totalAPagar;
    private EstadoReserva estado;
    private boolean conductorConfirmoPago;
    private boolean duenioConfirmoPago;
    private LocalDateTime fechaConfirmacionPago;
    private LocalDateTime fechaCreacion;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getConductorId() { return conductorId; }
    public void setConductorId(UUID conductorId) { this.conductorId = conductorId; }
    public String getConductorNombre() { return conductorNombre; }
    public void setConductorNombre(String conductorNombre) { this.conductorNombre = conductorNombre; }
    public UUID getParqueaderoId() { return parqueaderoId; }
    public void setParqueaderoId(UUID parqueaderoId) { this.parqueaderoId = parqueaderoId; }
    public String getParqueaderoNombre() { return parqueaderoNombre; }
    public void setParqueaderoNombre(String parqueaderoNombre) { this.parqueaderoNombre = parqueaderoNombre; }
    public String getParqueaderoDireccion() { return parqueaderoDireccion; }
    public void setParqueaderoDireccion(String parqueaderoDireccion) { this.parqueaderoDireccion = parqueaderoDireccion; }
    public String getParqueaderoMunicipio() { return parqueaderoMunicipio; }
    public void setParqueaderoMunicipio(String parqueaderoMunicipio) { this.parqueaderoMunicipio = parqueaderoMunicipio; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    public BigDecimal getTotalAPagar() { return totalAPagar; }
    public void setTotalAPagar(BigDecimal totalAPagar) { this.totalAPagar = totalAPagar; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    public boolean isConductorConfirmoPago() { return conductorConfirmoPago; }
    public void setConductorConfirmoPago(boolean conductorConfirmoPago) { this.conductorConfirmoPago = conductorConfirmoPago; }
    public boolean isDuenioConfirmoPago() { return duenioConfirmoPago; }
    public void setDuenioConfirmoPago(boolean duenioConfirmoPago) { this.duenioConfirmoPago = duenioConfirmoPago; }
    public LocalDateTime getFechaConfirmacionPago() { return fechaConfirmacionPago; }
    public void setFechaConfirmacionPago(LocalDateTime fechaConfirmacionPago) { this.fechaConfirmacionPago = fechaConfirmacionPago; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}