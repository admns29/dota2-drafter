# Dota 2 Drafter

A Spring Boot REST API for managing Dota 2 hero drafts with Captain's Mode mechanics. This application allows you to simulate the Dota 2 professional drafting phase with hero picks and bans.

## Features

- **Hero Management**: Sync and retrieve all Dota 2 heroes from the OpenDota API
- **Draft Mechanics**: Simulate Captain's Mode drafting with alternating picks and bans
- **Team Management**: Support for both Radiant and Dire teams
- **Hero Statistics**: Store and display hero attributes (Strength, Agility, Intelligence, Universal)
- **H2 Database**: In-memory database for quick development and testing
- **RESTful API**: Clean REST endpoints for all operations

## Tech Stack

- **Java 21**: Modern Java with enhanced features
- **Spring Boot 3.2.2**: Application framework
- **Spring Data JPA**: Database access
- **H2 Database**: In-memory database
- **Lombok**: Reduces boilerplate code
- **WebFlux**: Reactive web client for external API calls
- **Maven**: Build and dependency management

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/admns29/dota2-drafter.git
cd dota2-drafter
```

### 2. Build the project

```bash
./mvnw clean install
```

Or on Windows:

```bash
mvnw.cmd clean install
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access H2 Console (Optional)

Visit `http://localhost:8080/h2-console` to access the H2 database console.

**Connection details:**
- JDBC URL: `jdbc:h2:mem:dota2db`
- Username: `sa`
- Password: (leave empty)

## API Endpoints

### Hero Endpoints

#### Get All Heroes
```http
GET /api/heroes
```
Returns a list of all heroes in the database.

#### Sync Heroes from OpenDota API
```http
POST /api/heroes/sync
```
Fetches and stores all Dota 2 heroes from the OpenDota API with their stats.

**Response:**
```json
"Successfully synced 124 heroes from OpenDota API"
```

### Draft Endpoints

#### Start a New Draft
```http
POST /api/draft/start
```
Initializes a new draft session.

**Response:**
```json
{
  "id": 1,
  "startTime": "2024-02-06T14:30:00",
  "radiantPicks": [],
  "direPicks": [],
  "radiantBans": [],
  "direBans": [],
  "radiantTurn": true,
  "pickPhase": false,
  "complete": false,
  "currentTurnIndex": 0
}
```

#### Pick a Hero
```http
POST /api/draft/{draftId}/pick/{heroId}
```
Picks a hero for the current team during pick phase.

**Path Parameters:**
- `draftId`: The ID of the draft session
- `heroId`: The ID of the hero to pick

#### Ban a Hero
```http
POST /api/draft/{draftId}/ban/{heroId}
```
Bans a hero for the current team during ban phase.

**Path Parameters:**
- `draftId`: The ID of the draft session
- `heroId`: The ID of the hero to ban

## Draft Flow

The application follows a simplified Captain's Mode format:

1. **Ban Phase**: Teams alternate banning 4 heroes (2 per team)
2. **Pick Phase**: Teams pick heroes in alternating turns until each team has 5 heroes
3. **Draft Complete**: The draft ends when both teams have selected 5 heroes

The turn automatically switches between Radiant and Dire teams, and phases transition from ban to pick automatically.

## Data Model

### Hero
```java
- id: Long
- name: String
- primaryAttribute: HeroAttribute (STRENGTH, AGILITY, INTELLIGENCE, UNIVERSAL)
- roles: List<String>
- baseStrength: double
- baseAgility: double
- baseIntelligence: double
- imageUrl: String
```

### DraftState
```java
- id: Long
- startTime: LocalDateTime
- radiantPicks: List<Hero>
- direPicks: List<Hero>
- radiantBans: List<Hero>
- direBans: List<Hero>
- isRadiantTurn: boolean
- isPickPhase: boolean
- isComplete: boolean
- currentTurnIndex: int
```

## Example Usage

### 1. Sync heroes first
```bash
curl -X POST http://localhost:8080/api/heroes/sync
```

### 2. Get all heroes to see their IDs
```bash
curl http://localhost:8080/api/heroes
```

### 3. Start a new draft
```bash
curl -X POST http://localhost:8080/api/draft/start
```

### 4. Ban heroes (first 4 turns)
```bash
# Radiant bans hero ID 1
curl -X POST http://localhost:8080/api/draft/1/ban/1

# Dire bans hero ID 2
curl -X POST http://localhost:8080/api/draft/1/ban/2

# Continue alternating...
```

### 5. Pick heroes (after ban phase)
```bash
# Radiant picks hero ID 10
curl -X POST http://localhost:8080/api/draft/1/pick/10

# Dire picks hero ID 11
curl -X POST http://localhost:8080/api/draft/1/pick/11

# Continue until 5 picks per team...
```

## Project Structure

```
src/
├── main/
│   ├── java/com/dotadrafter/dota2/
│   │   ├── api/
│   │   │   └── MyController.java          # REST endpoints
│   │   ├── client/
│   │   │   └── OpenDotaClient.java        # OpenDota API client
│   │   ├── config/
│   │   │   └── WebClientConfig.java       # WebClient configuration
│   │   ├── dto/
│   │   │   └── HeroStatsDto.java          # Data transfer object
│   │   ├── model/
│   │   │   ├── DraftState.java            # Draft entity
│   │   │   ├── Hero.java                  # Hero entity
│   │   │   └── HeroAttribute.java         # Hero attribute enum
│   │   ├── repository/
│   │   │   ├── DraftRepository.java       # Draft data access
│   │   │   └── HeroRepository.java        # Hero data access
│   │   ├── service/
│   │   │   ├── DraftService.java          # Draft business logic
│   │   │   └── HeroService.java           # Hero business logic
│   │   └── Dota2Application.java          # Application entry point
│   └── resources/
│       └── application.properties          # Application configuration
└── test/                                   # Test files
```

## Development

### Running Tests

```bash
./mvnw test
```

### Building for Production

```bash
./mvnw clean package
java -jar target/dota2-0.0.1-SNAPSHOT.jar
```

## Configuration

You can customize the application by modifying `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Database settings
spring.datasource.url=jdbc:h2:mem:dota2db

# Logging level
logging.level.com.dotadrafter.dota2=DEBUG
```

## External API

This project uses the [OpenDota API](https://docs.opendota.com/) to fetch hero data:
- Endpoint: `https://api.opendota.com/api/heroStats`
- No authentication required
- Returns comprehensive hero statistics

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Future Enhancements

- [ ] Add hero counter suggestions based on draft state
- [ ] Implement full Captain's Mode draft sequence
- [ ] Add WebSocket support for real-time draft updates
- [ ] Create a frontend UI for visual draft selection
- [ ] Add draft history and analytics
- [ ] Implement player profiles and draft recommendations
- [ ] Add support for different game modes (All Pick, Random Draft, etc.)

## License

This project is open source and available under the [MIT License](LICENSE).

## Acknowledgments

- [OpenDota](https://www.opendota.com/) for providing the hero data API
- [Valve Corporation](https://www.valvesoftware.com/) for creating Dota 2
- Spring Boot community for excellent documentation

## Contact

Project Link: [https://github.com/admns29/dota2-drafter](https://github.com/admns29/dota2-drafter)

---

**Note**: This is a hobby project for learning purposes and is not affiliated with Valve Corporation or Dota 2.
