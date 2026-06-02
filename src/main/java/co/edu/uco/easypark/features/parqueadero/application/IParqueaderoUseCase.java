package co.edu.uco.easypark.features.parqueadero.application;

import java.util.List;
import java.util.UUID;

public interface IParqueaderoUseCase {

    ParqueaderoResponse crear(ParqueaderoRequest request, String emailDuenio);

    List<ParqueaderoResponse> listarDisponibles();

    List<ParqueaderoResponse> listarMisParqueaderos(String emailDuenio);

    ParqueaderoResponse actualizarDisponibilidad(UUID id, boolean disponible, String emailDuenio);

    ParqueaderoResponse aprobar(UUID id);

    ParqueaderoResponse rechazar(UUID id, String motivo);

    List<ParqueaderoResponse> listarPendientes();
}