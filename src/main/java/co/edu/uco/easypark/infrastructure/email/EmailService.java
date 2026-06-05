package co.edu.uco.easypark.infrastructure.email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
@Service
public class EmailService {
    @Value("${GMAIL_USERNAME}")
    private String gmailUsername;
    @Value("${GMAIL_PASSWORD}")
    private String gmailPassword;
    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(gmailUsername, gmailPassword);
            }
        });
    }
    private void enviar(String destinatario, String asunto, String html) {
        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(gmailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setContent(html, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
    public void enviarConfirmacionReserva(String destinatario, String nombreUsuario, String parqueadero, String fecha) {
        enviar(destinatario,
            "Reserva confirmada - EasyPark",
            "<h2>Hola " + nombreUsuario + "</h2>" +
            "<p>Tu reserva en <strong>" + parqueadero + "</strong> ha sido confirmada.</p>" +
            "<p>Fecha: <strong>" + fecha + "</strong></p>" +
            "<p>Gracias por usar EasyPark.</p>");
    }
    public void enviarRechazoReserva(String destinatario, String nombreUsuario, String parqueadero, String motivo) {
        enviar(destinatario,
            "Reserva rechazada - EasyPark",
            "<h2>Hola " + nombreUsuario + "</h2>" +
            "<p>Tu reserva en <strong>" + parqueadero + "</strong> ha sido rechazada.</p>" +
            "<p>Motivo: <strong>" + motivo + "</strong></p>" +
            "<p>Por favor intenta con otro parqueadero.</p>");
    }
    public void enviarBienvenida(String destinatario, String nombreUsuario) {
        enviar(destinatario,
            "Bienvenido a EasyPark",
            "<h2>Bienvenido " + nombreUsuario + "</h2>" +
            "<p>Tu cuenta ha sido creada exitosamente en EasyPark.</p>" +
            "<p>Ya puedes reservar espacios de parqueadero de forma facil y rapida.</p>");
    }
}