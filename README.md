# Bengaluru Environment Data Pipeline

A Spring Boot service that fetches weather and air-quality readings for multiple Bengaluru areas, validates and normalizes the data, stores it in PostgreSQL, exposes it through a REST API, and raises threshold-based PM2.5 alerts.

## Overview

This project is built as a backend data pipeline and designed for learning and portfolio demonstration.

It does the following:
- pulls weather data from Open-Meteo for multiple Bengaluru locations
- pulls air-quality data from Open-Meteo for the same locations
- validates and normalizes every reading
- stores readings in PostgreSQL in long format
- provides REST APIs for filtered querying and latest values
- uses a custom LRU cache for latest-readings performance
- raises PM2.5 alerts when the recent moving average crosses a threshold
- includes concurrent ingestion with timeout + retry logic
- includes benchmark reporting for ingestion and cache performance

## Tech Stack

- Java 21
- Maven
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- PostgreSQL
- Lombok (optional, used only if desired)
- Jackson
- RestClient
- JUnit 5
- Mockito

## Area Coverage

The service tracks the following Bengaluru areas:
- Koramangala
- Indiranagar
- Whitefield
- Jayanagar
- Electronic City
- Hebbal

## Data Model

The project stores data in a single long-format `reading` table.

Schema idea:
- id
- area
- metric
- value
- unit
- observed_at
- source

A uniqueness constraint is used on `(area, metric, observed_at)` to prevent duplicates.

## Architecture

The project follows a simple pipeline architecture:

```text
Scheduler
  -> Fetchers (Weather + Air Quality)
  -> Pipeline (Validate -> Normalize -> Enrich)
  -> PostgreSQL
  -> REST API
  -> LRU Cache
  -> Alert Engine
```

## Project Structure

```text
src/main/java/com/thanmayee/envpipeline/
├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── model/
├── pipeline/
├── repository/
├── scheduler/
├── service/
├── source/
├── cache/
├── ReadingServiceApplication.java
└── ...
```

## External Data Sources

Weather data:
- https://api.open-meteo.com/v1/forecast

Air-quality data:
- https://air-quality-api.open-meteo.com/v1/air-quality

The fetchers use the latitude/longitude for each area and request the relevant hourly fields, such as:
- `temperature_2m`
- `pm2_5`
- timezone: `Asia/Kolkata`

## Environment Variables

Use environment variables instead of hardcoded secrets.

Example:
```bash
export DB_URL=jdbc:postgresql://your-host:5432/your-db
export DB_USERNAME=your_user
export DB_PASSWORD=your_password
```

The application uses these in `application.properties`.

## Run Locally

### Prerequisites
- Java 21
- Maven
- PostgreSQL database available
- Environment variables set correctly

### Steps
1. Clone the repository.
2. Open in Eclipse or run from terminal.
3. Set the DB environment variables.
4. Run the app with Maven or Eclipse.
5. Confirm the app starts successfully.

Example Maven run:
```bash
./mvnw spring-boot:run
```

On Windows:
```bash
mvnw.cmd spring-boot:run
```

## API Endpoints

### Readings
- `GET /api/readings` — query readings with filtering and pagination
- `GET /api/readings/latest?area=` — get the latest values for a given area

### Areas
- `GET /api/areas` — list supported areas

### Alerts
- `GET /api/alerts` — get PM2.5 alerts based on configured thresholds

### Health
- `GET /actuator/health`

## Example Requests

```http
GET /api/readings?area=KORAMANGALA&metric=pm2_5&page=0&size=10
GET /api/readings/latest?area=KORAMANGALA
GET /api/areas
GET /api/alerts
```

## Error Handling

The app uses a global exception handler for consistent error responses.

Expected behavior:
- `400 Bad Request` for invalid parameters
- `404 Not Found` when no matching readings exist
- a consistent JSON error body across controllers

## Ingestion and Concurrency

The ingestion flow is:
- for each source
- for each area
- fetch data
- retry on failure with exponential backoff
- run source-area tasks concurrently with a bounded thread pool
- log failures without stopping the whole run

## Caching

The project includes a custom LRU cache implemented from scratch with:
- `HashMap` for O(1) lookup
- a doubly linked list for recency-order tracking
- capacity-based eviction
- synchronized access for thread safety

This is used for the newest reading results for an area.

## Alert Logic

The alert engine computes a moving average over the most recent N PM2.5 readings for each area, then compares that average against a configured threshold.

Thresholds and window size are configured in `application.properties`.

## Benchmarking

The project measures and prints:
- sequential ingestion time
- concurrent ingestion time
- cached vs uncached latest endpoint timing

This is meant for local run-time benchmarking. Actual results depend on:
- network conditions
- database latency
- machine speed
- amount of data already in PostgreSQL

## Testing

The project includes JUnit 5 + Mockito tests for:
- pipeline stages
- LRU cache behavior
- alert logic
- ingestion service behavior using mocked sources

Run tests with:
```bash
./mvnw test
```

## Security and Hygiene

- no secrets are committed to source code
- database credentials come from environment variables
- `.env` files and local secrets are ignored via `.gitignore`

## Future Improvements

If this project were scaled further, the next changes would likely include:
- Kafka/event-driven ingestion
- stronger DB indexing and partitioning
- dashboarding and analytics
- richer alerts and persistence
- deployment automation
- better monitoring and observability

## License

This project is for educational and portfolio purposes.
