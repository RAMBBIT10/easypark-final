package co.edu.uco.easypark.infrastructure.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FirebaseNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseNotificationService.class);

    @Async
    public void enviarNotificacion(String tokenFcm, String titulo, String cuerpo) {
        if (tokenFcm == null || tokenFcm.isBlank()) {
            logger.warn("FCM token vacío, notificación omitida");
            return;
        }
        try {
            Message message = Message.builder()
                    .setToken(tokenFcm)
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(cuerpo)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Notificación enviada exitosamente: {}", response);
        } catch (Exception e) {
            logger.error("Error enviando notificación FCM a {}: {}", tokenFcm, e.getMessage());
        }
    }

    @Async
    public void enviarNotificacionConDatos(String tokenFcm, String titulo, String cuerpo, Map<String, String> datos) {
        if (tokenFcm == null || tokenFcm.isBlank()) {
            logger.warn("FCM token vacío, notificación omitida");
            return;
        }
        try {
            Message.Builder builder = Message.builder()
                    .setToken(tokenFcm)
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(cuerpo)
                            .build());

            if (datos != null) {
                builder.putAllData(datos);
            }

            String response = FirebaseMessaging.getInstance().send(builder.build());
            logger.info("Notificación con datos enviada: {}", response);
        } catch (Exception e) {
            logger.error("Error enviando notificación FCM con datos: {}", e.getMessage());
        }
    }
}