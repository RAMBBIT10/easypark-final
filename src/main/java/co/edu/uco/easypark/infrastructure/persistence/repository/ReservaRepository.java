package co.edu.uco.easypark.infrastructure.persistence.repository;

import co.edu.uco.easypark.domain.model.EstadoReserva;
import co.edu.uco.easypark.infrastructure.persistence.entity.ParqueaderoEntity;
import co.edu.uco.easypark.infrastructure.persistence.entity.ReservaEntity;
import co.edu.uco.easypark.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, UUID> {
    List<ReservaEntity> findByConductor(UsuarioEntity conductor);
    List<ReservaEntity> findByParqueadero(ParqueaderoEntity parqueadero);
    List<ReservaEntity> findByConductorAndEstado(UsuarioEntity conductor, EstadoReserva estado);
    List<ReservaEntity> findByParqueaderoIdAndEstado(UUID parqueaderoId, EstadoReserva estado);

    @Query("SELECT r FROM ReservaEntity r WHERE r.parqueadero = :parqueadero " +
           "AND r.estado IN ('CONFIRMADA', 'EN_CURSO') " +
           "AND NOT (r.fechaFin <= :inicio OR r.fechaInicio >= :fin)")
    List<ReservaEntity> findConflictos(
            @Param("parqueadero") ParqueaderoEntity parqueadero,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}