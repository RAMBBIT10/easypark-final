package co.edu.uco.easypark.features.reserva.application;

import java.util.List;
import java.util.UUID;

public interface IReservaUseCase {

    ReservaResponse crear(ReservaRequest request, String emailConductor);

    List<ReservaResponse> listarMisReservas(String emailConductor);

    
    ReservaResponse finalizarEstadia(UUID id, String emailConductor);

    
    ReservaResponse conductorConfirmaPago(UUID id, String emailConductor);

    
    ReservaResponse duenioConfirmaPago(UUID id, String emailDuenio);

    ReservaResponse cancelar(UUID id, String emailConductor);

    List<ReservaResponse> listarReservasDeMisParqueaderos(String emailDuenio);

    List<ReservaResponse> listarTodas();
}