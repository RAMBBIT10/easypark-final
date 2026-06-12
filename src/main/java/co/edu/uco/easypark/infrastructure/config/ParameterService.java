package co.edu.uco.easypark.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class ParameterService {

    private static final Logger logger = LoggerFactory.getLogger(ParameterService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${strapi.url}")
    private String strapiUrl;

    public String obtener(String clave) {
        String url = strapiUrl + "/api/parameter-catalogs?filters[clave][$eq]=" + clave + "&filters[activo][$eq]=true";
        try {
            logger.info("Consultando parametro: {} en Strapi", clave);
            Map response = restTemplate.getForObject(url, Map.class);
            List<Map> data = (List<Map>) response.get("data");
            if (data != null && !data.isEmpty()) {
                String valor = (String) data.get(0).get("valor");
                logger.info("Parametro {} = {}", clave, valor);
                return valor;
            } else {
                logger.warn("Parametro no encontrado en Strapi: {}", clave);
            }
        } catch (Exception e) {
            logger.warn("Error consultando parametro {}: {}", clave, e.getMessage());
        }
        return null;
    }
}