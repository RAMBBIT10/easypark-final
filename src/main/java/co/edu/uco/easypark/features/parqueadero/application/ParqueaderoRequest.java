package co.edu.uco.easypark.features.parqueadero.application;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ParqueaderoRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 150)
    private String nombre;

    @Size(max = 500)
    private String descripcion;

    @NotBlank(message = "La dirección es requerida")
    @Size(max = 255)
    private String direccion;

    @NotBlank(message = "El municipio es requerido")
    @Size(max = 100)
    private String municipio;

    @NotBlank(message = "El departamento es requerido")
    @Size(max = 100)
    private String departamento;

    @NotNull(message = "El precio por hora es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precioPorHora;

    private String imagenUrl;
    private Double latitud;
    private Double longitud;

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
    public BigDecimal getPrecioPorHora() { return precioPorHora; }
    public void setPrecioPorHora(BigDecimal precioPorHora) { this.precioPorHora = precioPorHora; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
}