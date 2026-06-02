package co.edu.uco.easypark.infrastructure.persistence.repository;

import co.edu.uco.easypark.domain.model.EstadoParqueadero;
import co.edu.uco.easypark.infrastructure.persistence.entity.ParqueaderoEntity;
import co.edu.uco.easypark.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParqueaderoRepository extends JpaRepository<ParqueaderoEntity, UUID> {

    List<ParqueaderoEntity> findByEstado(EstadoParqueadero estado);

    List<ParqueaderoEntity> findByDuenio(UsuarioEntity duenio);

    List<ParqueaderoEntity> findByEstadoAndDisponible(EstadoParqueadero estado, boolean disponible);
}