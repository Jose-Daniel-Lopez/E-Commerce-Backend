# E-Commerce Backend API

<div align="center">

![Java](https://img.shields.io/badge/Java-23-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

Una API REST completa para un sistema de e-commerce desarrollada con Spring Boot 3, que incluye gestión de productos, usuarios, carritos de compra, pedidos y más.

</div>

## Tabla de Contenidos

- [Características](#-características)
- [Tecnologías](#️-tecnologías)
- [Arquitectura](#️-arquitectura)
- [Instalación](#-instalación)
- [Configuración](#️-configuración)
- [Uso](#-uso)
- [API Endpoints](#-api-endpoints)
- [Base de Datos](#️-base-de-datos)
- [Seguridad](#-seguridad)
- [Desarrollo](#-desarrollo)
- [Contribuir](#-contribuir)
- [Licencia](#-licencia)

## Características

### 🛍️ Gestión de Productos
- CRUD completo de productos con variantes (smartphones, laptops, cámaras, etc.)
- Categorización y filtrado avanzado
- Sistema de reviews y calificaciones
- Productos relacionados y recomendaciones
- Soporte para múltiples imágenes

### 👥 Gestión de Usuarios
- Registro e inicio de sesión con JWT
- Perfiles de usuario personalizables
- Sistema de verificación de cuentas
- Gestión de direcciones de envío

### 🛒 Carrito y Pedidos
- Carrito de compras persistente
- Gestión de cantidades y variantes
- Proceso completo de checkout
- Historial de pedidos
- Estados de pedidos (pendiente, procesando, enviado, entregado)

### ➕ Características Adicionales
- Lista de deseos (wishlist)
- Códigos de descuento
- Integración con Pexels API para imágenes
- Rate limiting para protección de endpoints
- HATEOAS para navegación de API
- Paginación en todas las consultas

## 🛠️ Tecnologías

### Backend
- **Java 23** - Lenguaje de programación principal
- **Spring Boot 3.5.3** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - ORM y acceso a datos
- **Spring HATEOAS** - API hipermedia REST
- **JWT (JSON Web Tokens)** - Autenticación stateless

### Base de Datos
- **PostgreSQL 17** - Base de datos relacional
- **Docker Compose** - Orquestación de contenedores
- **pgAdmin 4** - Administración de base de datos

### Herramientas y Librerías
- **Maven** - Gestión de dependencias y build
- **Lombok** - Reducción de código boilerplate
- **JavaFaker** - Generación de datos de prueba
- **Bucket4j** - Rate limiting
- **dotenv-java** - Gestión de variables de entorno

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura en capas** con los siguientes componentes:

```
src/main/java/com/app/
├── 📂 controllers/     # Controladores REST (API endpoints)
├── 📂 services/        # Lógica de negocio
├── 📂 repositories/    # Acceso a datos (JPA)
├── 📂 entities/        # Entidades JPA (modelos de datos)
├── 📂 DTO/            # Data Transfer Objects
├── 📂 security/       # Configuración de seguridad JWT
├── 📂 config/         # Configuraciones de Spring
├── 📂 hateoas/        # Representaciones HATEOAS
└── 📂 data/seeder/    # Seeders para datos iniciales
```

### ⚠️ Entidades Principales
- **User** - Usuarios del sistema
- **Product** - Productos con variantes
- **Category** - Categorías de productos
- **Cart / CartItem** - Carrito de compras
- **Order / OrderItem** - Pedidos e items
- **ProductReview** - Reviews de productos
- **Wishlist** - Lista de deseos
- **ShippingAddress** - Direcciones de envío
- **DiscountCode** - Códigos de descuento

## 🚀 Instalación

### Prerrequisitos
- Java 23+
- Maven 3.6+
- Docker y Docker Compose
- Git

### 1. Clonar el repositorio
```bash
git clone https://github.com/Jose-Daniel-Lopez/E-Commerce-Backend.git
cd E-Commerce-Backend
```

### 2. Configurar variables de entorno
```bash
cp src/main/resources/.env.example src/main/resources/.env
```

Edita el archivo `.env` con tus credenciales:
```env
# PostgreSQL Database Configuration
DB_USERNAME=postgres
DB_PASSWORD=root

# Pexels API Configuration (opcional)
PEXELS_API_KEY=tu_pexels_api_key_aqui
```

### 3. Levantar la base de datos
```bash
docker-compose up -d
```

### 4. Compilar y ejecutar la aplicación
```bash
# Opción 1: Usando el script
./build.sh
./run-server.sh

# Opción 2: Usando Maven directamente
./mvnw clean package -DskipTests
./mvnw spring-boot:run

# Opción 3: Ejecutar el JAR compilado
java -jar target/E-Commerce-App-0.0.1-SNAPSHOT.jar
```

## ⚙️ Configuración

### Base de Datos
La aplicación está configurada para usar PostgreSQL. El `docker-compose.yml` incluye:
- **PostgreSQL 17** en el puerto 5432
- **pgAdmin 4** en el puerto 5050 (http://localhost:5050)

### Variables de Entorno
| Variable | Descripción | Requerida |
|----------|-------------|-----------|
| `DB_USERNAME` | Usuario de PostgreSQL | ✅ |
| `DB_PASSWORD` | Contraseña de PostgreSQL | ✅ |
| `PEXELS_API_KEY` | API key para imágenes de Pexels | ❌ |

### Propiedades de la Aplicación
- **Puerto**: 8080 (por defecto)
- **Base URL**: `http://localhost:8080`
- **Documentación**: Las APIs están autodocumentadas con HATEOAS

## 📖 Uso

### Iniciar la aplicación

1. **Levantar base de datos**:
   ```bash
   docker-compose up -d
   ```

2. **Ejecutar aplicación**:
   ```bash
   ./run-server.sh
   ```

3. **Verificar estado**:
   - API: http://localhost:8080
   - pgAdmin: http://localhost:5050

### Acceso a pgAdmin
- **URL**: http://localhost:5050
- **Email**: admin@admin.com
- **Password**: admin

Para conectar al servidor PostgreSQL desde pgAdmin:
- **Host**: `db` (si pgAdmin está en Docker) o `localhost`
- **Puerto**: 5432
- **Usuario**: postgres
- **Contraseña**: root
- **Base de datos**: ecommerce_db

## 🔌 API Endpoints

### 🔐 Autenticación
```
POST   /api/auth/login          # Iniciar sesión
POST   /api/auth/register       # Registrar usuario
```

### 👤 Usuarios
```
GET    /api/users              # Listar usuarios
GET    /api/users/{id}         # Obtener usuario por ID
PUT    /api/users/{id}         # Actualizar usuario
DELETE /api/users/{id}         # Eliminar usuario
PATCH  /api/users/{id}/password # Cambiar contraseña
```

### 🛍️ Productos
```
GET    /api/products                    # Listar productos (paginado)
GET    /api/products/{id}              # Obtener producto por ID
GET    /api/products/category/{categoryId} # Productos por categoría
GET    /api/products/featured          # Productos destacados
GET    /api/products/newly-added       # Productos recién agregados
GET    /api/products/{id}/related      # Productos relacionados
```

### 🛒 Carrito
```
GET    /api/cart/{userId}              # Obtener carrito del usuario
POST   /api/cartItems                  # Agregar item al carrito
PUT    /api/cartItems/{id}/quantity    # Actualizar cantidad
DELETE /api/cartItems/{id}             # Eliminar item del carrito
```

### 📦 Pedidos
```
GET    /api/orders                     # Listar pedidos
POST   /api/orders                     # Crear nueva orden
GET    /api/orders/{id}               # Obtener orden por ID
PUT    /api/orders/{id}/status        # Actualizar estado de orden
```

### 💝 Wishlist
```
GET    /api/wishlists/{userId}         # Obtener wishlist del usuario
POST   /api/wishlists                  # Agregar producto a wishlist
DELETE /api/wishlists/{id}             # Eliminar de wishlist
```

### ⭐ Reviews
```
GET    /api/reviews/product/{productId} # Reviews de un producto
POST   /api/reviews                     # Crear review
PUT    /api/reviews/{id}               # Actualizar review
DELETE /api/reviews/{id}               # Eliminar review
```

### 📍 Direcciones
```
GET    /api/shippingAddresses/{userId} # Direcciones del usuario
POST   /api/shippingAddresses          # Crear dirección
PUT    /api/shippingAddresses/{id}     # Actualizar dirección
DELETE /api/shippingAddresses/{id}     # Eliminar dirección
```

## 🗄️ Base de Datos

### Modelo de Datos
El sistema utiliza un modelo relacional con las siguientes entidades principales:

- **Users**: Información de usuarios y autenticación
- **Products**: Catálogo de productos con variantes
- **Categories**: Categorización de productos
- **Orders**: Pedidos de compra con items
- **Cart**: Carrito persistente por usuario
- **ProductReviews**: Sistema de calificaciones
- **Wishlist**: Lista de productos deseados
- **ShippingAddresses**: Direcciones de envío
- **DiscountCodes**: Códigos promocionales

### Seeding
El proyecto incluye seeders automáticos que populan la base de datos con datos de prueba:
- Usuarios de ejemplo
- Productos con múltiples categorías
- Reviews y calificaciones
- Direcciones de envío

## 🔒 Seguridad

### Autenticación JWT
- **Autenticación**: Basada en JSON Web Tokens
- **Autorización**: Control de acceso por roles
- **CORS**: Configurado para desarrollo y producción
- **Rate Limiting**: Protección contra ataques de fuerza bruta

### Endpoints Protegidos
La mayoría de endpoints requieren autenticación JWT excepto:
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/products/*` (endpoints públicos de productos)

## 🔧 Desarrollo

### Estructura del Proyecto
```
E-Commerce-Backend/
├── 📄 pom.xml                    # Configuración Maven
├── 📄 docker-compose.yml         # Servicios Docker
├── 📄 application.properties     # Configuración Spring
├── 📄 .env.example              # Variables de entorno ejemplo
├── 📜 build.sh                  # Script de compilación
├── 📜 run-server.sh             # Script de ejecución
├── 📂 src/main/java/com/app/    # Código fuente principal
├── 📂 src/test/java/            # Tests unitarios
└── 📂 target/                   # Archivos compilados
```

### Comandos Útiles

**Desarrollo**:
```bash
./mvnw spring-boot:run           # Ejecutar en modo desarrollo
./mvnw clean compile             # Compilar código
./mvnw test                      # Ejecutar tests
```

**Producción**:
```bash
./build.sh                      # Compilar para producción
./run-server.sh                 # Ejecutar aplicación
```

**Base de Datos**:
```bash
docker-compose up -d             # Levantar PostgreSQL y pgAdmin
docker-compose down              # Detener servicios
docker-compose logs db           # Ver logs de PostgreSQL
```

### Testing
```bash
./mvnw test                      # Ejecutar todos los tests
./mvnw test -Dtest=ProductControllerTest # Ejecutar test específico
```

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Convenciones de Código
- Seguir las convenciones de Java/Spring Boot
- Usar Lombok para reducir boilerplate
- Documentar APIs con Javadoc
- Incluir tests para nuevas funcionalidades

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Equipo de Desarrollo

Desarrollador principal: **[`Jose`](https://github.com/Jose-Daniel-Lopez)**.

Colaborador: **[`Adan`](https://github.com/Adan-Perez)**.

---

## 🆘 Soporte

Si tienes problemas con la configuración, revisa:

1. **Guía de PostgreSQL**: [`guide_postgresql_docker.md`](guide_postgresql_docker.md)
2. **Logs de la aplicación**: `docker-compose logs`
3. **Estado de los servicios**: `docker-compose ps`

### Problemas Comunes

**Error de conexión a BD**:
```bash
# Verificar que PostgreSQL esté ejecutándose
docker-compose ps

# Reiniciar servicios si es necesario
docker-compose restart
```

**Puerto en uso**:
```bash
# Verificar qué proceso está usando el puerto 8080
lsof -i :8080

# O usar un puerto diferente en application.properties
server.port=8081
```

**Variables de entorno no cargadas**:
- Verificar que el archivo `.env` exista en `src/main/resources/`
- Comprobar que las variables estén en el formato correcto

---

<div align="center">

**¿Te gusta el proyecto? ¡Dale una ⭐!**

[Reportar Bug](../../issues) • [Solicitar Feature](../../issues)

</div>
