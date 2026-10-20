
# 🛒 E-Commerce Microservices Platform

A full-stack E-Commerce application built using **Java, Spring Boot, Spring Security, JWT, PostgreSQL, Redis, React.js, and Docker**. The project follows a microservices-based architecture, separating user management, product management, inventory, cart, orders, and payment processing into independent services.

The application includes user authentication, product browsing, product image uploads, cart and order workflows, inventory management, and simulated payment processing. It also includes performance optimization using Redis caching and pagination, along with application monitoring through Spring Boot Actuator, Prometheus, and Grafana.

## 🌐 Live Demo

- **Frontend:** https://ecommerce-live-app-suji.netlify.app/
- **Backend:** Deployed as individual services on Render
- **Source Code:** https://github.com/sujitha769/ecommerce_backend

> Note: Payments in the demo are simulated. The application does not process real financial transactions.

---

## 📌 Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [System Architecture](#-system-architecture)
- [Technology Stack](#-technology-stack)
- [Microservices](#-microservices)
- [Service Ports](#-service-ports)
- [Project Structure](#-project-structure)
- [Application Workflow](#-application-workflow)
- [Prerequisites](#-prerequisites)
- [Environment Variables](#-environment-variables)
- [Local Setup](#-local-setup)
- [Docker Setup](#-docker-setup)
- [API Documentation](#-api-documentation)
- [Database Design](#-database-design)
- [Image Uploads](#-image-uploads)
- [Performance Optimization](#-performance-optimization)
- [Monitoring and Observability](#-monitoring-and-observability)
- [Deployment](#-deployment)
- [Testing](#-testing)
- [Security](#-security)
- [Troubleshooting](#-troubleshooting)
- [Future Improvements](#-future-improvements)
- [Author](#-author)

---

## 📖 Project Overview

This project demonstrates the development of an E-Commerce application using independently deployable Spring Boot microservices.

Instead of implementing the complete backend inside one monolithic application, the system separates responsibilities into services for users, products, inventory, cart, orders, and payments.

Each service is responsible for its own business functionality and can be configured and deployed independently.

The React frontend communicates with backend services through HTTP APIs. JWT-based authentication protects user-specific operations, while PostgreSQL provides persistent storage and Redis is used for caching selected product retrieval operations.

The application is designed to demonstrate:

- Microservices-based backend development
- REST API development using Spring Boot
- Authentication and authorization using JWT
- Relational database integration
- Inter-service communication
- Product image uploads using Cloudinary
- Redis caching and pagination
- Containerization using Docker Compose
- Application metrics and monitoring
- Frontend and backend deployment

---

## ✨ Features

### 👤 User Management
- User registration
- User login
- Password hashing using Spring Security
- JWT-based authentication
- Protected user-specific endpoints
- Authenticated user profile retrieval

### 📦 Product Management
- Create products
- Retrieve products
- Retrieve a product by ID
- Update product information
- Delete products
- Search products
- Filter products by category
- Paginated product retrieval
- Product image uploads using Cloudinary

### 🏬 Inventory Management
- Separate inventory microservice
- Inventory initialization during product creation
- Inventory-related service communication
- Inventory retry endpoint for product inventory initialization

### 🛒 Cart Management
- Dedicated cart microservice
- Separate responsibility for cart operations
- Integration with the E-Commerce shopping workflow

### 📋 Order Management
- Dedicated order microservice
- Separate order-processing responsibility
- Integration with cart, inventory, and payment workflows as configured in the implementation

### 💳 Payment Management
- Dedicated payment microservice
- Payment-related API operations
- Simulated payment flow in the demo

### ⚡ Performance Optimization
- Redis caching for selected product retrieval operations
- Pagination for product listing APIs
- Search and category filtering
- Cache eviction when products are modified
- API response-time measurements

### 📊 Monitoring
- Spring Boot Actuator
- Application health endpoints
- Application metrics
- Prometheus metrics endpoint
- Grafana dashboards when connected to the configured monitoring setup

### 🚀 Deployment
- React frontend deployed on Netlify
- Backend microservices deployed on Render
- Docker Compose configuration for local multi-service execution

---

## 🏗️ System Architecture

The application uses a microservices architecture.

```text
                         ┌──────────────────────┐
                         │     React Frontend   │
                         │      Netlify         │
                         └──────────┬───────────┘
                                    │
                           HTTP / REST APIs
                                    │
           ┌────────────────────────┼────────────────────────┐
           │                        │                        │
           ▼                        ▼                        ▼
  ┌─────────────────┐     ┌─────────────────┐      ┌─────────────────┐
  │  User Service   │     │ Product Service │      │  Cart Service   │
  │   Port 8802     │     │   Port 8801     │      │   Port 8805     │
  └─────────────────┘     └────────┬────────┘      └─────────────────┘
                                    │
                    ┌───────────────┼────────────────┐
                    │               │                │
                    ▼               ▼                ▼
             ┌────────────┐  ┌────────────┐   ┌──────────────┐
             │ PostgreSQL │  │ Cloudinary │   │    Redis     │
             │            │  │ Image Store│   │    Cache     │
             └────────────┘  └────────────┘   └──────────────┘

           ┌─────────────────┐     ┌─────────────────┐
           │ Inventory       │     │ Order Service   │
           │ Service         │     │ Port 8806       │
           │ Port 8803       │     └────────┬────────┘
           └─────────────────┘              │
                                            ▼
                                  ┌─────────────────┐
                                  │ Payment Service │
                                  │ Port 8807       │
                                  └─────────────────┘
```

> The diagram is a conceptual overview of the services. Refer to the Docker Compose configuration and individual service implementations for the exact runtime communication paths and database assignments.

### Architecture Principles

1. **Separation of responsibilities:** Each microservice handles a specific business area.
2. **Independent deployment:** Services can be deployed separately.
3. **REST-based communication:** Services expose HTTP endpoints for supported operations.
4. **Data persistence:** PostgreSQL is used for relational data.
5. **Caching:** Redis reduces repeated database work for selected read operations.
6. **External image storage:** Cloudinary stores product images.
7. **Observability:** Actuator exposes health and metrics information for monitoring.

---

## 🧰 Technology Stack

| Category | Technologies |
|---|---|
| Backend Language | Java |
| Backend Framework | Spring Boot |
| Security | Spring Security, JWT |
| API Style | REST APIs |
| Database | PostgreSQL |
| Database Hosting | Neon PostgreSQL |
| Caching | Redis |
| Image Storage | Cloudinary |
| Frontend | React.js |
| Frontend Build Tool | Vite |
| Containerization | Docker, Docker Compose |
| Monitoring | Spring Boot Actuator, Micrometer, Prometheus, Grafana |
| Backend Hosting | Render |
| Frontend Hosting | Netlify |
| Version Control | Git, GitHub |

---

## 🧩 Microservices

### 1. User Service

**Default port:** `8802`

Responsible for user registration, login, authentication, and user-specific functionality.

Main responsibilities:

- Register new users
- Authenticate users
- Encode passwords
- Generate JWT tokens
- Validate authenticated requests
- Retrieve the authenticated user's profile

### 2. Product Service

**Default port:** `8801`

Responsible for product-related business operations.

Main responsibilities:

- Create and manage products
- Retrieve products
- Search products
- Filter products by category
- Return paginated product results
- Upload product images to Cloudinary
- Communicate with the Inventory Service
- Cache selected product retrieval results using Redis
- Expose application health and metrics endpoints

### 3. Inventory Service

**Default port:** `8803`

Responsible for inventory-related functionality.

The Product Service communicates with the Inventory Service when initializing inventory for a newly created product.

The Product Service also exposes an inventory retry operation for retrying inventory initialization.

### 4. Cart Service

**Default port:** `8805`

Responsible for cart-related functionality in the shopping workflow.

Its exact endpoint paths, request schemas, and persistence rules should be documented from the current Cart Service controller and configuration.

### 5. Order Service

**Default port:** `8806`

Responsible for order-related functionality.

It represents the order-processing part of the shopping workflow. Exact order creation, retrieval, status transitions, and service communication depend on the current implementation.

### 6. Payment Service

**Default port:** `8807`

Responsible for payment-related functionality.

The frontend demo uses simulated payments. Do not treat this as a production payment gateway integration.

Exact payment endpoints and request formats should be documented from the current Payment Service controller.

---

## 🔌 Service Ports

| Service | Default Port | Main Responsibility |
|---|---:|---|
| Product Service | 8801 | Product management |
| User Service | 8802 | Registration and authentication |
| Inventory Service | 8803 | Inventory operations |
| Cart Service | 8805 | Shopping cart |
| Order Service | 8806 | Order management |
| Payment Service | 8807 | Payment operations |
| Redis | 6379 | Cache |

Ports listed above are the local/default ports. Render uses the deployed service URLs and platform-provided port configuration where applicable.

---

## 📁 Project Structure

The repository contains multiple backend microservices and supporting configuration.

```text
ecommerce_backend/
│
├── user-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── product-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── inventory-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── cart-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── order-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── payment-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── docker-compose.yml
└── README.md
```

The tree is illustrative. Add any actual shared configuration, test, or deployment directories present in your repository.

---

## 🔄 Application Workflow

### User Authentication

1. A user registers through the frontend.
2. The User Service validates the registration request.
3. The password is stored using the configured password encoder.
4. The user logs in with their credentials.
5. The User Service returns a JWT on successful authentication.
6. The frontend uses the token for protected requests.

### Product Creation

1. An authorized client sends product details and an image to the Product Service.
2. The Product Service validates the product request.
3. The image is uploaded to Cloudinary.
4. The product data and image URL are persisted.
5. The Product Service communicates with the Inventory Service to initialize inventory.
6. The API returns the created product response.

Inventory initialization and error recovery depend on the current service implementation.

### Product Retrieval

1. The client requests products.
2. The Product Service processes any search, category, or pagination parameters.
3. Selected retrieval operations use Redis caching.
4. If a cache entry is unavailable, the service retrieves data through its persistence layer.
5. The result is returned to the client.

### Shopping, Orders, and Payments

The frontend supports a shopping workflow involving cart, orders, and simulated payments. The exact sequence, inventory updates, payment status transitions, and failure handling should be verified against the corresponding service implementations.

---

## ⚙️ Prerequisites

Install the following before running the project locally:

- Java 21 for the Product Service; check the Java version required by each other service.
- Maven, or use the Maven Wrapper if provided.
- Node.js and npm for the React frontend.
- Docker Desktop with Docker Compose.
- PostgreSQL database access, such as a Neon PostgreSQL database.
- Redis, locally or through Docker.
- A Cloudinary account for product image uploads.

You will also need the repository's environment variables configured before starting services that depend on external resources.

---

## 🔐 Environment Variables

Configure secrets through environment variables or a local `.env` file supported by your deployment setup.

Example template:

```env
# Database
DB_URL=jdbc:postgresql://YOUR_DATABASE_HOST/YOUR_DATABASE_NAME
DB_USERNAME=YOUR_DATABASE_USERNAME
DB_PASSWORD=YOUR_DATABASE_PASSWORD

# JWT
JWT_SECRET=REPLACE_WITH_A_SECURE_SECRET

# Cloudinary
CLOUDINARY_CLOUD_NAME=YOUR_CLOUD_NAME
CLOUDINARY_API_KEY=YOUR_API_KEY
CLOUDINARY_API_SECRET=YOUR_API_SECRET

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Service communication
INVENTORY_SERVICE_URL=http://localhost:8803
```

These names are a template based on the known configuration. Check each service's `application.properties`, `application.yml`, and Docker Compose environment section for the exact variables it consumes.

**Important security practices:**

- Never commit actual passwords, JWT secrets, or API credentials.
- Do not put private credentials in frontend environment variables.
- Configure production secrets in the hosting provider's environment settings.
- Rotate any secret that has accidentally been committed or exposed.
- Use a strong, randomly generated JWT secret.
- Ensure production databases and external services are not publicly accessible without appropriate authentication and network controls.

---

## 🚀 Local Setup

### 1. Clone the Repository

```bash
git clone https://github.com/sujitha769/ecommerce_backend.git
cd ecommerce_backend
```

### 2. Configure the Environment

Configure the required database, JWT, Cloudinary, Redis, and service URL environment variables.

Make sure the database exists and the configured database user has the necessary permissions.

### 3. Start Redis

If Redis is installed locally, start it using your operating system's service manager.

Alternatively, if Docker is available:

```bash
docker run -d \
  --name ecommerce-redis \
  -p 6379:6379 \
  redis:7-alpine
```

Check Redis:

```bash
docker exec -it ecommerce-redis redis-cli ping
```

Expected response:

```text
PONG
```

If Redis is already started by Docker Compose, do not start a second container on the same host port.

### 4. Run a Spring Boot Service

Open a terminal in the relevant service directory.

For example:

```bash
cd product-service
```

Run with Maven:

```bash
mvn spring-boot:run
```

If the project includes a Maven Wrapper, you can use:

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Repeat for the other services, ensuring the required environment variables and dependent services are available.

### 5. Start the Frontend

Open a terminal in the frontend repository:

```bash
npm install
npm run dev
```

Vite will display the local development URL in the terminal.

Configure the frontend's API base URLs to point to the local services or the deployed Render endpoints, depending on the environment.

---

## 🐳 Docker Setup

The backend repository includes a Docker Compose configuration for running the services together.

### Start the Services

From the directory containing `docker-compose.yml`:

```bash
docker compose up -d --build
```

### Check Running Containers

```bash
docker compose ps
```

### View Logs

View all service logs:

```bash
docker compose logs -f
```

View a particular service's logs:

```bash
docker compose logs -f product-service
```

Replace `product-service` with the appropriate Compose service name.

### Restart a Service

```bash
docker compose restart product-service
```

### Rebuild a Service

```bash
docker compose up -d --build product-service
```

### Stop the Services

```bash
docker compose down
```

To remove persisted volumes as well, Docker supports:

```bash
docker compose down -v
```

**Warning:** Removing volumes can permanently delete data stored in those volumes. Review the Compose configuration before using this command.

### Docker Networking

Inside Docker Compose, containers should communicate using their Compose service names and internal ports rather than `localhost`.

For example, the Product Service can use:

```text
http://inventory-service:8803
```

when `inventory-service` is the actual Compose service name.

For Redis, use the Compose hostname:

```text
redis
```

The correct hostname and port must match the project's `docker-compose.yml`.

---

## 📡 API Documentation

The following endpoint paths are documented from the known Product Service and User Service configuration. For Cart, Inventory, Order, and Payment endpoints, consult the corresponding controller classes before adding exact routes to this section.

### User Service

Base URL:

```text
http://localhost:8802
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Authenticate a user |
| GET | `/api/users/me` | Retrieve the authenticated user's profile |

#### Register a User

```http
POST /api/auth/register
Content-Type: application/json
```

Example request structure (adjust field names to the actual DTO):

```json
{
  "username": "example",
  "email": "user@example.com",
  "password": "YOUR_PASSWORD"
}
```

#### Login

```http
POST /api/auth/login
Content-Type: application/json
```

Example request structure (adjust field names to the actual DTO):

```json
{
  "username": "example",
  "password": "YOUR_PASSWORD"
}
```

A successful login should return the authentication response defined by the User Service, including the JWT if that is the configured response format.

#### Get Current User

```http
GET /api/users/me
Authorization: Bearer YOUR_JWT_TOKEN
```

---

### Product Service

Base URL:

```text
http://localhost:8801
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Create a product with an image |
| GET | `/api/products` | Retrieve products |
| GET | `/api/products/page` | Retrieve a paginated product list |
| GET | `/api/products/{id}` | Retrieve a product by ID |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |
| POST | `/api/products/{id}/inventory/retry?initialQuantity=25` | Retry inventory initialization |

#### Create a Product

The create endpoint accepts `multipart/form-data` with a product JSON part and an image file.

```http
POST /api/products
Content-Type: multipart/form-data
```

Form-data fields:

| Field | Type | Description |
|---|---|---|
| `product` | JSON | Product details |
| `file` | File | Product image |

Example product JSON (use the actual `ProductRequest` field names):

```json
{
  "name": "Wireless Headphones",
  "description": "Wireless audio headphones",
  "price": 2499.00,
  "category": "Electronics",
  "stockQuantity": 25
}
```

Example cURL structure:

```bash
curl -X POST "http://localhost:8801/api/products" \
  -F 'product={"name":"Wireless Headphones","description":"Wireless audio headphones","price":2499.00,"category":"Electronics","stockQuantity":25};type=application/json' \
  -F "file=@headphones.jpg"
```

The exact JSON fields and validation constraints must match the Product Service's `ProductRequest` DTO.

#### Retrieve All Products

```http
GET /api/products
```

Optional query parameters:

- `category` — filter products by category.
- `search` — search for products using the supported search logic.

Example:

```http
GET /api/products?category=Electronics
```

Search example:

```http
GET /api/products?search=headphones
```

When both search and category are supplied, the current implementation gives search precedence.

#### Retrieve Paginated Products

```http
GET /api/products/page?page=0&size=10
```

Supported parameters:

| Parameter | Description |
|---|---|
| `page` | Zero-based page index |
| `size` | Number of products per page; supported range is 1–100 |
| `category` | Optional category filter |
| `search` | Optional search term |

Example:

```http
GET /api/products/page?page=0&size=10&category=Electronics
```

Example with search:

```http
GET /api/products/page?page=0&size=10&search=headphones
```

The paginated results are sorted by product ID in descending order in the known implementation.

#### Retrieve a Product by ID

```http
GET /api/products/1
```

#### Update a Product

```http
PUT /api/products/1
```

The required content type, request structure, and image-update behavior should be confirmed from the current controller.

#### Delete a Product

```http
DELETE /api/products/1
```

#### Retry Inventory Initialization

```http
POST /api/products/1/inventory/retry?initialQuantity=25
```

This operation requests an inventory initialization retry for the specified product.

---

### Inventory Service

Default base URL:

```text
http://localhost:8803
```

The Product Service uses this service to initialize inventory for products. Retrieve the Inventory Service controllers to document its exact public endpoints, request fields, response bodies, and error responses.

### Cart Service

Default base URL:

```text
http://localhost:8805
```

Document the cart endpoints from the Cart Service controllers, including the actual HTTP methods, paths, authentication requirements, and request/response DTOs.

### Order Service

Default base URL:

```text
http://localhost:8806
```

Document the order endpoints from the Order Service controllers. Include only workflows and order-status transitions that are implemented in the current code.

### Payment Service

Default base URL:

```text
http://localhost:8807
```

Document the payment endpoints from the Payment Service controllers. Clearly identify simulated payment behavior and do not describe it as a live payment-gateway integration.

---

## 🗄️ Database Design

PostgreSQL is used for relational data, with Neon PostgreSQL used for hosted database connectivity in the configured deployment.

The services are organized around separate business responsibilities. The actual database schema and ownership of individual tables should be verified in each service's JPA entities and datasource configuration.

Typical data responsibilities include:

- **User Service:** user account and authentication-related data.
- **Product Service:** product details and image references.
- **Inventory Service:** stock and inventory-related records.
- **Cart Service:** cart and cart-item records, if persisted by the implementation.
- **Order Service:** order and order-item records, if implemented.
- **Payment Service:** payment-related records, if persisted.

Do not assume that every service uses a separate database or schema without checking its configuration.

### Database Configuration

A typical PostgreSQL JDBC URL follows this structure:

```text
jdbc:postgresql://HOST:PORT/DATABASE_NAME
```

The actual URL, credentials, SSL settings, and datasource options should come from the environment configuration for each service.

---

## 🖼️ Product Image Uploads

Cloudinary is used for product image storage.

The Product Service accepts a multipart request containing product information and an image file.

Typical workflow:

1. The client submits product details and an image.
2. The backend receives the multipart request.
3. The image is uploaded to Cloudinary.
4. The resulting image URL is stored with the product data.
5. The API returns the created product information.

Keep Cloudinary credentials on the backend. Never expose the Cloudinary API secret in frontend source code.

---

## ⚡ Performance Optimization

Performance optimization is an important objective of this project.

### 1. Redis Caching

Redis is used to cache selected product retrieval results.

Benefits:

- Reduces repeated database queries for cacheable requests.
- Improves response times for repeated reads.
- Reduces some database load.
- Supports faster product-listing responses when a valid cache entry exists.

Cache invalidation is important. Product mutations should invalidate the relevant cached results so that clients do not continue receiving stale product data.

### 2. Pagination

The Product Service provides a paginated listing endpoint.

Example:

```http
GET /api/products/page?page=0&size=10
```

Benefits:

- Returns a limited number of products per request.
- Avoids transferring an unnecessarily large result set.
- Helps control memory and response payload size.
- Supports more scalable product listing as data grows.

### 3. Search and Category Filtering

Product retrieval supports search and category filtering. Filtering at the backend reduces the need to retrieve all products and filter them in the frontend.

### 4. API Performance Measurement

Measure response times before and after changes using a consistent test setup.

Record:

- Endpoint and HTTP method
- Number of requests
- Average response time
- Median response time
- p95 and p99 latency where available
- Error rate
- Cache hit/miss behavior
- Database load

Performance figures depend on the test environment and workload.

**Reported project results:** Product creation response time was reduced by 60%, and product retrieval APIs achieved response times under 200 ms in the measured test conditions. These figures should be supported by recorded benchmark results when presented in a portfolio or interview.

---

## 📊 Monitoring and Observability

The Product Service is configured with Spring Boot Actuator and Micrometer Prometheus metrics support.

### Health Endpoint

```http
GET /actuator/health
```

Returns application health information, subject to the configured Actuator exposure and security settings.

### Application Information

```http
GET /actuator/info
```

Returns application information if configured.

### Application Metrics

```http
GET /actuator/metrics
```

Lists available application metrics when the endpoint is exposed.

A particular metric can be requested by its name, for example:

```http
GET /actuator/metrics/jvm.memory.used
```

### Prometheus Metrics

```http
GET /actuator/prometheus
```

Provides Prometheus-formatted metrics when the endpoint is enabled and accessible.

### Grafana

Grafana can visualize collected metrics when connected to a configured Prometheus data source.

Useful monitoring panels include:

- Request rate
- HTTP error rate
- Request latency
- JVM memory usage
- CPU utilization, when collected
- Database connection-pool metrics, when exposed
- Cache metrics, when instrumented
- Service health

Prometheus and Grafana require the relevant collection and dashboard configuration. An endpoint existing in a service does not by itself prove that a complete monitoring pipeline is running.

---

## ☁️ Deployment

### Frontend — Netlify

The React frontend is deployed at:

https://ecommerce-live-app-suji.netlify.app/

When deploying the frontend:

1. Configure the correct API base URLs.
2. Set frontend build environment variables where required.
3. Verify that the backend permits requests from the frontend origin.
4. Test registration, login, product browsing, and the available shopping workflow.
5. Check browser developer tools for failed network requests.

Do not put database passwords, JWT signing secrets, or Cloudinary API secrets in frontend environment variables.

### Backend — Render

The backend services are deployed separately.

Known deployed service URLs:

| Service | URL |
|---|---|
| Product Service | https://suzi-ecom-product.onrender.com |
| User Service | https://suzi-ecom-user.onrender.com |
| Order Service | https://suzi-ecom-order.onrender.com |
| Inventory Service | https://suzi-ecom-inventory.onrender.com |
| Cart Service | https://suzi-ecom-cart.onrender.com |
| Payment Service | https://suzi-ecom-payment.onrender.com |

These URLs identify the configured deployments and do not guarantee that every service is currently healthy or available.

### Deployment Checklist

- Configure the production database URL and credentials.
- Configure the JWT secret securely.
- Configure Cloudinary credentials for the Product Service.
- Configure service-to-service URLs using deployed addresses.
- Configure Redis connectivity where required.
- Configure allowed frontend origins for CORS.
- Ensure the application listens on the port required by the hosting platform.
- Check build and runtime logs.
- Verify health endpoints where exposed.
- Test the complete workflow after deployment.
- Avoid committing environment files containing secrets.

---

## 🧪 Testing

Use tools such as Postman or cURL to test the REST APIs.

### Recommended Test Scenarios

#### User Service
- Register a valid user.
- Verify validation for invalid registration data.
- Log in with valid credentials.
- Verify incorrect credentials are rejected.
- Access a protected endpoint with a valid JWT.
- Verify protected access without a valid token is handled correctly.

#### Product Service
- Create a product with valid details and an image.
- Verify invalid product data is rejected.
- Retrieve all products.
- Retrieve a product by ID.
- Search products.
- Filter products by category.
- Retrieve paginated results.
- Update an existing product.
- Delete a product.
- Verify inventory initialization and retry behavior.
- Verify cache behavior after product changes.

#### Inventory, Cart, Order, and Payment Services
- Test each implemented endpoint using its actual request DTO.
- Test invalid IDs and malformed requests.
- Test service-unavailable and dependency-failure cases where applicable.
- Verify authentication and authorization requirements.
- Verify that simulated payments are clearly distinguished from real transactions.

### Performance Testing

For meaningful performance comparisons:

1. Use the same environment for before-and-after measurements.
2. Use representative request payloads.
3. Test both cold-cache and warm-cache requests.
4. Run enough requests to reduce the impact of one-off network delays.
5. Record latency, throughput, and errors.
6. Keep benchmark results so that optimization claims can be reproduced.

---

## 🔒 Security

The project uses Spring Security and JWT-based authentication in the User Service.

Important security considerations:

- Store passwords using a suitable password encoder.
- Keep JWT signing secrets private.
- Validate incoming request data.
- Protect endpoints according to their intended access rules.
- Configure CORS for approved frontend origins.
- Do not trust client-supplied user IDs or prices without server-side validation.
- Validate product images and uploaded file sizes.
- Protect database credentials and Cloudinary secrets.
- Use HTTPS in deployed environments.
- Avoid returning internal stack traces or secret values in API responses.
- Keep dependencies updated.
- Use a real payment provider and its verified server-side integration before processing real payments.

---

## 🛠️ Troubleshooting

### Service Cannot Connect to PostgreSQL

Check:

- Database URL and credentials.
- Network access and database availability.
- SSL requirements for the hosted database.
- Environment variables loaded by the application.
- Database connection logs.

### Redis Connection Failure

Check:

- Redis is running.
- The configured host and port are correct.
- Docker services use the Redis Compose hostname instead of `localhost`.
- Redis is reachable from the application container.

### Product Creation Fails

Check:

- Multipart field names match the controller.
- The `product` field contains valid JSON.
- The `file` field contains a valid image.
- Required product fields are provided.
- Cloudinary credentials are correct.
- The Product Service can reach its database.
- Inventory Service connectivity and logs.

### JWT Authentication Fails

Check:

- The login request succeeds.
- The client sends the token using the expected authorization format.
- The token has not expired.
- The signing secret is consistent with the configured validation logic.
- Protected endpoints are configured correctly.

### Frontend Cannot Reach the Backend

Check:

- The frontend uses the correct API base URL.
- The backend service is running.
- CORS permits the frontend origin.
- The browser's Network tab shows the actual failing request.
- The deployed backend URL and endpoint path are correct.

### Docker Service Fails to Start

Inspect:

```bash
docker compose ps
docker compose logs -f
```

Verify environment variables, port conflicts, database connectivity, and internal service URLs.

### Actuator or Prometheus Endpoint Is Unavailable

Check the Actuator dependency, endpoint exposure configuration, security rules, and whether the hosting platform is routing requests to the correct service.

---

## 🔮 Future Improvements

Possible improvements for future iterations include:

- Add automated integration tests for inter-service workflows.
- Add API documentation using OpenAPI/Swagger.
- Introduce centralized API routing if required by the architecture.
- Add distributed tracing and centralized log aggregation.
- Add stronger resilience patterns for service-to-service communication.
- Add automated CI/CD pipelines.
- Add structured audit logging.
- Improve load testing and performance benchmarking.
- Add role-based authorization for administrative operations.
- Add a production payment gateway with verified webhook handling.
- Add inventory reservation and order consistency mechanisms.
- Improve deployment automation and infrastructure configuration.

These are proposed improvements, not claims that the features are already implemented.

---

## 👩‍💻 Author

**Sujitha**

- GitHub: https://github.com/sujitha769
- Live Demo: https://ecommerce-live-app-suji.netlify.app/

---

## 📄 License

Add a `LICENSE` file to the repository if you intend to distribute the project under an open-source license. Until a license is included, do not assume that the repository grants others permission to reuse or redistribute the code.
