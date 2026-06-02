package co.edu.uco.easypark;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "jwt.secret=test-secret-key-must-be-at-least-32-chars-long-ok",
    "jwt.expiration=86400000",
    "firebase.project-id=test-project",
    "firebase.credentials-path=classpath:firebase-service-account.json",
    "mercadopago.access-token=TEST-token"
})
class EasyParkApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring Boot levanta correctamente
    }
}
