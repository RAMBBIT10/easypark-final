package co.edu.uco.easypark.infrastructure.persistence.repository;

import co.edu.uco.easypark.infrastructure.persistence.entity.NotificacionCatalogoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionCatalogoRepository extends JpaRepository<NotificacionCatalogoEntity, Long> {
}
