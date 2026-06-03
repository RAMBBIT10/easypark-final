package co.edu.uco.easypark.infrastructure.persistence.repository;

import co.edu.uco.easypark.infrastructure.persistence.entity.MensajeCatalogoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MensajeCatalogoRepository extends JpaRepository<MensajeCatalogoEntity, Long> {
    Optional<MensajeCatalogoEntity> findByCodigo(String codigo);
}
