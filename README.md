Product Service
Product Service is a Spring Boot microservice in the E-Commerce Microservices Platform. It manages product information, uploads product images to Cloudinary, provides search, category filtering and pagination, and initializes inventory through the Inventory Service.

Tech Stack
- Java 21
- Spring Boot 4.1.1
- Spring Web MVC / REST APIs
- Spring Data JPA and Hibernate
- PostgreSQL (including Neon PostgreSQL)
- Cloudinary for product image uploads
- Redis caching for paginated product queries
- Spring Boot Actuator and Micrometer Prometheus registry
- Maven
- 
Responsibilities
- Create, retrieve, search, filter, update and delete products.
- Upload product images to Cloudinary.
- Validate product input and return response DTOs.
- Initialize inventory through the Inventory Service when a product is created.
- Retry inventory initialization for an existing product.
- Cache paginated product queries in Redis and evict those caches after product mutations.
- Expose health and Prometheus metrics through Actuator.
Run Locally
Prerequisites
- JDK 21
- Maven
- PostgreSQL database / Neon PostgreSQL database
- Redis running on port 6379
- Cloudinary account credentials
- Inventory Service reachable at http://localhost:8803 for product creation
Configure environment variables
The application reads database settings from environment variables. Set Cloudinary credentials for image uploads. Use your own values and never commit credentials to Git.

Windows Command Prompt:
set DB_URL=jdbc:postgresql://<host>/<database>?sslmode=require
set DB_USERNAME=<database-username>
set DB_PASSWORD=<database-password>
set CLOUDINARY_CLOUD_NAME=<cloud-name>
set CLOUDINARY_API_KEY=<api-key>
set CLOUDINARY_API_SECRET=<api-secret>
set INVENTORY_SERVICE_URL=http://localhost:8803
Replace placeholders with your provider's values. The inventory URL defaults to http://localhost:8803.
Start the service
From the repository root:
cd product-service
mvn spring-boot:run
The service listens on port 8801.

To start the full Docker Compose stack from the repository root, after configuring the required environment variables:
docker compose up -d --build
docker compose ps
docker compose logs -f product-service
Inside Compose, Product Service uses the redis and inventory-service service hostnames.
API Reference
Base URL: http://localhost:8801
Create a product
POST /api/products

Content type: multipart/form-data

Form fields:
- product: JSON part containing product details
- file: product image file
Example JSON for the product part:
{
  "name": "Everyday Sneakers",
  "category": "Footwear",
  "price": 2499.00,
  "mrp": 3199.00,
  "description": "Lightweight everyday sneakers",
  "emoji": "👟",
  "bg": ["#f3f4f6", "#e5e7eb"],
  "sizes": ["7", "8", "9", "10"],
  "rating": 0.0,
  "reviews": 0,
  "stockQuantity": 25
}
In Postman, choose Body → form-data, add a text field named product with the JSON above and a file field named file.

A successful request returns 201 Created and the created product response. Creation uploads the image and calls Inventory Service; if inventory initialization fails, the service returns a service-unavailable response.
List products
GET /api/products

Optional query parameters:
- category: filter by category (case-insensitive)
- search: search product name and description
Examples:
GET /api/products
GET /api/products?category=Footwear
GET /api/products?search=sneakers
If both search and category are present, search takes precedence.
List products with pagination
GET /api/products/page

Query parameters:
- page: zero-based page number; default 0
- size: items per page; default 10, permitted range 1–100
- category: optional category filter
- search: optional name/description search
Examples:
GET /api/products/page
GET /api/products/page?page=0&size=10
GET /api/products/page?page=0&size=12&category=Footwear
GET /api/products/page?page=1&size=10&search=sneakers
Results are sorted by product ID descending and returned as a Spring Page, including pagination metadata. Paginated lists, category results and search results are cached in Redis.
Get product by ID
GET /api/products/{id}

Example: GET /api/products/1

Returns 200 OK when found; otherwise 404 Not Found.
Update product details
PUT /api/products/{id}

Content type: application/json

Example body:
{
  "name": "Everyday Sneakers",
  "category": "Footwear",
  "price": 2399.00,
  "mrp": 3199.00,
  "description": "Updated product description",
  "emoji": "👟",
  "bg": ["#f3f4f6", "#e5e7eb"],
  "sizes": ["7", "8", "9", "10"],
  "rating": 4.5,
  "reviews": 10,
  "stockQuantity": 25
}
Delete product
DELETE /api/products/{id}

Returns 204 No Content when deletion succeeds.
Retry inventory initialization
POST /api/products/{id}/inventory/retry?initialQuantity=25

Retries initialization for an existing product. initialQuantity must be zero or greater.
Product Fields
Field	Type	Validation / notes
name	string	Required, non-blank
category	string	Required, non-blank
price	decimal	Required, greater than zero
mrp	decimal	Required, greater than zero
description	string	Required, non-blank
stockQuantity	integer	Required for creation, zero or greater
emoji	string	Optional display value
bg	string array	Optional; if supplied, contains two values
sizes	string array	Optional
rating	number	Defaults to 0
reviews	integer	Defaults to 0, zero or greater


Product responses include the product ID, details, rating/review data, display fields and Cloudinary imageUrl.
Caching and Performance
- Redis is configured as the Spring cache provider.
- Paginated listing, category and search queries use dedicated caches.
- Product create, update and delete operations evict paginated query caches.
- Redis defaults to localhost:6379 outside Docker; Compose configures Redis under the hostname redis.
- Pagination limits the result set returned for product listings.

Performance varies by environment and workload. Use measured benchmark results when claiming response-time improvements.
Health and Monitoring
Configured Actuator endpoints include:
- Health: GET /actuator/health
- Info: GET /actuator/info
- Metrics: GET /actuator/metrics
- Prometheus: GET /actuator/prometheus
Example: http://localhost:8801/actuator/health
Project Structure
product-service/
├── src/main/java/com/ecommerce/product/
│   ├── client/       # Inventory Service client
│   ├── config/       # Application and integration configuration
│   ├── controller/   # REST endpoints
│   ├── dto/          # Request/response models and API error DTO
│   ├── entity/       # JPA entities
│   ├── exception/    # Error handling
│   ├── repository/   # Spring Data repositories
│   └── service/      # Product and image business logic
├── src/main/resources/
│   └── application.properties
└── pom.xml
Troubleshooting
- Database connection fails: verify DB_URL, DB_USERNAME and DB_PASSWORD, including SSL options required by your PostgreSQL provider.
- Image upload fails: verify Cloudinary environment variables and check that the image file is valid.
- Product creation returns 503: confirm Inventory Service is reachable at INVENTORY_SERVICE_URL and inspect the Product Service logs.
- Redis connection fails: check Redis on port 6379 locally, or use the redis hostname when running under Compose.
- Pagination returns 400: page must be zero or greater and size must be between 1 and 100.
Security
Keep database passwords and Cloudinary secrets in environment variables or a secret manager. Do not commit re
