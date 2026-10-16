# Distributed Rate Limiter & Audit Logger

A high-performance, distributed rate-limiting system built with **Spring Boot 3** and **Redis Sorted Sets (ZSET)** using the **Sliding Window Log** algorithm, coupled with **RabbitMQ** for asynchronous audit logging.

## 📌 Architecture & Design

### 1. Sliding Window Log via Redis ZSET
Instead of fixed time buckets (which suffer from boundary spike issues), this implementation tracks request timestamps within a continuous 60-second sliding window.
* **Redis Key:** Client IP (or User ID)
* **Redis Score:** Request Timestamp (`Epoch Milliseconds`)
* **Redis Member:** Unique string formatted as `<timestamp>-<UUID>` to prevent member collisions.

### 2. Asynchronous Event-Driven Audit Logging
* **Producer:** `RateLimitInterceptor` publishes request metadata (IP, endpoint, method, status, payload) asynchronously via `RabbitTemplate` to a RabbitMQ Direct Exchange.
* **Consumer:** `@RabbitListener` consumes logs off the queue in the background, decoupling disk/DB I/O from the critical HTTP request path to maintain low latency.

---

## 🛠️ Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.x (Spring MVC, AMQP)
* **Datastore / In-Memory:** Redis Stack (`StringRedisTemplate`), RabbitMQ
* **Utilities:** Lombok, Maven

---

## 🚀 Getting Started

### Prerequisites
* JDK 21+
* Docker / Docker Compose

### 1. Run Redis Container
```bash
# 1. Stop and remove existing containers
docker rm -f redis-stack rabbitmq
# 2. Run them again
# Run Redis Stack
docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest

# Run RabbitMQ with Management UI
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### 2. Build & Run Application
```bash
./mvnw clean spring-boot:run
```

---

## 🧪 Testing the API

### Successful Request
```bash
curl -i http://localhost:8080/test/api
```

### Triggering Rate Limit (6 Sequential Requests)
```bash
for i in {1..6}; do curl -i http://localhost:8080/test/api; echo ""; done
```

**Expected Response on 6th Request:**
```http
HTTP/1.1 429 
Content-Type: text/plain;charset=UTF-8
Transfer-Encoding: chunked
Date: Fri, 16 Oct 2026 05:49:17 GMT

Rate limit exceeded. Please try again later.
```