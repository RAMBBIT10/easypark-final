package co.edu.uco.easypark.crosscutting.security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonProperty;
@Service
public class RecaptchaService {
    private static final Logger logger = LoggerFactory.getLogger(RecaptchaService.class);
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final double MIN_SCORE = 0.5;
    @Value("${RECAPTCHA_SECRET_KEY:6LdbTQgtAAAAAOwmKjhJl5x-JPc1EoM3cg0LWMd4}")
    private String secretKey;
    private final RestTemplate restTemplate = new RestTemplate();
    public boolean validar(String token) {
        if (token == null || token.isBlank()) {
            logger.warn("reCAPTCHA token vacio o nulo");
            return false;
        }
        try {
            String url = VERIFY_URL + "?secret=" + secretKey + "&response=" + token;
            RecaptchaResponse response = restTemplate.postForObject(url, null, RecaptchaResponse.class);
            if (response == null) {
                logger.warn("reCAPTCHA respuesta nula - permitiendo acceso");
                return true;
            }
            logger.info("reCAPTCHA score: {}, success: {}", response.score, response.success);
            return response.success && response.score >= MIN_SCORE;
        } catch (Exception e) {
            logger.warn("Error conectando con Google reCAPTCHA - permitiendo acceso: {}", e.getMessage());
            return true;
        }
    }
    private static class RecaptchaResponse {
        public boolean success;
        public double score;
        public String action;
        @JsonProperty("error-codes")
        public String[] errorCodes;
    }
}