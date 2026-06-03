package co.edu.uco.easypark.infrastructure.email;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;

    @Value("${resend.from.email:onboarding@resend.dev}")
    private String fromEmail;

    public EmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void enviarConfirmacionReserva(String destinatario, String nombreUsuario, String parqueadero, String fecha) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(destinatario)
                    .subject("✅ Reserva confirmada - EasyPark")
                    .html("<h2>Hola " + nombreUsuario + "</h2>" +
                          "<p>Tu reserva en <strong>" + parqueadero + "</strong> ha sido confirmada.</p>" +
                          "<p>Fecha: <strong>" + fecha + "</strong></p>" +
                          "<p>Gracias por usar EasyPark.</p>")
                    .build();
            resend.emails().send(params);
        } catch (ResendException e) {
            System.err.println("Error enviando email: " + e.getMessage());
        }
    }

    public void enviarRechazoReserva(String destinatario, String nombreUsuario, String parqueadero, String motivo) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(destinatario)
                    .subject("❌ Reserva rechazada - EasyPark")
                    .html("<h2>Hola " + nombreUsuario + "</h2>" +
                          "<p>Tu reserva en <strong>" + parqueadero + "</strong> ha sido rechazada.</p>" +
                          "<p>Motivo: <strong>" + motivo + "</strong></p>" +
                          "<p>Por favor intenta con otro parqueadero.</p>")
                    .build();
            resend.emails().send(params);
        } catch (ResendException e) {
            System.err.println("Error enviando email: " + e.getMessage());
        }
    }

    public void enviarBienvenida(String destinatario, String nombreUsuario) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(destinatario)
                    .subject("👋 Bienvenido a EasyPark")
                    .html("<h2>Bienvenido " + nombreUsuario + "</h2>" +
                          "<p>Tu cuenta ha sido creada exitosamente en EasyPark.</p>" +
                          "<p>Ya puedes reservar espacios de parqueadero de forma fácil y rápida.</p>")
                    .build();
            resend.emails().send(params);
        } catch (ResendException e) {
            System.err.println("Error enviando email: " + e.getMessage());
        }
    }
}
