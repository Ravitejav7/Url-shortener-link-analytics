# URL Shortener & Link Analytics

A Spring Boot API that shortens long URLs, supports custom aliases, redirects short codes with `301`, persists data in MongoDB, and records click analytics.

## Tech stack

- Java 21
- Spring Boot 4.1.0
- Maven 3.9.16
- MongoDB / MongoDB Atlas
- Docker-ready for Render deployment

## Requirements implemented

- `POST /shorten` accepts a URL and optional custom alias.
- `GET /{code}` redirects to the original URL with `301 Moved Permanently`.
- Unknown codes return `404`.
- URL mappings persist in MongoDB.
- Custom aliases are supported and conflicts return `409`.
- Generated short codes use `SecureRandom` Base62, 8 characters.
- Same URL shortened twice without alias returns the same generated code.
- Redirects record analytics: timestamp, user-agent, and referrer.
- `GET /analytics/{code}` returns click count and recent click events.
- `GET /` returns a small API status payload for deployment smoke checks.
- Malformed JSON and invalid requests return structured `400` responses.

## Configuration

The app reads configuration from environment variables:

| Variable | Local default | Purpose |
| --- | --- | --- |
| `MONGODB_URI` | `mongodb://localhost:27017/url_shortener` | MongoDB connection string mapped to Spring Boot 4's `spring.mongodb.uri` |
| `APP_BASE_URL` | `http://localhost:8080` | Base URL used to build short URLs |
| `PORT` | `8080` | HTTP port |

For production on Render, use a MongoDB Atlas connection string for `MONGODB_URI`.
The deployed service currently uses:

```text
https://url-shortener-link-analytics.onrender.com
```

## Run locally

Start MongoDB locally first, or set `MONGODB_URI` to a MongoDB Atlas URI.

PowerShell:

```powershell
$env:MONGODB_URI="mongodb://localhost:27017/url_shortener"
$env:APP_BASE_URL="http://localhost:8080"
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
export MONGODB_URI="mongodb://localhost:27017/url_shortener"
export APP_BASE_URL="http://localhost:8080"
./mvnw spring-boot:run
```

## Run tests

```powershell
.\mvnw.cmd test
```

The automated tests use mocks and do not require a running MongoDB instance.

## API examples

PowerShell JSON-safe request:

```powershell
$body = @{ url = "https://example.com/some/long/path" } | ConvertTo-Json -Compress
Invoke-WebRequest `
  -Uri "http://localhost:8080/shorten" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

Create a generated short code:

```bash
curl -i -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d "{\"url\":\"https://example.com/some/long/path\"}"
```

Create a custom alias:

```bash
curl -i -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d "{\"url\":\"https://example.com\",\"alias\":\"my-demo\"}"
```

Redirect:

```bash
curl -i http://localhost:8080/my-demo
```

Analytics:

```bash
curl -i http://localhost:8080/analytics/my-demo
```

Invalid URL example:

```bash
curl -i -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d "{\"url\":\"javascript:alert(1)\"}"
```

## Design decisions

- Generated codes are idempotent by URL: shortening the same URL twice without an alias returns the existing generated code.
- Custom aliases are explicit named links: the same URL may have multiple custom aliases.
- Alias conflicts return `409 Conflict`; aliases are never overwritten.
- URL validation accepts only absolute `http` and `https` URLs.
- Analytics intentionally excludes IP addresses to avoid unnecessary privacy risk.

## Render deployment

1. Push this repository to GitHub.
2. Create a MongoDB Atlas cluster and copy the connection URI.
3. In Render, create a new Web Service from the repository.
4. Use Docker runtime, or use the included `render.yaml` Blueprint.
5. In MongoDB Atlas Network Access, allow Render to connect. For this demo deployment, add `0.0.0.0/0`.
6. Set environment variables:

```text
MONGODB_URI=mongodb+srv://<user>:<password>@<cluster-host>/url_shortener?retryWrites=true&w=majority&tls=true
APP_BASE_URL=https://url-shortener-link-analytics.onrender.com
```

The Docker image builds the app with Maven and starts the app with Spring Boot 4's MongoDB property:

```bash
java -Dspring.mongodb.uri="$MONGODB_URI" -jar app.jar
```

## Project structure

```text
controller/      HTTP endpoints
service/         service interfaces
service/impl/    service implementations
entity/          MongoDB documents
repository/      Spring Data Mongo repositories
dto/             request/response contracts
helper/          validation, code generation, URL utilities
exception/       custom exceptions and global JSON error handling
config/          app properties and Mongo indexes
```
