package co.edu.uco.easypark.infrastructure.persistence.entity;

import co.edu.uco.easypark.domain.model.EstadoReserva;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservas")
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conductor_id", nullable = false)
    private UsuarioEntity conductor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parqueadero_id", nullable = false)
    private ParqueaderoEntity parqueadero;

    @Column(nullable = false, length = 10)
    private String placa;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "total_a_pagar", precision = 10, scale = 2)
    private BigDecimal totalAPagar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE_PAGO;

    @Column(name = "conductor_confirmo_pago", nullable = false)
    private boolean conductorConfirmoPago = false;

    @Column(name = "duenio_confirmo_pago", nullable = false)
    private boolean duenioConfirmoPago = false;

    @Column(name = "fecha_confirmacion_pago")
    private LocalDateTime fechaConfirmacionPago;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UsuarioEntity getConductor() { return conductor; }
    public void setConductor(UsuarioEntity conductor) { this.conductor = conductor; }
    public ParqueaderoEntity getParqueadero() { return parqueadero; }
    public void setParqueadero(ParqueaderoEntity parqueadero) { this.parqueadero = parqueadero; }
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
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}