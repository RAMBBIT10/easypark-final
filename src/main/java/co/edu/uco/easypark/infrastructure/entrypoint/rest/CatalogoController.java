package co.edu.uco.easypark.infrastructure.entrypoint.rest;

import co.edu.uco.easypark.infrastructure.config.ParameterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/catalogos")
public class CatalogoController {

    private final ParameterService parameterService;

    public CatalogoController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @GetMapping("/parametros/{clave}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> obtenerParametro(@PathVariable String clave) {
        String valor = parameterService.obtener(clave);
        if (valor != null) {
            return ResponseEntity.ok(Map.of("clave", clave, "valor", valor));
        }
        return ResponseEntity.notFound().build();
    }
}