# Order Management System

A Spring Boot-based Order Management System that handles customer orders, inventory management, and administrative functions. 

## Features

- Customer Management
- Order Processing
- Inventory Management
- Product Management
- Administrative Controls
- Notifications

## Technology Stack

- Java
- Spring Boot
- Elasticsearch
- Apache Kafka

## Project Structure

```
src/main/java/com/example/orderemanagement/
├── controller/         # REST API endpoints
├── service/           # Business logic layer
├── repository/        # Data access layer
├── model/            # Entity classes
├── dto/              # Data Transfer Objects
├── mapper/           # Object mapping utilities
├── config/           # Configuration classes
├── exception/        # Custom exception handling
├── elasticsearch/    # Elasticsearch integration
├── kafka/           # Kafka message processing
└── notification/     # Notification services
```

## Key Components

### Models
- Customer: Customer information management
- Order: Order processing and tracking
- Product: Product catalog management
- Inventory: Stock management
- Admin: Administrative user management

### Controllers
- CustomerController: Handles customer-related operations
- AdminController: Manages administrative functions

### Installation

1. Clone the repository:
```bash
git clone [repository-url]
```

2. Navigate to the project directory:
```bash
cd orderemanagement
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

## API Documentation

### Customer Endpoints

#### Authentication
- `POST /api/customers/register` - Register a new customer
- `POST /api/customers/login` - Customer login

#### Customer Operations
- `GET /api/customers/{id}` - Get customer details by ID

#### Product Operations
- `GET /api/customers/products` - Get all products
- `GET /api/customers/products/paginated` - Get paginated products
  - Query Parameters:
    - `cursor` (optional): Pagination cursor
    - `size` (default: 10): Number of items per page
    - `sortBy` (default: "name"): Field to sort by
    - `sortDir` (default: "asc"): Sort direction
- `POST /api/customers/products/buy` - Purchase a product

### Admin Endpoints

#### Authentication
- `POST /api/admins/register` - Register a new admin
- `POST /api/admins/login` - Admin login

#### Product Management
- `POST /api/admins/products/add` - Add a new product
- `GET /api/admins/products` - Get all products
- `GET /api/admins/product` - Get product by name
  - Query Parameter: `name`
- `DELETE /api/admins/product` - Delete a product
  - Query Parameters: `id` or `name`
- `PUT /api/admins/product` - Update a product
  - Query Parameters: `id` or `name`
- `PATCH /api/admins/product/inventory` - Add to product inventory
  - Query Parameters: `id` or `name`

### Request/Response Formats

#### Customer Registration
```json
POST /api/customers/register
{
    "name": "string",
    "email": "string",
    "password": "string"
}
```

#### Product Purchase
```json
POST /api/customers/products/buy
{
    "productId": "long",
    "quantity": "integer"
}
```

#### Add Product (Admin)
```json
POST /api/admins/products/add
{
    "name": "string",
    "description": "string",
    "price": "decimal",
    "quantity": "integer"
}
```

#### Update Product (Admin)
```json
PUT /api/admins/product
{
    "name": "string",
    "description": "string",
    "price": "decimal"
}
```

#### Add to Inventory (Admin)
```json
PATCH /api/admins/product/inventory
{
    "quantity": "integer"
}
```

