package co.edu.uco.easypark.crosscutting.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class WafFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(WafFilter.class);

    
    private static final List<Pattern> SQL_INJECTION_PATTERNS = Arrays.asList(
        Pattern.compile("(?i)(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION|SCRIPT)\\s"),
        Pattern.compile("(?i)(OR|AND)\\s+['\"]?\\d+['\"]?\\s*=\\s*['\"]?\\d+['\"]?"),
        Pattern.compile("(?i)--\\s*$"),
        Pattern.compile("(?i);\\s*(DROP|DELETE|UPDATE|INSERT)"),
        Pattern.compile("(?i)\\bxp_\\w+"),
        Pattern.compile("(?i)WAITFOR\\s+DELAY")
    );

    
    private static final List<Pattern> XSS_PATTERNS = Arrays.asList(
        Pattern.compile("(?i)<script[^>]*>.*?</script>"),
        Pattern.compile("(?i)<[^>]*(onload|onerror|onclick|onmouseover|onfocus)\\s*="),
        Pattern.compile("(?i)javascript\\s*:"),
        Pattern.compile("(?i)vbscript\\s*:"),
        Pattern.compile("(?i)<iframe[^>]*>"),
        Pattern.compile("(?i)eval\\s*\\("),
        Pattern.compile("(?i)document\\.(cookie|write|location)")
    );

    
    private static final List<Pattern> PATH_TRAVERSAL_PATTERNS = Arrays.asList(
        Pattern.compile("\\.\\./"),
        Pattern.compile("\\.\\.\\\\"),
        Pattern.compile("(?i)%2e%2e%2f"),
        Pattern.compile("(?i)%252e%252e")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String ip = request.getRemoteAddr();

        
        if (isAttack(uri, ip, "URI")) {
            blockRequest(response, "Malicious request detected");
            return;
        }

        
        if (queryString != null && isAttack(queryString, ip, "QueryString")) {
            blockRequest(response, "Malicious query string detected");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAttack(String input, String ip, String source) {
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(input).find()) {
                logger.warn("WAF - SQL Injection detectado en {} desde IP {}: {}", source, ip, input);
                return true;
            }
        }
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                logger.warn("WAF - XSS detectado en {} desde IP {}: {}", source, ip, input);
                return true;
            }
        }
        for (Pattern pattern : PATH_TRAVERSAL_PATTERNS) {
            if (pattern.matcher(input).find()) {
                logger.warn("WAF - Path Traversal detectado en {} desde IP {}: {}", source, ip, input);
                return true;
            }
        }
        return false;
    }

    private void blockRequest(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}