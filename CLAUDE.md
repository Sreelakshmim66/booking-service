# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

**Build:**
```bash
./mvnw clean package
```

**Run:**
```bash
./mvnw spring-boot:run
```

**Test:**
```bash
./mvnw test
```

**Run a single test:**
```bash
./mvnw test -Dtest=BooklingServiceApplicationTests
```

**Compile (including protobuf generation):**
```bash
./mvnw compile
```
Protobuf stubs are auto-generated from `src/main/proto/` into `target/generated-sources/` during compile.

## Architecture

This is a **Spring Boot 3.3 microservice** (Java 17) that handles travel bookings. It runs on port `8083` (HTTP) and `9093` (gRPC server).

### Microservice ecosystem
The service participates in a larger microservice system:
- Registers with **Eureka** service discovery at `localhost:8761`
- Communicates outbound via **gRPC** (using `net.devh` grpc-spring-boot-starter 3.x) to:
  - `TRIP-SERVICE` — to validate trips (currently commented out in `BookingService`)
  - `NOTIFICATION-SERVICE` — to send booking confirmation notifications (fire-and-forget; failures are logged but don't fail the booking)

### Data
- **PostgreSQL** on port `5433`, database `Booking-service`
- Single `bookings` table via JPA/Hibernate (`ddl-auto=update`)
- `bookingId` is a 16-digit random numeric string generated in `@PrePersist`

### Key flows

**POST `/api/bookings/completeBooking`** — creates a booking, then fires a gRPC notification. Trip validation via gRPC is implemented but currently commented out.

**GET `/api/bookings/trip/{tripId}`** and **GET `/api/bookings/{bookingId}`** — read-only lookups.

Client IP is extracted from `X-Forwarded-For` header (or `remoteAddr`) and forwarded to the notification service.

### Package structure
```
com.bookling_service
├── controller/   # BookingController — REST endpoints
├── service/      # BookingService — business logic
├── entity/       # Booking — JPA entity
├── dto/          # BookingDtos — request/response records
├── repository/   # BookingRepository — JPA repository
└── grpc/         # TripGrpcClient, NotificationGrpcClient — outbound gRPC stubs
```

### Proto files
- `src/main/proto/trip.proto` — defines `ValidateTrip` RPC
- `src/main/proto/notification.proto` — defines `SendNotification` RPC

Both generate Java stubs into `com.bookling_service.grpc` via the `protobuf-maven-plugin`.

### JWT
A JWT secret is configured in `application.properties` (`app.jwt.secret`) but is not yet wired up to any filter or validation logic.
