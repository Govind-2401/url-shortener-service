# 🔗 High-Performance URL Shortener & Analytics Service

A scalable, full-stack URL shortener service built with **Spring Boot 3**, **MySQL**, **Caffeine Cache**, **Bucket4j**, and a modern **Tailwind CSS UI**. Designed for low-latency redirection, rate limiting, TTL link expiration, and live QR code generation.

---

## ✨ Features

* **⚡ Ultra-Low Latency Redirection:** Built-in **Base62 Encoding** paired with **Caffeine Cache** for lighting-fast URL redirection.
* **🔒 Rate Limiting & Anti-Spam:** Integrated **Bucket4j** token-bucket algorithm to prevent API abuse and DDoS requests.
* **⏳ Dynamic TTL & Link Expiration:** Custom link expiration support (e.g., set links to expire in 7, 30, or custom days).
* **📈 Real-Time Click Analytics:** Tracks link visit counts and active/expired status using `@CachePut` synchronization.
* **📱 Live QR Code Generation:** Instant QR code render for mobile-friendly link sharing and direct scanning.
* **🎨 Modern Glassmorphism UI:** Built with Tailwind CSS, embedded directly into Spring Boot static resources.
* **📖 Interactive API Docs:** Integrated **Swagger UI** for testing endpoints effortlessly.

---

## 🛠️ Tech Stack & Architecture

* **Backend:** Java 17+, Spring Boot 3, Spring Data JPA, Hibernate
* **Database:** MySQL
* **Caching:** Caffeine In-Memory Cache
* **Rate Limiting:** Bucket4j
* **Frontend:** HTML5, Tailwind CSS (CDN), FontAwesome, QRCode.js
* **API Documentation:** OpenAPI 3 / Swagger UI

---

## 🚀 Getting Started

### Prerequisites

* Java 17 or higher
* Maven 3.8+
* MySQL Database

### Database Setup

1. Create a MySQL database:
   ```sql
   CREATE DATABASE urlshortener;