# USSD Wallet

A complete USSD backend built with **Java 17** and **Spring Boot**, simulating a mobile wallet service accessible via the `*123#` code.

> **Note**: This is an educational project built to understand the architecture of real-world USSD systems. The external core (banking operations) is stubbed.

---

## 📖 What is USSD?

**USSD** (Unstructured Supplementary Service Data) is a protocol used by mobile network operators to provide text-based services on any mobile phone — no internet, no app installation required.

A typical session looks like this:
*123#
Welcome

Check balance

Transfer

Package

Language

Quit


Unlike SMS, USSD is session-based. The backend must remember where the user is between requests.

## Features

| # | Feature | Description |
|---|---------|-------------|
| 1 | Check balance | Instant balance consultation |
| 2 | Transfer | Multi-step transfer with confirmation |
| 3 | Package | Internet package purchase (500 MB / 1 GB / 2 GB) |
| 4 | Language | Persistent FR/EN switch |
| 5 | Quit | Explicit session termination |

## Architecture
HTTP Request
↓
UssdController
↓
UssdService (orchestration)
├── SessionService → SessionRepository → MongoDB
├── MenuService → MessageService (i18n)
└── BusinessService → CoreInterface (port)


Principles:
- Strict separation of concerns
- Ports and adapters (CoreInterface)
- Stateless server (sessions in MongoDB)
- Explicit session lifecycle (ACTIVE / TERMINATED / EXPIRED)

## Menu Tree

*123# (menuLevel = "0")
├── 1 → Check balance → END
├── 2 → Transfer (menuLevel = "02")
│ ├── [recipient] (menuLevel = "021")
│ ├── [amount] (menuLevel = "022")
│ ├── 1. Confirm → END
│ └── 2. Cancel → back to "0"
├── 3 → Package (menuLevel = "03")
│ ├── 1. 500 MB - 500 FCFA (menuLevel = "031")
│ ├── 2. 1 GB - 1000 FCFA (menuLevel = "032")
│ ├── 3. 2 GB - 1800 FCFA (menuLevel = "033")
│ └── [confirmation] → END
├── 4 → Language (menuLevel = "04")
│ ├── 1. Français → END
│ └── 2. English → END
└── 5 → Quit → END


The `menuLevel` string encodes the navigation path. Example: `"032"` means main menu → 3 (package) → 2 (1 GB). The path **is** the state.

## Session Model

```java
UssdSession {
    String id               // "msisdn:sessionId"
    String msisdn
    String sessionId
    String menuLevel        // path in the menu tree
    SessionStatus status    // ACTIVE, TERMINATED
    LocalDateTime createdAt
    LocalDateTime expirationDate
    Map<String, Object> temporaryData
}

Lifecycle:

Status	Meaning
ACTIVE	Session is in use
TERMINATED	User explicitly quit
expired	TTL exceeded (2 minutes)
A session is invalid if it is expired OR terminated. Invalid sessions are replaced by a new one.

Tech Stack
Java 17

Spring Boot 4.1.1

Spring Data MongoDB

Lombok

JUnit 5 + Mockito

SpringDoc OpenAPI (Swagger UI)

Getting Started
Prerequisites:

Java 17+

Maven

MongoDB on localhost:27017

Swagger UI:

text
http://localhost:8080/swagger-ui/index.html
Example Request
First USSD composition:

bash
curl -X POST http://localhost:8080/api/ussd \
  -H "Content-Type: application/json" \
  -d '{
    "msisdn": "699112233",
    "sessionId": "sess-001",
    "message": "",
    "provider": "mtn"
  }'
Response:

json
{
  "message": "Bienvenue\n1. Consulter mon solde\n2. Transférer\n3. Forfait\n4. Langue\n5. Quitter",
  "command": "CONTINUE"
}

Then choose option "1":

bash
curl -X POST http://localhost:8080/api/ussd \
  -H "Content-Type: application/json" \
  -d '{
    "msisdn": "699112233",
    "sessionId": "sess-001",
    "message": "1",
    "provider": "mtn"
  }'
Response:

json
{
  "message": "Votre solde est de 5000 FCFA",
  "command": "END"
}

Roadmap
Integration tests with Testcontainers + MongoDB

Real implementation of CoreInterface (HTTP client)

Typed TemporaryData class (replace Map<String, Object>)

Multi-operator adapters (MTN, Orange, Campost)

Observability (Micrometer, structured logs)

Dockerization

Project Structure
text
src/main/java/com/ussddev/ussdproject/
├── controller/    HTTP endpoints
├── service/       Business + orchestration
├── repository/    MongoDB access
├── model/         Entities, DTOs, enums
└── core/          Ports and adapters

src/test/java/     JUnit + Mockito tests

Author
Kontchou Djoumessi Joram Cabrel
LinkedIn:https://www.linkedin.com/in/joram-kontchou-b913592b6/
Email: joramcabrelkontchoudjoumessi@gmail.com

