# Scala REST API Application with Play Framework

### Technology Stack
- Scala 2.13
- Play Framework 2.8.19
- Slick 5.0.0 (DB Access/Evolutions)
- PostgreSQL
- Guice (DI)
- Silhouette (Authn/Authz)
- HTTPClient (Play WS)
- JSON Conversion (Play JSON)
- Logging (Logback)

### Project Structure

### Getting Started

#### 1. Setup `PostgreSQL` Database
You can install PostgreSQL on your local machine or running the docker compose in the `/docker/database` folder
to get PostgreSQL ready.

#### 2. Run application 
You need to download and install sbt for this application to run.
_Note: I've added the `SBT bin` to this project to build the source code without SBT installation_
Once you have sbt installed, the following at the command prompt will start up Play in development mode:
```bash
./sbt run
```

Play will start up on the HTTP port at <http://localhost:8080/>.   You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.

#### 3. Run Unit Tests
```bash
./sbt clean test
```

or To generate code coverage report with SCoverage
```bash
./sbt clean coverage test coverageReport
```

#### 4. Run Integration Tests
```bash
./sbt clean integration/test
```

### Usage
_Ref: Postman collection at `postman` folder_
Authentication
- POST /login – Login to application with JWT Authentication support

Users
1. GET /users - retrieves a list of all users.
2. POST /users - creates a new user.
3. GET /users/:id - retrieves a user by ID.
4. PUT /users/:id - updates an existing user by ID.
5. DELETE /users/:id - deletes a

Products
1. GET /products - retrieves a list of all products.
2. POST /products - creates a new product.
3. GET /products/:id - retrieves a product by ID.
4. PUT /products/:id - updates an existing product by ID.
5. DELETE /products/:id - deletes a product by ID. 

Orders
1. GET /orders - retrieves a list of all orders.
2. POST /orders - creates a new order.
3. GET /orders/:id - retrieves an order by ID.
4.PUT /orders/:id - updates an existing order by ID.
5. DELETE /orders/:id - deletes an order by ID. 

### Notes
- All API endpoints must require authentication using Silhouette, except ‘/login’.
- Only authenticated users with the Admin role should be allowed to perform CRUD operations on Users.
- Only authenticated users with the Operator or Admin role should be allowed to retrieve product information from the External Products. (And then register these external products to the local Products table via exposed APIs)
- Only authenticated users with the Operator or Admin role should be allowed to perform CUD operation on Products.
- All authenticated Users should be allowed to retrieve the Products information so that they can create the Orders.
- All authenticated Users should be allowed to perform CRUD operations on their own Orders.
- Admin should be allowed to perform CRUD operations on all Orders. 