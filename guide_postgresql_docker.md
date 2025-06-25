# Guía rápida: PostgreSQL con Docker, Spring Boot y pgAdmin

## 1. ¿Qué hace el `docker-compose.yml`?

- Levanta un contenedor de PostgreSQL con la base de datos `ecommerce_db`.
- Crea un volumen para persistir los datos.
- Levanta pgAdmin para administrar la base de datos desde el navegador (http://localhost:5050).

## 2. Configuración en `application.yml`

- Spring Boot se conecta a la base de datos de Docker usando:
  - `url: jdbc:postgresql://localhost:5432/ecommerce_db`
  - Usuario: `${DB_USERNAME}` (postgres)
  - Contraseña: `${DB_PASSWORD}` (root)
- `ddl-auto: update` permite que Spring cree y actualice las tablas automáticamente sin borrar los datos.

## 3. Diferencia entre PostgreSQL local y Docker

- Si tienes PostgreSQL instalado localmente y también usas Docker, puedes tener dos bases de datos distintas en el mismo puerto.
- Asegúrate de que tu backend y pgAdmin apunten al contenedor de Docker (puerto 5432) para evitar confusiones.
- Si pgAdmin no muestra las tablas, revisa que esté conectado al contenedor correcto y no a la instancia local.

## 4. Uso de pgAdmin

- Accede a http://localhost:5050
- Crea un nuevo servidor:
  - Si usas pgAdmin desde el mismo docker-compose, pon como host: `db` (nombre del servicio)
  - Si usas pgAdmin desde fuera de Docker, pon como host: `localhost`
  - Puerto: `5432`, usuario: `postgres`, contraseña: `root`
- La base de datos a visualizar es `ecommerce_db`.
- Si no ves las tablas, refresca la vista o revisa la conexión.

## 5. Notas útiles

- Spring Boot crea las tablas automáticamente al arrancar si la conexión es correcta.
- Puedes poblar la base de datos usando el frontend, Postman o un seeder en Spring.
- Si tienes problemas, revisa los logs del backend y asegúrate de no tener dos PostgreSQL corriendo a la vez. #mepasó

---

Cualquier duda, revisa este documento o pregunta a tu compañero que tiene menos idea todavía :)
