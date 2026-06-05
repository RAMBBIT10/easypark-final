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
    public void enviarBienvenida(String destinatario, String nombreUsuario) {
        enviar(destinatario,
            "Bienvenido a EasyPark",
            "<h2>Bienvenido " + nombreUsuario + "</h2>" +
            "<p>Tu cuenta ha sido creada exitosamente en EasyPark.</p>" +
            "<p>Ya puedes reservar espacios de parqueadero de forma facil y rapida.</p>");
    }
    public void enviarConfirmacionReserva(String destinatario, String nombreUsuario, String parqueadero, String fecha) {
        enviar(destinatario,
            "Reserva confirmada - EasyPark",
            "<h2>Hola " + nombreUsuario + "</h2>" +
            "<p>Tu reserva en <strong>" + parqueadero + "</strong> ha sido confirmada.</p>" +
            "<p>Fecha de inicio: <strong>" + fecha + "</strong></p>" +
            "<p>Gracias por usar EasyPark.</p>");
    }
    public void enviarNuevaReservaDuenio(String destinatario, String nombreDuenio, String parqueadero, String conductor, String placa, String fecha) {
        enviar(destinatario,
            "Nueva reserva en tu parqueadero - EasyPark",
            "<h2>Hola " + nombreDuenio + "</h2>" +
            "<p>El conductor <strong>" + conductor + "</strong> acaba de reservar en <strong>" + parqueadero + "</strong>.</p>" +
            "<p>Placa: <strong>" + placa + "</strong></p>" +
            "<p>Fecha: <strong>" + fecha + "</strong></p>");
    }
    public void enviarEstadiaFinalizada(String destinatario, String nombreUsuario, String parqueadero, String total, String inicio, String fin) {
        enviar(destinatario,
            "Estadia finalizada - EasyPark",
            "<h2>Hola " + nombreUsuario + "</h2>" +
            "<p>Tu estadia en <strong>" + parqueadero + "</strong> ha finalizado.</p>" +
            "<p>Inicio: <strong>" + inicio + "</strong></p>" +
            "<p>Fin: <strong>" + fin + "</strong></p>" +
            "<p>Total a pagar: <strong>$" + total + "</strong></p>" +
            "<p>Por favor confirma el pago en la aplicacion.</p>");
    }
    public void enviarRechazoReserva(String destinatario, String nombreUsuario, String parqueadero, String motivo) {
        enviar(destinatario,
            "Reserva cancelada - EasyPark",
            "<h2>Hola " + nombreUsuario + "</h2>" +
            "<p>Tu reserva en <strong>" + parqueadero + "</strong> ha sido cancelada.</p>" +
            "<p>Motivo: <strong>" + motivo + "</strong></p>");
    }
    public void enviarPagoFinalizado(String destinatario, String nombreUsuario, String parqueadero, String total) {
        enviar(destinatario,
            "Pago confirmado - EasyPark",
            "<h2>Hola " + nombreUsuario + "</h2>" +
            "<p>El pago de tu reserva en <strong>" + parqueadero + "</strong> ha sido confirmado por ambas partes.</p>" +
            "<p>Total pagado: <strong>$" + total + "</strong></p>" +
            "<p>Gracias por usar EasyPark.</p>");
    }
}