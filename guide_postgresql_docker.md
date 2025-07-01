# Guía rápida: PostgreSQL con Docker, Spring Boot y pgAdmin

## 1. ¿Qué hace el `docker-compose.yml`?

- Levanta un contenedor de PostgreSQL con la base de datos `ecommerce_db`.
- **Levanta tu aplicación Spring Boot en un contenedor** (puerto 8080).
- Levanta pgAdmin para administrar la base de datos desde el navegador (http://localhost:5050).
- Crea un volumen para persistir los datos.
- Conecta automáticamente todos los servicios entre sí.

## 2. Cómo usar Docker Compose

### Levantar todo el stack:
```bash
docker-compose up --build
```

### Solo levantar sin reconstruir:
```bash
docker-compose up
```

### Detener todos los servicios:
```bash
docker-compose down
```

### Ver logs de un servicio específico:
```bash
docker-compose logs app    # Backend
docker-compose logs db     # PostgreSQL
docker-compose logs pgadmin # pgAdmin
```

## 3. Configuración en `application.yml`

- Spring Boot se conecta automáticamente usando variables de entorno:
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/ecommerce_db` (usa `db` como host dentro de Docker)
  - Usuario: `${DB_USERNAME}` (postgres)
  - Contraseña: `${DB_PASSWORD}` (root)
- `ddl-auto: update` permite que Spring cree y actualice las tablas automáticamente sin borrar los datos.

## 4. Diferencia entre PostgreSQL local y Docker

- Si tienes PostgreSQL instalado localmente y también usas Docker, puedes tener dos bases de datos distintas en el mismo puerto.
- Asegúrate de que tu backend y pgAdmin apunten al contenedor de Docker (puerto 5432) para evitar confusiones.
- Si pgAdmin no muestra las tablas, revisa que esté conectado al contenedor correcto y no a la instancia local.

## 5. Uso de pgAdmin

- Accede a http://localhost:5050
- Crea un nuevo servidor:
  - Si usas pgAdmin desde el mismo docker-compose, pon como host: `db` (nombre del servicio)
  - Si usas pgAdmin desde fuera de Docker, pon como host: `localhost`
  - Puerto: `5432`, usuario: `postgres`, contraseña: `root`
- La base de datos a visualizar es `ecommerce_db`.
- Si no ves las tablas, refresca la vista o revisa la conexión.

## 6. Desarrollo con Docker vs Local

### Ventajas de usar Docker:
- ✅ Todo el equipo tiene el mismo entorno
- ✅ Un solo comando para levantar todo
- ✅ No hay conflictos con instalaciones locales
- ✅ Fácil de resetear si algo se rompe

### Comandos útiles:
- `docker-compose logs -f app` - Ver logs del backend en tiempo real
- `docker-compose restart app` - Reiniciar solo el backend
- `docker-compose down -v` - Borrar todo incluidos los datos

## 7. Notas útiles

- Spring Boot crea las tablas automáticamente al arrancar si la conexión es correcta.
- Puedes poblar la base de datos usando el frontend, Postman o un seeder en Spring.
- Si tienes problemas, revisa los logs del backend y asegúrate de no tener dos PostgreSQL corriendo a la vez. #mepasó

## 8. Desarrollo eficiente con Docker

### Opción 1: Desarrollo híbrido (RECOMENDADO)
- Ejecuta **solo la base de datos y pgAdmin** en Docker
- Ejecuta tu **backend Spring Boot localmente** (puerto 8080)

```bash
# Solo levantar BD y pgAdmin
docker-compose up db pgadmin

# En otra terminal, ejecutar Spring Boot localmente
./mvnw spring-boot:run
```

**Ventajas:**
- ✅ Cambios instantáneos con Spring DevTools
- ✅ Hot reload automático
- ✅ Base de datos consistente en Docker
- ✅ No reconstruir contenedor en cada cambio

### Opción 2: Volúmenes de desarrollo (Avanzado)
Modificar `docker-compose.yml` para desarrollo:

```yaml
app:
  build: .
  volumes:
    - ./src:/app/src  # Mapear código fuente
    - ./target:/app/target
  environment:
    - SPRING_DEVTOOLS_RESTART_ENABLED=true
```

### Opción 3: Solo Docker en producción
- Desarrollo: `./mvnw spring-boot:run` + `docker-compose up db pgadmin`
- Producción: `docker-compose up --build`

### Comandos para desarrollo híbrido:
```bash
# Levantar solo BD
docker-compose up db pgadmin -d

# Parar solo el backend (si está corriendo)
docker-compose stop app

# Ver logs de BD
docker-compose logs -f db
```

---

Cualquier duda, revisa este documento o pregunta a tu compañero que tiene menos idea todavía :)
