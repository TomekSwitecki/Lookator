# Lookator MVP

Lookator helps home buyers evaluate the surrounding area of a property before making a decision. This scaffold implements the MVP architecture from the brief:

- React + TypeScript + SCSS Modules frontend
- Java 21 + Spring Boot REST API under `/api/v1`
- PostgreSQL with PostGIS enabled
- JWT registration and login
- Saved location analyses
- Score breakdown, strengths, weaknesses, and comparison view

## Run Locally

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs at [http://localhost:5173](http://localhost:5173).

The API runs at [http://localhost:8080/api/v1](http://localhost:8080/api/v1), with Swagger UI at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

## Environment Variables

For Railway, do not commit a real `.env` file. Add these variables in the backend service Variables panel:

- `SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:PORT/DATABASE`
- `SPRING_DATASOURCE_USERNAME=USER`
- `SPRING_DATASOURCE_PASSWORD=PASSWORD`
- `LOOKATOR_JWT_SECRET=a-long-random-secret`

The same variables are documented in `backend/.env.example`.

Railway Postgres should have the PostGIS extension enabled before location queries rely on geospatial SQL:

```sql
CREATE EXTENSION IF NOT EXISTS postgis;
```

## Current MVP Behavior

The backend includes the domain model, persistence, authentication, scoring, report generation, and comparison API. The `LocationDataProvider` interface is the provider boundary for geographic sources.

`OpenStreetMapDataProvider` currently returns deterministic sample metrics for supplied coordinates. This keeps the full user flow runnable while preserving the integration point for real Overpass/OpenStreetMap queries.

## API Shape

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/analyses`
- `GET /api/v1/analyses`
- `GET /api/v1/analyses/{id}`
- `POST /api/v1/comparisons`
- `GET /api/v1/comparisons`

## Next Backend Step

Replace the deterministic metrics inside `OpenStreetMapDataProvider` with Overpass queries for:

- public transport stops
- grocery stores
- schools and kindergartens
- pharmacies and healthcare
- parks and green areas
- railways, major roads, gas stations, and industrial land use

The scoring weights are configurable in `backend/src/main/resources/application.yml` under `lookator.scoring`.
