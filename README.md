# Distributed Rate Limiter & Audit Logger

A high-performance, distributed rate-limiting system built with **Spring Boot 3** and **Redis Sorted Sets (ZSET)** using the **Sliding Window Log** algorithm.

---

## 📌 Architecture & Design

### Sliding Window Log via Redis ZSET
Instead of fixed time buckets (which suffer from boundary spike issues), this implementation tracks request timestamps within a continuous 60-second sliding window.

* **Redis Key:** Client IP (or User ID)
* **Redis Score:** Request Timestamp (`Epoch Milliseconds`)
* **Redis Member:** Unique string formatted as `<timestamp>-<UUID>` to prevent member collisions when multiple requests occur within the exact same millisecond.

### Request Flow
1. **Interceptor (`RateLimitInterceptor`):** Intercepts incoming HTTP requests, extracts the real client IP (handling `X-Forwarded-For` proxy headers), and queries the rate limit service.
2. **Range Cleanup:** Removes entries from Redis ZSET older than `currentTime - 60s`.
3. **Cardinality Check (`ZCARD`):** Counts valid requests in the active window.
4. **Decision:**
    * If `count >= MAX_REQUESTS (5)` $\rightarrow$ Returns HTTP `429 Too Many Requests`.
    * Else $\rightarrow$ Records the timestamped UUID member, refreshes TTL (60s), and allows the request through.

---

## 🛠️ Tech Stack

* **Java:** 21 / 25
* **Framework:** Spring Boot 3.x (Spring MVC, Web)
* **In-Memory Store:** Redis (`StringRedisTemplate`)
* **Utilities:** Lombok

---

## 🚀 Getting Started

### Prerequisites
* JDK 21+
* Docker / Docker Compose

### 1. Run Redis Container
```bash
docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest
```
#### 2. Build & Run the Application
```bash
./mvnw clean spring-boot:run
```
🧪 Testing the API
Success Request
```bash
curl -i http://localhost:8080/test/api
```