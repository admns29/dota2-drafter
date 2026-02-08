# Dota 2 Drafter

A web-based Dota 2 drafting simulator application built with Spring Boot and vanilla JavaScript.

## Description

The Dota 2 Drafter allows users to simulate a professional Dota 2 draft (Captain's Mode style). Users can select and ban heroes for both Radiant and Dire teams. The application fetches hero data from an external API and provides filtering and search capabilities.

## Features

- **Hero Synchronization**: Fetch and sync the latest Dota 2 hero data from an external API.
- **Interactive Drafting**: Simulate a draft with proper pick and ban phases for two teams (Radiant and Dire).
- **Hero Grid**: View all available heroes in a responsive grid.
- **Search & Filter**:
  - Search heroes by name.
  - Filter heroes by primary attribute (Strength, Agility, Intelligence, Universal).
- **Responsive Design**: Clean and usable interface for drafting on different screen sizes.

## Tech Stack

### Backend

- **Java 21**: The latest LTS version of Java.
- **Spring Boot 3.2.2**: Framework for building the REST API and serving static content.
- **Spring Data JPA**: For database interactions.
- **H2 Database**: In-memory database for storing hero data during runtime.
- **Maven**: Dependency management and build tool.
- **Lombok**: Boilerplate code reduction.

### Frontend

- **HTML5**: semantic structure.
- **CSS3**: Custom styling.
- **JavaScript (Vanilla)**: Frontend logic for drafting and API interaction.

## Prerequisites

- **Java JDK 21** or higher
- **Maven 3.6+** (optional if using the included `mvnw` wrapper)

## Installation & Setup

1. **Clone the repository:**

   ```bash
   git clone https://github.com/yourusername/dota2-drafter.git
   cd dota2-drafter
   ```

2. **Build the application:**
   Using the Maven wrapper (recommended):

   ```bash
   ./mvnw clean install
   ```

   Or using installed Maven:

   ```bash
   mvn clean install
   ```

3. **Run the application:**
   Using the Maven wrapper:

   ```bash
   ./mvnw spring-boot:run
   ```

   Or run the JAR directly:

   ```bash
   java -jar target/dota2-0.0.1-SNAPSHOT.jar
   ```

4. **Access the application:**
   Open your browser and navigate to:
   [http://localhost:8080](http://localhost:8080)

## API Endpoints

The backend exposes several REST endpoints (implied based on functionality):

- `GET /api/heroes`: List all heroes.
- `POST /api/heroes/sync`: Trigger synchronization with the OpenDota API.
- `POST /api/draft/start`: Initialize a new draft session. Returns the initial draft state.
- `POST /api/draft/{id}/pick/{heroId}`: Lock in a hero pick for the specified draft ID.
- `POST /api/draft/{id}/ban/{heroId}`: Lock in a hero ban for the specified draft ID.

## Project Structure

```
dota2-drafter/
├── src/
│   ├── main/
│   │   ├── java/com/dotadrafter/dota2/
│   │   │   ├── api/          # REST Controllers
│   │   │   ├── client/       # External API Clients
│   │   │   ├── config/       # Configuration Classes
│   │   │   ├── dto/          # Data Transfer Objects
│   │   │   ├── model/        # JPA Entities
│   │   │   ├── repository/   # Data Access Layer
│   │   │   ├── service/      # Business Logic
│   │   │   └── Dota2Application.java
│   │   └── resources/
│   │       ├── static/       # Frontend Assets (HTML, CSS, JS)
│   │       └── application.properties
├── pom.xml                   # Maven dependencies
└── README.md                 # Project documentation
```

## License

This project is open source and available under the [MIT License](LICENSE).
