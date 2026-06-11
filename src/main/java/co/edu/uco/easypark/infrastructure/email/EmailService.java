package co.edu.uco.easypark.infrastructure.email;

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
            Map response = restTemplate.getForObject(url, Map.class);
            List<Map> data = (List<Map>) response.get("data");
            if (data != null && !data.isEmpty()) {
                return (Map<String, String>) data.get(0);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private void enviar(String destinatario, String asunto, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(gmailUsername);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
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
            enviar(destinatario, "Bienvenido a EasyPark", "<h2>Bienvenido " + nombreUsuario + "</h2><p>Tu cuenta ha sido creada exitosamente.</p>");
        }
    }

    public void enviarConfirmacionReserva(String destinatario, String nombreUsuario, String parqueadero, String fecha) {
        Map<String, String> msg = obtenerMensaje("CONFIRMACION_RESERVA");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombre", nombreUsuario, "parqueadero", parqueadero, "fecha", fecha)));
        } else {
            enviar(destinatario, "Reserva confirmada - EasyPark", "<h2>Hola " + nombreUsuario + "</h2><p>Tu reserva en " + parqueadero + " fue confirmada.</p>");
        }
    }

    public void enviarNuevaReservaDuenio(String destinatario, String nombreDuenio, String parqueadero, String conductor, String placa, String fecha) {
        Map<String, String> msg = obtenerMensaje("NUEVA_RESERVA_DUENIO");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombreDuenio", nombreDuenio, "parqueadero", parqueadero, "conductor", conductor, "placa", placa, "fecha", fecha)));
        } else {
            enviar(destinatario, "Nueva reserva - EasyPark", "<h2>Hola " + nombreDuenio + "</h2><p>" + conductor + " reservó en " + parqueadero + ".</p>");
        }
    }

    public void enviarEstadiaFinalizada(String destinatario, String nombreUsuario, String parqueadero, String total, String inicio, String fin) {
        Map<String, String> msg = obtenerMensaje("ESTADIA_FINALIZADA");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombre", nombreUsuario, "parqueadero", parqueadero, "total", total, "inicio", inicio, "fin", fin)));
        } else {
            enviar(destinatario, "Estadia finalizada - EasyPark", "<h2>Hola " + nombreUsuario + "</h2><p>Total: $" + total + "</p>");
        }
    }

    public void enviarRechazoReserva(String destinatario, String nombreUsuario, String parqueadero, String motivo) {
        Map<String, String> msg = obtenerMensaje("RECHAZO_RESERVA");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombre", nombreUsuario, "parqueadero", parqueadero, "motivo", motivo)));
        } else {
            enviar(destinatario, "Reserva cancelada - EasyPark", "<h2>Hola " + nombreUsuario + "</h2><p>Tu reserva fue cancelada.</p>");
        }
    }

    public void enviarPagoFinalizado(String destinatario, String nombreUsuario, String parqueadero, String total) {
        Map<String, String> msg = obtenerMensaje("PAGO_FINALIZADO");
        if (msg != null) {
            enviar(destinatario, msg.get("asunto"), reemplazar(msg.get("cuerpo"), Map.of("nombre", nombreUsuario, "parqueadero", parqueadero, "total", total)));
        } else {
            enviar(destinatario, "Pago confirmado - EasyPark", "<h2>Hola " + nombreUsuario + "</h2><p>Pago de $" + total + " confirmado.</p>");
        }
    }
}