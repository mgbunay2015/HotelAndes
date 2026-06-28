#!/usr/bin/env python3
"""Genera el manual PDF del proyecto Hotel Los Andes."""

from fpdf import FPDF
from pathlib import Path

OUTPUT = Path(__file__).parent.parent / "Manual_Hotel_Microservicios.pdf"


class ManualPDF(FPDF):
    def header(self):
        self.set_font("Helvetica", "B", 11)
        self.cell(0, 8, "Hotel Los Andes - Sistema de Microservicios", border=0, ln=True, align="C")
        self.ln(2)

    def footer(self):
        self.set_y(-15)
        self.set_font("Helvetica", "I", 8)
        self.cell(0, 10, f"Pagina {self.page_no()}", align="C")

    def section_title(self, title):
        self.ln(4)
        self.set_font("Helvetica", "B", 14)
        self.multi_cell(0, 8, title)
        self.ln(2)

    def sub_title(self, title):
        self.set_font("Helvetica", "B", 11)
        self.multi_cell(0, 7, title)
        self.ln(1)

    def body_text(self, text):
        self.set_font("Helvetica", "", 10)
        self.multi_cell(0, 5, text)
        self.ln(2)

    def code_block(self, text):
        self.set_font("Courier", "", 8)
        self.set_fill_color(240, 240, 240)
        for line in text.split("\n"):
            self.cell(0, 4, line, ln=True, fill=True)
        self.ln(3)


def build_manual():
    pdf = ManualPDF()
    pdf.set_auto_page_break(auto=True, margin=15)
    pdf.add_page()

    pdf.set_font("Helvetica", "B", 18)
    pdf.cell(0, 12, "Manual Tecnico", ln=True, align="C")
    pdf.set_font("Helvetica", "", 12)
    pdf.cell(0, 8, "Sistema de Reservas Hotel Los Andes", ln=True, align="C")
    pdf.cell(0, 8, "Arquitectura de Microservicios - UTI", ln=True, align="C")
    pdf.ln(8)

    pdf.section_title("1. Introduccion")
    pdf.body_text(
        "Este proyecto implementa la Actividad 3 de Microservicios: un sistema de reservas "
        "hoteleras para el Hotel Los Andes. Se basa en los patrones del proyecto "
        "comunicacionMicroServicios (DTOs, mappers, excepciones centralizadas) y en la "
        "infraestructura Docker del proyecto Biblioteca (docker-compose, nginx gateway, "
        "base de datos por servicio)."
    )

    pdf.section_title("2. Arquitectura General")
    pdf.code_block(
        "Cliente (Postman)\n"
        "       |\n"
        "  Nginx :80 (API Gateway)\n"
        "    /         \\\n"
        "svc-rooms    svc-reservations\n"
        "  :8081         :8082\n"
        "    |              |\n"
        " rooms-db    reservations-db\n"
        "  (5433)         (5434)"
    )
    pdf.body_text(
        "Reglas: cada microservicio tiene su propia base de datos PostgreSQL (Database per "
        "Service). La comunicacion entre svc-reservations y svc-rooms es exclusivamente HTTP REST. "
        "Todo el sistema se levanta con: docker-compose up -d --build"
    )

    pdf.section_title("3. Estructura del Proyecto")
    pdf.code_block(
        "Hotel/\n"
        "|-- docker-compose.yml\n"
        "|-- nginx/nginx.conf\n"
        "|-- svc-rooms/\n"
        "|   |-- pom.xml, Dockerfile\n"
        "|   +-- src/main/java/com/uti/svcrooms/\n"
        "|       |-- controller/   -> Endpoints REST\n"
        "|       |-- service/impl/ -> Logica de negocio\n"
        "|       |-- repository/   -> Acceso JPA\n"
        "|       |-- model/        -> Entidades JPA\n"
        "|       |-- dto/          -> Objetos de transferencia\n"
        "|       |-- mapper/       -> Entity <-> DTO\n"
        "|       +-- exception/    -> Manejo global de errores\n"
        "|-- svc-reservations/\n"
        "|   +-- src/main/java/com/uti/svcreservations/\n"
        "|       |-- client/       -> RestTemplate y WebClient\n"
        "|       |-- config/       -> Beans HTTP\n"
        "|       +-- (misma estructura que svc-rooms)\n"
        "+-- postman/Hotel_Los_Andes.postman_collection.json"
    )

    pdf.section_title("4. Microservicio svc-rooms (Puerto 8081)")
    pdf.sub_title("4.1 Entidad Room")
    pdf.body_text(
        "Campos: id, roomNumber (unico), type (SINGLE/DOUBLE/SUITE), pricePerNight, "
        "totalCapacity, availableRooms, floor, description."
    )
    pdf.sub_title("4.2 Endpoints")
    pdf.code_block(
        "GET    /api/v1/rooms                    -> Listar habitaciones\n"
        "GET    /api/v1/rooms/{id}               -> Obtener por ID\n"
        "POST   /api/v1/rooms                    -> Registrar habitacion\n"
        "GET    /api/v1/rooms/{id}/availability  -> Consultar disponibilidad\n"
        "PATCH  /api/v1/rooms/{id}/availability  -> Actualizar disponibilidad"
    )
    pdf.sub_title("4.3 Reglas de negocio")
    pdf.body_text(
        "- roomNumber duplicado -> 409 Conflict\n"
        "- availableRooms negativo -> 422 Unprocessable Entity\n"
        "- availableRooms > totalCapacity -> 422\n"
        "- Errores con formato: timestamp, status, error, message, path"
    )

    pdf.add_page()
    pdf.section_title("5. Microservicio svc-reservations (Puerto 8082)")
    pdf.sub_title("5.1 Entidad Reservation")
    pdf.body_text(
        "Campos: id, roomId, guestName, guestEmail, checkInDate, checkOutDate, "
        "status (ACTIVE/COMPLETED/CANCELLED), totalNights (calculado), createdAt."
    )
    pdf.sub_title("5.2 Endpoints")
    pdf.code_block(
        "POST  /api/v1/reservations              -> Crear reserva\n"
        "GET   /api/v1/reservations/{id}         -> Obtener con datos del cuarto\n"
        "GET   /api/v1/reservations/guest/{email} -> Reservas por email\n"
        "PATCH /api/v1/reservations/{id}/checkout -> Registrar salida"
    )
    pdf.sub_title("5.3 Comunicacion HTTP con svc-rooms")
    pdf.body_text(
        "createReservation(): RestTemplate -> GET /availability; WebClient -> GET /rooms/{id}\n"
        "getReservationById(): RestTemplate -> GET /rooms/{id}\n"
        "getReservationsByEmail(): WebClient -> GET /rooms/{id}\n"
        "checkout(): RestTemplate -> GET /rooms/{id}"
    )
    pdf.sub_title("5.4 RestTemplate vs WebClient")
    pdf.body_text(
        "RestTemplate: cliente sincrono bloqueante. El hilo espera la respuesta HTTP. "
        "Ideal para operaciones secuenciales donde se necesita el resultado inmediato.\n\n"
        "WebClient: cliente reactivo no bloqueante (Spring WebFlux). Permite programacion "
        "asincrona con Mono/Flux. En este proyecto se usa block() para integrarlo con "
        "servicios sincronos. La actividad exige usar ambos en metodos especificos."
    )

    pdf.section_title("6. Resilience4j - Patrones de Resiliencia")
    pdf.body_text(
        "Configurado en svc-reservations para proteger llamadas a svc-rooms:\n\n"
        "Circuit Breaker: sliding-window-size=5, failure-rate-threshold=50%, "
        "wait-duration=10s. Tras 3 fallos consecutivos el circuito se abre.\n\n"
        "Retry: max-attempts=3, wait-duration=2s. Reintenta llamadas fallidas.\n\n"
        "Fallback: cuando svc-rooms no responde:\n"
        "- Crear reserva -> 503 'Room information temporarily unavailable'\n"
        "- Consultar reserva -> 200 con roomNumber='Room information temporarily unavailable'"
    )

    pdf.section_title("7. Nginx API Gateway")
    pdf.body_text(
        "Nginx escucha en puerto 80 y enruta segun la URI:\n"
        "- /api/v1/rooms* -> svc-rooms:8081\n"
        "- /api/v1/reservations* -> svc-reservations:8082\n\n"
        "El cliente (Postman) siempre apunta a http://localhost (puerto 80)."
    )

    pdf.section_title("8. Docker Compose")
    pdf.code_block(
        "Servicio           Puerto externo   Descripcion\n"
        "nginx-gateway      80               API Gateway\n"
        "svc-rooms          8081             Microservicio habitaciones\n"
        "svc-reservations   8082             Microservicio reservas\n"
        "rooms-db           5433             PostgreSQL svc-rooms\n"
        "reservations-db    5434             PostgreSQL svc-reservations"
    )
    pdf.body_text("Comando de inicio: docker-compose up -d --build")

    pdf.add_page()
    pdf.section_title("9. Flujo de Creacion de Reserva")
    pdf.code_block(
        "1. Cliente POST /api/v1/reservations\n"
        "2. Nginx enruta a svc-reservations:8082\n"
        "3. Validar fechas y reserva duplicada\n"
        "4. RestTemplate -> svc-rooms GET /availability\n"
        "5. Si available=false -> 422 'Room is not available'\n"
        "6. WebClient -> svc-rooms GET /rooms/{id} (enriquecer respuesta)\n"
        "7. Guardar reserva en reservations-db\n"
        "8. RestTemplate PATCH availability (decrementar)\n"
        "9. Retornar 201 con datos enriquecidos"
    )

    pdf.section_title("10. Pruebas Minimas (Postman)")
    pdf.body_text(
        "1. Registrar 3 habitaciones (SINGLE, DOUBLE, SUITE) -> 201\n"
        "2. Crear reserva valida -> 201 enriquecida\n"
        "3. Reserva sin disponibilidad -> 422\n"
        "4. Reserva duplicada ACTIVE -> 422\n"
        "5. Consultar reserva por ID -> 200 con datos del cuarto\n"
        "6. Checkout reserva ACTIVE -> 200 COMPLETED\n"
        "7. Checkout reserva COMPLETED -> 422\n"
        "8. Parar svc-rooms, crear reserva -> 503 fallback\n"
        "9. Consultar reserva con svc-rooms caido -> 200 fallback\n"
        "10. Reiniciar svc-rooms -> recuperacion automatica"
    )

    pdf.section_title("11. Concepto de Funcionamiento por Archivo")
    files = [
        ("docker-compose.yml", "Orquesta todos los contenedores: BDs, microservicios y nginx."),
        ("nginx/nginx.conf", "Configura el API Gateway con reglas de enrutamiento por URI."),
        ("SvcRoomsApplication.java", "Punto de entrada Spring Boot de svc-rooms."),
        ("Room.java", "Entidad JPA mapeada a tabla rooms en PostgreSQL."),
        ("RoomController.java", "Expone endpoints REST /api/v1/rooms."),
        ("RoomServiceImpl.java", "Implementa reglas de negocio de habitaciones."),
        ("RoomMapper.java", "Convierte entre entidad Room y DTOs sin exponer JPA."),
        ("GlobalExceptionHandler.java", "Centraliza errores con formato uniforme JSON."),
        ("AppConfig.java", "Define beans RestTemplate y WebClient para svc-rooms."),
        ("RoomsRestTemplateClient.java", "Cliente sincrono con Circuit Breaker, Retry y Fallback."),
        ("RoomsWebClient.java", "Cliente reactivo con mismos patrones de resiliencia."),
        ("ReservationServiceImpl.java", "Orquesta reservas y llamadas HTTP a svc-rooms."),
        ("application.properties", "Configura BD, URL de svc-rooms y Resilience4j."),
    ]
    for name, desc in files:
        pdf.sub_title(name)
        pdf.body_text(desc)

    pdf.output(str(OUTPUT))
    print(f"Manual generado: {OUTPUT}")


if __name__ == "__main__":
    build_manual()
