# Vanilla Cakes 🍰

A small e-commerce web application for browsing and ordering cakes.

In order to better understand frameworks, the project was built using **plain Java**, without Spring or another dependency injection framework, and with **plain HTML, CSS and JavaScript**.

It implements a complete, albeit simple, shopping flow: browsing cakes, viewing their details, adding them to a cart, and placing an order.

## Features

* 🍰 Browse available cakes
* 🔎 View cake details
* 🛒 Add cakes to a shopping cart
* 🔢 Adjust quantities in the cart
* 🗑️ Remove items or clear the cart
* 📦 Place orders
* ✅ Display order confirmation
* 🖼️ Store and serve cake images from the database
* 📄 Paginated cake listing
* 📱 Responsive frontend

## Technologies

### Backend

* Java
* Embedded Apache Tomcat
* JDBC
* PostgreSQL
* Liquibase

The backend intentionally does **not** use Spring or another application framework. The application is assembled explicitly, with dependencies being created and wired by the application itself.

### Frontend

* HTML
* CSS
* JavaScript

The frontend also intentionally avoids frameworks, keeping the implementation simple and close to the browser APIs.

## Architecture

The backend is organized into a small layered architecture:

```text
HTTP Request
     │
     ▼
Controllers
     │
     ▼
Services
     │
     ▼
Repositories
     │
     ▼
PostgreSQL
```

The application itself acts as the composition root, explicitly creating and connecting the components used by the system.

Database access is performed through JDBC, with repositories receiving a database connection explicitly rather than relying on a dependency injection container.

## Project Structure

The project is organized around the main responsibilities of the application:

```text
src/
├── main/
│   ├── java/
│   │   └── com/vanillacakes/
│   │       ├── cakes/
│   │       ├── orders/
│   │       └── ...
│   └── resources/
│       ├── static/
│       └── db/
│           └── changelog/
└── ...
```

## Running the Project

### Requirements

* Java
* PostgreSQL

Clone the repository:

```bash
git clone https://github.com/golinguim/vanilla-cakes.git
cd vanilla-cakes
```

Start the database containers (for the application and tests):

```bash
docker compose up -d
```

Compile and run the application:
```bash
mvn compile exec:java
```

Once running, open:

```text
http://localhost:8080/cakes.html
```

## Database

Database schema changes are managed with **Liquibase**.

Cake images are stored as binary data in a dedicated `cake_images` table rather than being stored directly in the cake record.

## Limitations

This is a **study project**, not a production-ready e-commerce platform.

Some intentional limitations include:

* No user authentication or authorization
* No real payment processing or order fulfillment
* No persistent user accounts
* No graphical interface for use for the cake creation endpoint
* The application is intended to run locally

## Possible Improvements

Some things that could be explored in the future include:

* More polished frontend components
* Better error handling
* More sophisticated validation and order management

## Preview

A short video showing the application in action:

