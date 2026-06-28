# Hotel Los Andes — Microservicios

Sistema de reservas hoteleras para la **Actividad 3 — Microservicios UTI**.

**Repositorio:** https://github.com/mgbunay2015/HotelAndes

## Arquitectura

```
Postman → Nginx :80 → svc-rooms :8081 → rooms-db (PostgreSQL :5433)
                    → svc-reservations :8082 → reservations-db (PostgreSQL :5434)
                              ↓ HTTP REST
                         svc-rooms
```

## Levantar el sistema

```bash
docker compose up -d --build
```

## Endpoints (via Nginx)

| Servicio | Endpoint |
|----------|----------|
| svc-rooms | `GET/POST /api/v1/rooms` |
| svc-rooms | `GET/PATCH /api/v1/rooms/{id}/availability` |
| svc-reservations | `POST /api/v1/reservations` |
| svc-reservations | `GET /api/v1/reservations/{id}` |
| svc-reservations | `GET /api/v1/reservations/guest/{email}` |
| svc-reservations | `PATCH /api/v1/reservations/{id}/checkout` |

## Comunicacion entre servicios

| Metodo | Cliente | Proposito |
|--------|---------|-----------|
| createReservation() | RestTemplate | Verificar disponibilidad |
| createReservation() | WebClient | Datos del cuarto |
| getReservationById() | RestTemplate | Enriquecer respuesta |
| getReservationsByEmail() | WebClient | Enriquecer respuesta |
| checkout() | RestTemplate | Datos del cuarto |

## Resilience4j

- Circuit Breaker: sliding-window-size=5, failure-rate-threshold=50%, wait-duration=10s
- Retry: max-attempts=3, wait-duration=2s
- Fallback: `Room information temporarily unavailable`

## Postman

Importar: `postman/Hotel_Los_Andes.postman_collection.json`

Base URL: `http://localhost`

## Estructura

```
svc-rooms/          → com.uti.svcrooms
svc-reservations/   → com.uti.svcreservations
docker-compose.yml
nginx/nginx.conf
postman/
video/
```
