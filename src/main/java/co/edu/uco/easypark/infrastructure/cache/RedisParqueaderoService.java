package co.edu.uco.easypark.infrastructure.cache;

import co.edu.uco.easypark.crosscutting.specification.ParameterCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RedisParqueaderoService {

    private static final Logger logger = LoggerFactory.getLogger(RedisParqueaderoService.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisParqueaderoService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void guardar(UUID parqueaderoId, Object data) {
        String key = ParameterCatalog.CACHE_PARQUEADERO_ID + parqueaderoId;
        try {
            redisTemplate.opsForValue().set(key, data, Duration.ofSeconds(ParameterCatalog.CACHE_TTL_SECONDS));
            logger.debug("Parqueadero cached: {}", key);
        } catch (Exception e) {
            logger.warn("Error caching parqueadero {}: {}", parqueaderoId, e.getMessage());
        }
    }

    public Object obtener(UUID parqueaderoId) {
        String key = ParameterCatalog.CACHE_PARQUEADERO_ID + parqueaderoId;
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.warn("Error getting cached parqueadero {}: {}", parqueaderoId, e.getMessage());
            return null;
        }
    }

    public void eliminar(UUID parqueaderoId) {
        String key = ParameterCatalog.CACHE_PARQUEADERO_ID + parqueaderoId;
        try {
            redisTemplate.delete(key);
            logger.debug("Parqueadero removed from cache: {}", key);
        } catch (Exception e) {
            logger.warn("Error removing cached parqueadero {}: {}", parqueaderoId, e.getMessage());
        }
    }

    public boolean bloquear(UUID parqueaderoId, long ttlSeconds) {
        String lockKey = "lock:parqueadero:" + parqueaderoId;
        try {
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", Duration.ofSeconds(ttlSeconds));
            return Boolean.TRUE.equals(locked);
        } catch (Exception e) {
            logger.warn("Error locking parqueadero {}: {}", parqueaderoId, e.getMessage());
            return false;
        }
    }

    public void liberarBloqueo(UUID parqueaderoId) {
        String lockKey = "lock:parqueadero:" + parqueaderoId;
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            logger.warn("Error releasing lock for parqueadero {}: {}", parqueaderoId, e.getMessage());
        }
    }
}