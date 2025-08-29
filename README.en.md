# E-Commerce Backend API

<div align="center">

![Java](https://img.shields.io/badge/Java-23-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

A complete REST API for an e-commerce system developed with Spring Boot 3, including product management, users, shopping carts, orders, and more.

</div>

## Table of Contents

- [Features](#-features)
- [Technologies](#️-technologies)
- [Architecture](#️-architecture)
- [Installation](#-installation)
- [Configuration](#️-configuration)
- [Usage](#-usage)
- [API Endpoints](#-api-endpoints)
- [Database](#️-database)
- [Security](#-security)
- [Development](#-development)
- [Contributing](#-contributing)
- [License](#-license)

## Features

### 🛍️ Product Management
- Complete CRUD operations for products with variants (smartphones, laptops, cameras, etc.)
- Advanced categorization and filtering
- Review and rating system
- Related products and recommendations
- Support for multiple images

### 👥 User Management
- Registration and login with JWT
- Customizable user profiles
- Account verification system
- Shipping address management

### 🛒 Cart and Orders
- Persistent shopping cart
- Quantity and variant management
- Complete checkout process
- Order history
- Order statuses (pending, processing, shipped, delivered)

### ➕ Additional Features
- Wishlist
- Discount codes
- Pexels API integration for images
- Rate limiting for endpoint protection
- HATEOAS for API navigation
- Pagination on all queries

## 🛠️ Technologies

### Backend
- **Java 23** - Main programming language
- **Spring Boot 3.5.3** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - ORM and data access
- **Spring HATEOAS** - Hypermedia REST API
- **JWT (JSON Web Tokens)** - Stateless authentication

### Database
- **PostgreSQL 17** - Relational database
- **Docker Compose** - Container orchestration
- **pgAdmin 4** - Database administration

### Tools and Libraries
- **Maven** - Dependency management and build
- **Lombok** - Boilerplate code reduction
- **JavaFaker** - Test data generation
- **Bucket4j** - Rate limiting
- **dotenv-java** - Environment variable management

## 🏗️ Architecture

The project follows a **layered architecture** with the following components:

```
src/main/java/com/app/
├── 📂 controllers/     # REST Controllers (API endpoints)
├── 📂 services/        # Business logic
├── 📂 repositories/    # Data access (JPA)
├── 📂 entities/        # JPA Entities (data models)
├── 📂 DTO/            # Data Transfer Objects
├── 📂 security/       # JWT security configuration
├── 📂 config/         # Spring configurations
├── 📂 hateoas/        # HATEOAS representations
└── 📂 data/seeder/    # Seeders for initial data
```

### ⚠️ Main Entities
- **User** - System users
- **Product** - Products with variants
- **Category** - Product categories
- **Cart / CartItem** - Shopping cart
- **Order / OrderItem** - Orders and items
- **ProductReview** - Product reviews
- **Wishlist** - Wishlist
- **ShippingAddress** - Shipping addresses
- **DiscountCode** - Discount codes

## 🚀 Installation

### Prerequisites
- Java 23+
- Maven 3.6+
- Docker and Docker Compose
- Git

### 1. Clone the repository
```bash
git clone https://github.com/Jose-Daniel-Lopez/E-Commerce-Backend.git
cd E-Commerce-Backend
```

### 2. Configure environment variables
```bash
cp src/main/resources/.env.example src/main/resources/.env
```

Edit the `.env` file with your credentials:
```env
# PostgreSQL Database Configuration
DB_USERNAME=postgres
DB_PASSWORD=root

# Pexels API Configuration (optional)
PEXELS_API_KEY=your_pexels_api_key_here
```

### 3. Start the database
```bash
docker-compose up -d
```

### 4. Build and run the application
```bash
# Option 1: Using the script
./build.sh
./run-server.sh

# Option 2: Using Maven directly
./mvnw clean package -DskipTests
./mvnw spring-boot:run

# Option 3: Run the compiled JAR
java -jar target/E-Commerce-App-0.0.1-SNAPSHOT.jar
```

## ⚙️ Configuration

### Database
The application is configured to use PostgreSQL. The `docker-compose.yml` includes:
- **PostgreSQL 17** on port 5432
- **pgAdmin 4** on port 5050 (http://localhost:5050)

### Environment Variables
| Variable | Description | Required |
|----------|-------------|-----------|
| `DB_USERNAME` | PostgreSQL username | ✅ |
| `DB_PASSWORD` | PostgreSQL password | ✅ |
| `PEXELS_API_KEY` | API key for Pexels images | ❌ |

### Application Properties
- **Port**: 8080 (default)
- **Base URL**: `http://localhost:8080`
- **Documentation**: APIs are self-documented with HATEOAS

## 📖 Usage

### Start the application

1. **Start database**:
   ```bash
   docker-compose up -d
   ```

2. **Run application**:
   ```bash
   ./run-server.sh
   ```

3. **Verify status**:
   - API: http://localhost:8080
   - pgAdmin: http://localhost:5050

### pgAdmin Access
- **URL**: http://localhost:5050
- **Email**: admin@admin.com
- **Password**: admin

To connect to the PostgreSQL server from pgAdmin:
- **Host**: `db` (if pgAdmin is in Docker) or `localhost`
- **Port**: 5432
- **Username**: postgres
- **Password**: root
- **Database**: ecommerce_db

## 🔌 API Endpoints

### 🔐 Authentication
```
POST   /api/auth/login          # Login
POST   /api/auth/register       # Register user
```

### 👤 Users
```
GET    /api/users              # List users
GET    /api/users/{id}         # Get user by ID
PUT    /api/users/{id}         # Update user
DELETE /api/users/{id}         # Delete user
PATCH  /api/users/{id}/password # Change password
```

### 🛍️ Products
```
GET    /api/products                    # List products (paginated)
GET    /api/products/{id}              # Get product by ID
GET    /api/products/category/{categoryId} # Products by category
GET    /api/products/featured          # Featured products
GET    /api/products/newly-added       # Newly added products
GET    /api/products/{id}/related      # Related products
```

### 🛒 Cart
```
GET    /api/cart/{userId}              # Get user's cart
POST   /api/cartItems                  # Add item to cart
PUT    /api/cartItems/{id}/quantity    # Update quantity
DELETE /api/cartItems/{id}             # Remove cart item
```

### 📦 Orders
```
GET    /api/orders                     # List orders
POST   /api/orders                     # Create new order
GET    /api/orders/{id}               # Get order by ID
PUT    /api/orders/{id}/status        # Update order status
```

### 💝 Wishlist
```
GET    /api/wishlists/{userId}         # Get user's wishlist
POST   /api/wishlists                  # Add product to wishlist
DELETE /api/wishlists/{id}             # Remove from wishlist
```

### ⭐ Reviews
```
GET    /api/reviews/product/{productId} # Product reviews
POST   /api/reviews                     # Create review
PUT    /api/reviews/{id}               # Update review
DELETE /api/reviews/{id}               # Delete review
```

### 📍 Addresses
```
GET    /api/shippingAddresses/{userId} # User's addresses
POST   /api/shippingAddresses          # Create address
PUT    /api/shippingAddresses/{id}     # Update address
DELETE /api/shippingAddresses/{id}     # Delete address
```

## 🗄️ Database

### Data Model
The system uses a relational model with the following main entities:

- **Users**: User information and authentication
- **Products**: Product catalog with variants
- **Categories**: Product categorization
- **Orders**: Purchase orders with items
- **Cart**: Persistent cart per user
- **ProductReviews**: Rating system
- **Wishlist**: Desired products list
- **ShippingAddresses**: Shipping addresses
- **DiscountCodes**: Promotional codes

### Seeding
The project includes automatic seeders that populate the database with test data:
- Example users
- Products with multiple categories
- Reviews and ratings
- Shipping addresses

## 🔒 Security

### JWT Authentication
- **Authentication**: Based on JSON Web Tokens
- **Authorization**: Role-based access control
- **CORS**: Configured for development and production
- **Rate Limiting**: Protection against brute force attacks

### Protected Endpoints
Most endpoints require JWT authentication except:
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/products/*` (public product endpoints)

## 🔧 Development

### Project Structure
```
E-Commerce-Backend/
├── 📄 pom.xml                    # Maven configuration
├── 📄 docker-compose.yml         # Docker services
├── 📄 application.properties     # Spring configuration
├── 📄 .env.example              # Environment variables example
├── 📜 build.sh                  # Build script
├── 📜 run-server.sh             # Run script
├── 📂 src/main/java/com/app/    # Main source code
├── 📂 src/test/java/            # Unit tests
└── 📂 target/                   # Compiled files
```

### Useful Commands

**Development**:
```bash
./mvnw spring-boot:run           # Run in development mode
./mvnw clean compile             # Compile code
./mvnw test                      # Run tests
```

**Production**:
```bash
./build.sh                      # Build for production
./run-server.sh                 # Run application
```

**Database**:
```bash
docker-compose up -d             # Start PostgreSQL and pgAdmin
docker-compose down              # Stop services
docker-compose logs db           # View PostgreSQL logs
```

### Testing
```bash
./mvnw test                      # Run all tests
./mvnw test -Dtest=ProductControllerTest # Run specific test
```

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Conventions
- Follow Java/Spring Boot conventions
- Use Lombok to reduce boilerplate
- Document APIs with Javadoc
- Include tests for new features

## 📝 License

This project is under the MIT License - see the [LICENSE](LICENSE) file for more details.

## 👥 Development Team

Main Developer: **[`Jose`](https://github.com/Jose-Daniel-Lopez)**.

Collaborator: **[`Adan`](https://github.com/Adan-Perez)**.

---

## 🆘 Support

If you have configuration issues, check:

1. **PostgreSQL Guide**: [`guide_postgresql_docker.md`](guide_postgresql_docker.md)
2. **Application logs**: `docker-compose logs`
3. **Service status**: `docker-compose ps`

### Common Issues

**Database connection error**:
```bash
# Verify PostgreSQL is running
docker-compose ps

# Restart services if necessary
docker-compose restart
```

**Port in use**:
```bash
# Check which process is using port 8080
lsof -i :8080

# Or use a different port in application.properties
server.port=8081
```

**Environment variables not loaded**:
- Verify that the `.env` file exists in `src/main/resources/`
- Check that variables are in the correct format

---

<div align="center">

**Like the project? Give it a ⭐!**

[Report Bug](../../issues) • [Request Feature](../../issues)

</div>
