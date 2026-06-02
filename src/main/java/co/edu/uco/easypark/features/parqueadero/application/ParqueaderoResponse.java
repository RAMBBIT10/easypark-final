package co.edu.uco.easypark.features.parqueadero.application;

import co.edu.uco.easypark.domain.model.EstadoParqueadero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ParqueaderoResponse {

    private UUID id;
    private String nombre;
    private String descripcion;
    private String direccion;
    private String municipio;
    private String departamento;
    private Double latitud;
    private Double longitud;
    private BigDecimal precioPorHora;
    private boolean disponible;
    private EstadoParqueadero estado;
    private UUID duenioId;
    private String duenioNombre;
    private String imagenUrl;
    private LocalDateTime fechaCreacion;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public BigDecimal getPrecioPorHora() { return precioPorHora; }
    public void setPrecioPorHora(BigDecimal precioPorHora) { this.precioPorHora = precioPorHora; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public EstadoParqueadero getEstado() { return estado; }
    public void setEstado(EstadoParqueadero estado) { this.estado = estado; }
    public UUID getDuenioId() { return duenioId; }
    public void setDuenioId(UUID duenioId) { this.duenioId = duenioId; }
    public String getDuenioNombre() { return duenioNombre; }
    public void setDuenioNombre(String duenioNombre) { this.duenioNombre = duenioNombre; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}