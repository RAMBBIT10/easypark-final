package co.edu.uco.easypark.features.auth.application;

import co.edu.uco.easypark.domain.model.Rol;
import java.util.UUID;

public class LoginResponse {

    private String token;
    private String tipo = "Bearer";
    private UUID usuarioId;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;

    public LoginResponse() {}

    public LoginResponse(String token, UUID usuarioId, String nombre, String apellido, String email, Rol rol) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}