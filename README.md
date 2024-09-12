# Online Bookstore

---

### Introduction

---

The "Online bookstore" is a web application for purchasing books online.
In this app, users can browse available books, search for desired books by multiple criteria, save them to a shopping cart, and place an order.
This allows users to buy books without the need to leave their homes or browse through physical bookshelves, making the process comfortable and fast.

---

### Technologies and tools

---

- Java 21
- Spring Boot, Spring Security, Spring Data JPA
- MySQL, Liquibase
- JWT
- Maven, Docker
- Lombok, Mapstruct, Swagger
- Junit, Mockito, Testcontainers

---

### How to run:

---

1. Install and run Docker.
2. Configure access parameters in the `.env` file (refer to the required fields in the `.env.template` file).
3. Open a terminal and navigate to the root directory of the project on your machine.
4. Run the application using Docker Compose: `docker-compose up`
5. Voilà! :)

Feel free to test the available functionality using endpoints with tools like Postman or via Swagger UI, which you can find below ⬇️

---

### Application functionality

---

Currently, the application supports two roles: `ADMIN` and `USER`. The `USER` role is automatically assigned to each new user but has limited access to certain endpoints. To assign the `ADMIN` role, you must create a Liquibase script or use an SQL query.

**Base URL**: - `http://localhost:8080/api`

### Available endpoints

**Authentication** - Endpoints for managing authentication:
- `POST: /auth/registration` - Register a new user
- `POST: /auth/login` - Log in

**Book** - Endpoints for managing books:
- `GET: /books` - Get all available books
- `GET: /books/{id}` - Get a book by its ID
- `POST: /books` - Create a new book (ADMIN only)
- `PUT: /books/{id}` - Update a book (ADMIN only)
- `GET: /books/search` - Search for books by parameters such as authors, ISBN, max price, or part of the title
- `DELETE: /books/{id}` - Delete a book by its id (ADMIN only)

**Category** - Endpoints for managing categories:
- `GET: /categories` - Get all categories
- `GET: /categories/{id}` - Get a category by its ID
- `POST: /categories` - Create a new category (ADMIN only)
- `PUT: /categories/{id}` - Update a category (ADMIN only)
- `GET /categories/{id}/books` - Get all books by category ID
- `DELETE: /categories/{id}` - Delete a category by its ID (ADMIN only)

**Shopping cart** - Endpoints for shopping cart management:
- `GET: /cart` - Retrieve the user's shopping cart
- `POST: /cart` - Add a book to the shopping cart
- `PUT: /cart/items/{cartItemId}` - Update the quantity of a book in the shopping cart
- `DELETE: /cart/items/{cartItemId}` - Remove a book from the shopping cart

**Order** - Endpoints for managing user orders:
- `GET: /orders` - Retrieve the user's order history
- `POST: /orders` - Place an order
- `PATCH: /orders/{id}` - Update the order status (ADMIN only)
- `GET: /orders/{orderId}/items/{itemId}` - Retrieve a specific ordered item within an order
- `GET: /orders/{orderId}/items` - Retrieve all ordered items for a specific order

For examples of request bodies, refer to the Swagger documentation.

---

#### [SWAGGER UI](http://localhost:8080/api/swagger-ui/index.html#/)

---