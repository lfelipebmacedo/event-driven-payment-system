# 🚀 Event-Driven Payment System

A backend system built to simulate real-world payment processing using an event-driven architecture.

This project focuses on production-oriented backend engineering concepts such as asynchronous processing, idempotency, concurrency control, and financial consistency.

---

## 🧠 Overview

The system allows clients to create payments via a REST API, while the processing happens asynchronously through a message broker.

It is inspired by real-world payment platforms (e.g. Pix, Stripe), where reliability, consistency, and safe retries are critical.

---

## 🏗 Architecture

The platform is composed of multiple services:

### 🔹 Payment API

* Receives HTTP requests
* Validates input
* Persists payment with `PENDING` status
* Publishes `payment.created` event

### 🔹 Payment Processor

* Consumes events from the broker
* Processes payments asynchronously
* Updates payment status (`SUCCEEDED` or `FAILED`)

### 🔹 Ledger Service

* Records financial entries (debit/credit)
* Ensures transactional consistency

### 🔹 Notification Service

* Sends notifications based on payment outcome

---

## 🔁 Payment Flow

1. Client sends `POST /payments`
2. Payment is stored with status `PENDING`
3. Event `payment.created` is published
4. Processor consumes event and processes payment
5. Status is updated to `SUCCEEDED` or `FAILED`
6. Ledger records transaction
7. Notification is triggered

---

## ⚙️ Tech Stack

* Java 21
* Spring Boot
* Spring Data JPA
* PostgreSQL
* RabbitMQ
* Docker & Docker Compose

---

## 🧠 Key Concepts

### Idempotency

The API supports idempotency keys to prevent duplicate payments during retries.

### Event-driven architecture

Decouples request handling from processing, improving scalability and resilience.

### Concurrency control

Optimistic locking is used to prevent race conditions during state transitions.

### Retry & Dead-letter queue

Failed messages are retried and eventually sent to a dead-letter queue if necessary.

---

## 📊 Payment Lifecycle

* `PENDING`
* `PROCESSING`
* `SUCCEEDED`
* `FAILED`

---

## 📦 Project Structure

```bash
event-driven-payment-system/
├── services/
│   ├── payment-api/
│   ├── payment-processor/
│   ├── ledger-service/
│   └── notification-service/
├── docs/
├── docker-compose.yml
└── README.md
```

---

## 🚀 Running locally

### Requirements

* Docker
* Docker Compose

### Start the system

```bash
docker compose up --build
```

The `payment-api` service also exposes a JVM debug port on `5005`, so you can attach a remote debugger while it runs in Docker.

To run the API locally with the same debug port:

```bash
make api-run-debug
```

---

## 📡 API Example

### Create payment

```http
POST /payments
Idempotency-Key: 7f7aa9c4-4c87-4db3-b9ec-ccf20aab6e54
Content-Type: application/json
```

```json
{
  "external_reference": "order-123",
  "amount": 1500,
  "currency": "BRL",
  "payer_id": "user-1",
  "receiver_id": "merchant-1"
}
```

---

## 🧠 Architectural Decisions

### Why event-driven architecture?

To decouple API from processing, allowing better scalability and fault tolerance.

### Why idempotency?

To ensure safe retries and prevent duplicated financial operations.

### Why optimistic locking?

To avoid inconsistent updates while keeping performance acceptable.

### Why a shared database (initially)?

To simplify development while maintaining logical separation between services.

---

## ⚖️ Trade-offs

* Increased complexity compared to a monolithic CRUD API
* Eventual consistency introduces debugging challenges
* Messaging infrastructure adds operational overhead

---

## 🚧 Future Improvements

* Distributed tracing (OpenTelemetry)
* Metrics and monitoring (Prometheus + Grafana)
* Authentication & authorization
* Separate databases per service
* Integration and contract testing

---

## 🎯 Purpose

This project is part of my portfolio to demonstrate backend engineering skills with a focus on:

* system design
* distributed systems fundamentals
* data consistency
* real-world backend challenges

---

## 📫 Contact

* LinkedIn: (add your link)
* Email: (optional)

---

💡 Built to learn, simulate real-world systems, and improve backend engineering skills.
