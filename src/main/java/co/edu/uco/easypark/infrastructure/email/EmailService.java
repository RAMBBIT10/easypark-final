package co.edu.uco.easypark.infrastructure.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;

    @Value("${GMAIL_USERNAME}")
    private String gmailUsername;

    @Value("${strapi.url}")
    private String strapiUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.restTemplate = new RestTemplate();
    }

    private Map<String, String> obtenerMensaje(String codigo) {
        String url = strapiUrl + "/api/message-catalogs?filters[codigo][$eq]=" + codigo;
        try {
            logger.info("Consultando Strapi para codigo: {} en {}", codigo, url);
            Map response = restTemplate.getForObject(url, Map.class);
            List<Map> data = (List<Map>) response.get("data");
            if (data != null && !data.isEmpty()) {
                logger.info("Mensaje encontrado en Strapi para codigo: {}", codigo);
                return (Map<String, String>) data.get(0);
            } else {
                logger.warn("No se encontro mensaje en Strapi para codigo: {}", codigo);
            }
        } catch (Exception e) {
            logger.warn("Error consultando Strapi para {}: {}", codigo, e.getMessage());
        }
        return null;
    }

    private void enviar(String destinatario, String asunto, String html) {
        try {
            logger.info("Enviando email a {} con asunto: {}", destinatario, asunto);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(gmailUsername);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(html, true);
            mailSender.send(message);
            logger.info("Email enviado exitosamente a {}", destinatario);
        } catch (Exception e) {
            logger.error("Error enviando email a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String reemplazar(String texto, Map<String, String> vars) {
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            texto = texto.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return texto;
    }

    public void enviarBienvenida(String destinatario, String nombreUsuario) {
        Map<String, String> msg = obtenerMensaje("BIENVENIDA");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombre", nombreUsuario)));
        } else {
            logger.warn("No se pudo obtener mensaje BIENVENIDA de Strapi, email no enviado.");
        }
    }

    public void enviarConfirmacionReserva(String destinatario, String nombreUsuario, String parqueadero, String fecha) {
        Map<String, String> msg = obtenerMensaje("CONFIRMACION_RESERVA");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombre", nombreUsuario, "parqueadero", parqueadero, "fecha", fecha)));
        } else {
            logger.warn("No se pudo obtener mensaje CONFIRMACION_RESERVA de Strapi, email no enviado.");
        }
    }

    public void enviarNuevaReservaDuenio(String destinatario, String nombreDuenio, String parqueadero, String conductor, String placa, String fecha) {
        Map<String, String> msg = obtenerMensaje("NUEVA_RESERVA_DUENIO");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombreDuenio", nombreDuenio, "parqueadero", parqueadero, "conductor", conductor, "placa", placa, "fecha", fecha)));
        } else {
            logger.warn("No se pudo obtener mensaje NUEVA_RESERVA_DUENIO de Strapi, email no enviado.");
        }
    }

    public void enviarEstadiaFinalizada(String destinatario, String nombreUsuario, String parqueadero, String total, String inicio, String fin) {
        Map<String, String> msg = obtenerMensaje("ESTADIA_FINALIZADA");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombre", nombreUsuario, "parqueadero", parqueadero, "total", total, "inicio", inicio, "fin", fin)));
        } else {
            logger.warn("No se pudo obtener mensaje ESTADIA_FINALIZADA de Strapi, email no enviado.");
        }
    }

    public void enviarRechazoReserva(String destinatario, String nombreUsuario, String parqueadero, String motivo) {
        Map<String, String> msg = obtenerMensaje("RECHAZO_RESERVA");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombre", nombreUsuario, "parqueadero", parqueadero, "motivo", motivo)));
        } else {
            logger.warn("No se pudo obtener mensaje RECHAZO_RESERVA de Strapi, email no enviado.");
        }
    }

    public void enviarPagoFinalizado(String destinatario, String nombreUsuario, String parqueadero, String total) {
        Map<String, String> msg = obtenerMensaje("PAGO_FINALIZADO");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombre", nombreUsuario, "parqueadero", parqueadero, "total", total)));
        } else {
            logger.warn("No se pudo obtener mensaje PAGO_FINALIZADO de Strapi, email no enviado.");
        }
    }
}