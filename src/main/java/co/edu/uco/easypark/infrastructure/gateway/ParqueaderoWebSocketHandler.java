package co.edu.uco.easypark.infrastructure.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class ParqueaderoWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ParqueaderoWebSocketHandler.class);
    private static final String TOPIC_PARQUEADEROS = "/topic/parqueaderos";

    private final SimpMessagingTemplate messagingTemplate;

    public ParqueaderoWebSocketHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notificarCambioDisponibilidad(UUID parqueaderoId, boolean disponible) {
        Map<String, Object> payload = Map.of(
                "parqueaderoId", parqueaderoId.toString(),
                "disponible", disponible,
                "tipo", "CAMBIO_DISPONIBILIDAD"
        );
        broadcast(payload);
        logger.info("WebSocket: parqueadero {} disponibilidad={}", parqueaderoId, disponible);
    }

    public void notificarNuevoParqueadero(UUID parqueaderoId, String nombre) {
        Map<String, Object> payload = Map.of(
                "parqueaderoId", parqueaderoId.toString(),
                "nombre", nombre,
                "tipo", "NUEVO_PARQUEADERO"
        );
        broadcast(payload);
        logger.info("WebSocket: nuevo parqueadero aprobado {}", parqueaderoId);
    }

    public void notificarReservaCreada(UUID parqueaderoId, UUID reservaId) {
        Map<String, Object> payload = Map.of(
                "parqueaderoId", parqueaderoId.toString(),
                "reservaId", reservaId.toString(),
                "tipo", "RESERVA_CREADA"
        );
        broadcast(payload);
    }

    private void broadcast(Object payload) {
        try {
            messagingTemplate.convertAndSend(TOPIC_PARQUEADEROS, payload);
        } catch (Exception e) {
            logger.error("Error broadcasting WebSocket message: {}", e.getMessage());
        }
    }
}