package co.edu.uco.easypark.crosscutting.specification;

public final class ParameterCatalog {

    private ParameterCatalog() {}

    
    public static final String JWT_CLAIM_ROL = "rol";
    public static final String JWT_CLAIM_EMAIL = "email";
    public static final String JWT_BEARER_PREFIX = "Bearer ";
    public static final String JWT_HEADER = "Authorization";

    
    public static final String CACHE_PARQUEADEROS = "parqueaderos";
    public static final String CACHE_PARQUEADERO_ID = "parqueadero:";
    public static final long CACHE_TTL_SECONDS = 300L;

    
    public static final String WS_ENDPOINT = "/ws/parqueaderos";
    public static final String WS_TOPIC_PARQUEADEROS = "/topic/parqueaderos";
    public static final String WS_APP_PREFIX = "/app";

    
    public static final String ROL_CONDUCTOR = "CONDUCTOR";
    public static final String ROL_DUENO = "DUENO";
    public static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";

    
    public static final String[] PUBLIC_PATHS = {
            "/auth/**",
            "/parqueaderos",
            "/parqueaderos/**",
            "/actuator/health",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/ws/**",
            "/actuator/prometheus"
    };

    
    public static final String DEFAULT_ADMIN_EMAIL = "admin@easypark.co";
    public static final String DEFAULT_ADMIN_PASSWORD_PLAIN = "admin123";

    
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int NOMBRE_MAX_LENGTH = 100;
    public static final int DESCRIPCION_MAX_LENGTH = 500;
    public static final int DIRECCION_MAX_LENGTH = 255;
}
