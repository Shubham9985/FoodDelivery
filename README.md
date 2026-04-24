# 🍔 Food Delivery Application

## 📌 Project Overview

This is a scalable food delivery system (like Swiggy/Zomato) built using a microservice-ready architecture.
The system supports customers, restaurants, delivery partners, and orders.

---

## 🚀 Features

* User Registration & Login
* Restaurant & Menu Management
* Cart & Order Placement
* Coupon & Discounts
* Delivery Assignment
* Ratings

---

## 🛠️ Tech Stack

### Backend

* Java, Spring Boot
* Spring Data JPA
* Hibernate

### Frontend

* Angular

### Database

* PostgreSQL

### Tools

* Git & GitHub
* Postman
* Swagger

---

## 🏗️ Architecture

```
Controller → Service → Repository → Database
```

---

## 🧩 Core Modules

* Customer Management
* Restaurant Management
* Menu Management
* Cart System
* Order Managemen
* Delivery System

---

## 📊 Database Design

Entities used:

* Customer
* Restaurant
* MenuItem
* Order
* OrderItem
* DeliveryDriver
* Cart & CartItem
* Payment
* Coupon
* Rating

---

## 🔗 API Endpoints (Sample)

### Customer APIs

* POST /customers/register
* POST /customers/login
* GET /customers/{id}

### Order APIs

* POST /orders
* GET /orders/{id}
* PUT /orders/{id}/status

### Cart APIs

* POST /cart/add
* GET /cart
* DELETE /cart/clear

---

## ⚙️ Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/Shubham9985/FoodDelivery.git
```

### 2. Backend Setup

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 3. Database Setup

* Create database `Food_Delivery`
* Update `application.properties`

---

## 👥 Team Collaboration Guidelines

* Use feature branches:

  * feature/cart
  * feature/order
* Follow naming conventions
* Create Pull Requests for merging
* Do not push directly to main branch

---

## 📌 Future Enhancements

* Real-time tracking using WebSockets
* AI-based recommendations
* Microservices architecture
* Docker & Kubernetes deployment

---

## 🧑‍💻 Contributors

* Team of 5 Developers

---

## 📜 License

This project is for educational purposes.