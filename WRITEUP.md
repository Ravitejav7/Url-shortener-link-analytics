# One-page write-up

## 1. What I asked the AI to do, and what I wrote or decided myself

I used AI as a planning and implementation partner, but I drove the requirements and design choices. I first broke the assignment down into the behaviors that would be evaluated: shortening, redirect round-trips, unknown-code handling, persistence, custom aliases, collision-safe code generation, duplicate URL behavior, automated tests, README, deployment readiness, and the required write-up.

I specifically asked the AI to help me reason through edge cases before coding: invalid URLs, unsupported schemes like `javascript:` and `mailto:`, duplicate URLs, duplicate aliases, reserved aliases such as `analytics` and `shorten`, unknown short codes, code collisions, redirect analytics, and what should or should not be stored for privacy.

The main product and engineering decisions were chosen and reviewed by me: MongoDB Atlas for Render deployment, a clean SDE-style layered structure, `SecureRandom` Base62 short-code generation, idempotent behavior for duplicate generated URLs, explicit custom-alias behavior, `409 Conflict` for alias collisions, and analytics that store timestamp/user-agent/referrer but not IP addresses.

I used the AI to generate scaffolding and implementation support for controllers, service interfaces, service implementations, repositories, DTOs, validation helpers, exception handling, tests, README, Render/Docker deployment files, and the first draft of the write-up. I reviewed and adjusted the output so the final project matched the requirements and stayed explainable for a follow-up live session.

## 2. Where I overrode, corrected, or threw away the AI's output

I actively steered several important decisions instead of accepting the first AI-generated direction. The initial plan was local-only, but I highlighted that the project needed to deploy on Render, where local MongoDB would not work. Based on that, I changed the production database plan to MongoDB Atlas and kept local MongoDB only as a development fallback.

I also pushed for a more SDE-style structure because the assignment evaluates code organization and judgment, not just whether endpoints work. Instead of leaving everything in one or two classes, I separated controllers, service interfaces, service implementations, entities, repositories, DTOs, helpers, configuration, and exception handling. That makes the code easier to review, test, and extend in a follow-up live session.

I clarified product behavior that could otherwise be ambiguous: generated links are idempotent for the same URL, but custom aliases are treated as intentional named links. I also chose to reject alias conflicts instead of silently overwriting or returning an existing alias, because overwriting short links would be unsafe.

I corrected the deployment/tooling setup as well. A local Maven wrapper tied to a machine-specific `.tools` directory was not portable enough for reviewers or Render, so I replaced it with the standard Maven Wrapper and added Docker deployment support.

Finally, I pushed the implementation toward meaningful tests and safer analytics. The service stores timestamp, user-agent, and referrer, but not IP addresses, because detailed analytics are useful while unnecessary personal data would create avoidable privacy risk.

## 3. Biggest tradeoffs

MongoDB Atlas vs local MongoDB/Postgres: MongoDB fits the document-shaped data model and Atlas works cleanly with Render. Postgres would provide strong relational constraints, but it would require changing the data model and setup. Local MongoDB is convenient for development, but not sufficient for deployment.

Random Base62 vs sequential IDs: sequential IDs avoid collisions but are predictable and require sequence coordination. I chose 8-character Base62 codes generated with `SecureRandom`. The code space is large, MongoDB enforces uniqueness on `code`, and the service retries on rare collisions.

Duplicate URL behavior: for generated codes, repeated shortening of the same URL returns the existing code. That makes `POST /shorten` idempotent for the common case. For custom aliases, the service allows multiple aliases for the same URL because the user is explicitly asking for a named link.

Analytics detail vs privacy: I store timestamp, user-agent, and referrer, but not IP address. This provides useful link analytics while avoiding unnecessary sensitive data collection.

## 4. What's missing, or what I would do with another day

With more time, I would add rate limiting, authentication for analytics endpoints, link expiration, soft deletes, Docker Compose for local MongoDB, integration tests against a real MongoDB test container, OpenAPI documentation, and production observability such as structured logs and metrics.
