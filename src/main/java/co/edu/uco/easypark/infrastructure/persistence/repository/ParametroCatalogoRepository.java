package co.edu.uco.easypark.infrastructure.persistence.repository;

import co.edu.uco.easypark.infrastructure.persistence.entity.ParametroCatalogoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParametroCatalogoRepository extends JpaRepository<ParametroCatalogoEntity, Long> {
    Optional<ParametroCatalogoEntity> findByClave(String clave);
}
