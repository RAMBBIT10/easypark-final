# 🅿️ Easy Park — Backend

Sistema de parqueaderos privados comercial que conecta conductores con dueños de espacios privados.

## Stack

| Capa | Tecnología |
|------|-----------|
| Backend | Spring Boot 3.3 + Java 21 |
| Base de datos | PostgreSQL 17 |
| Caché / Bloqueo | Redis |
| Seguridad | Spring Security + JWT (JJWT 0.11.5) |
| WebSocket | Spring WebSocket (STOMP) |
| Notificaciones | Firebase FCM |
| Pagos | Mercado Pago |
| CI/CD | Docker + GitHub Actions + Railway |
| Documentación | Swagger / OpenAPI 3 |

---

## Requisitos previos

- Java 21
- Maven 3.9+
- PostgreSQL 17 (BD: `easy_park`, puerto 5432)
- Redis (puerto 6379)
- (Opcional) Docker

---

## Configuración rápida

```bash
# 1. Clonar / descomprimir el proyecto
# 2. Copiar variables de entorno
cp .env.example .env
# 3. Editar .env con tus credenciales reales

# 4. Crear la base de datos en PostgreSQL
psql -U postgres -c "CREATE DATABASE easy_park;"

# 5. Compilar y ejecutar
mvn spring-boot:run
```

La app levanta en `http://localhost:8080`

---

## Credenciales de admin por defecto

| Campo | Valor |
|-------|-------|
| Email | `admin@easypark.co` |
| Password | `admin123` |

> ⚠️ Cambia la contraseña en producción.

---

## Endpoints principales

### Autenticación (públicos)
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/register` | Registrar usuario (CONDUCTOR o DUENO) |
| POST | `/auth/login` | Login → retorna JWT |

### Parqueaderos
| Método | Ruta | Rol |
|--------|------|-----|
| GET | `/parqueaderos` | Público |
| POST | `/parqueaderos` | DUENO |
| GET | `/parqueaderos/mis-parqueaderos` | DUENO |
| PATCH | `/parqueaderos/{id}/disponibilidad` | DUENO |
| PATCH | `/parqueaderos/admin/{id}/aprobar` | ADMINISTRADOR |
| GET | `/parqueaderos/admin/pendientes` | ADMINISTRADOR |

### Reservas
| Método | Ruta | Rol |
|--------|------|-----|
| POST | `/reservas` | CONDUCTOR |
| GET | `/reservas/mis-reservas` | CONDUCTOR |
| PATCH | `/reservas/{id}/cancelar` | CONDUCTOR |
| PATCH | `/reservas/{id}/confirmar-pago` | ADMINISTRADOR |
| GET | `/reservas/admin/todas` | ADMINISTRADOR |

### Otros
| Ruta | Descripción |
|------|-------------|
| `ws://localhost:8080/ws/parqueaderos` | WebSocket tiempo real |
| `GET /actuator/health` | Health check |
| `GET /swagger-ui.html` | Documentación interactiva |

---

## Roles

| Rol | Puede hacer |
|-----|------------|
| `CONDUCTOR` | Buscar parqueaderos, crear y cancelar reservas |
| `DUENO` | Publicar y gestionar sus parqueaderos |
| `ADMINISTRADOR` | Aprobar parqueaderos, gestionar reservas y pagos |

---

## Firebase (notificaciones push)

1. En Firebase Console → Configuración del proyecto → Cuentas de servicio
2. Genera una nueva clave privada → descarga el JSON
3. Reemplaza `src/main/resources/firebase-service-account.json` con ese archivo
4. Actualiza `FIREBASE_PROJECT_ID` en `.env`

---

## Ejecutar con Docker

```bash
docker build -t easypark .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/easy_park \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=tu_password \
  -e JWT_SECRET=tu_secreto_seguro \
  easypark
```

---

## Importar en Eclipse

1. `File → Import → Maven → Existing Maven Projects`
2. Selecciona la carpeta raíz del proyecto
3. Click en `Finish`
4. Espera a que Maven descargue las dependencias
5. Configura las variables de entorno en `Run Configurations`

## Importar en VS Code

1. Abre la carpeta del proyecto
2. Instala la extensión **Extension Pack for Java**
3. Maven descargará las dependencias automáticamente
4. Usa el panel **Spring Boot Dashboard** para correr la app
