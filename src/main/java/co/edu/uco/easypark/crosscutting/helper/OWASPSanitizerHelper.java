package co.edu.uco.easypark.crosscutting.helper;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

@Component
public class OWASPSanitizerHelper {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return POLICY.sanitize(input.trim());
    }

    public String sanitizePlainText(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim().replaceAll("<[^>]*>", "").trim();
    }
}