# 🎵 RevPlay — Music Streaming Application

> **⚠️ WARNING FOR TEAM MEMBERS:**
> Do **NOT** change the database connection details in your local `application.properties`. Your local config must match the shared team configuration exactly. Changing the DB credentials will break your connection to the shared Oracle database.

RevPlay is a full-stack music streaming web application built with **Spring Boot**, **Thymeleaf**, and an **Oracle Database**. It allows listeners to discover and play music, and artists to manage and upload their content.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 17, Spring Boot 3.x |
| **Security** | Spring Security 6 |
| **ORM** | Spring Data JPA / Hibernate |
| **Templating** | Thymeleaf + Thymeleaf Spring Security Extras |
| **Database** | Oracle Database (via OJDBC 11) |
| **Frontend** | Bootstrap 5.3, Vanilla CSS, JavaScript |
| **Build Tool** | Maven (Maven Wrapper included) |

---

## ⚙️ Prerequisites

Before running the application, make sure you have:

- **Java 17+** installed (JDK 17 or JDK 21 recommended)
- **Maven** or use the included `./mvnw` wrapper
- Access to the shared **Oracle Database** (credentials in `application.properties`)

---

## 🏃 Running the Application

```bash
# Using the Maven Wrapper to run the application
./mvnw spring-boot:run

# If JAVA_HOME is not set to Java 17+, override it:
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'; ./mvnw spring-boot:run
```

The application will start at: **http://localhost:8080**

### 🧪 Running Tests & Code Coverage
```bash
# Run all automated tests
./mvnw test

# Generate a JaCoCo code coverage report
./mvnw clean test jacoco:report

# Once completed, you can view the HTML report by opening:
# target/site/jacoco/index.html in your web browser.
```

> You will be redirected to the Login page immediately. Register an account to get started.

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/revature/revplay/
│   │   ├── config/          # Spring Security, Web, and general configurations
│   │   ├── controller/      # MVC Web Controllers and REST API Endpoints
│   │   ├── dto/             # Data Transfer Objects for forms & API responses
│   │   ├── entity/          # JPA Entity classes modeling the database tables
│   │   ├── exception/       # Global exception handlers & custom error classes
│   │   ├── repository/      # Spring Data JPA Repositories for data access
│   │   └── service/         # Business logic services and their implementations
│   └── resources/
│       ├── scripts/         # SQL initialization scripts and database setup
│       ├── static/          # Global assets (CSS, JS, images)
│       ├── templates/       # Thymeleaf HTML templates (UI views)
│       ├── application.properties # Main application configuration
│       └── log4j2.xml       # Externalized logging configurations
└── test/
    └── java/com/revature/revplay/
        └── service/         # JUnit & Mockito test classes for service layer
```

---

## 🗃️ Database ERD

```mermaid
erDiagram
    USERS {
        LONG user_id PK
        STRING email
        STRING username
        STRING password
        STRING role
        STRING display_name
        STRING bio
        BLOB profile_image
        TIMESTAMP created_at
    }
    ARTISTS {
        LONG artist_id PK
        LONG user_id FK
        STRING artist_name
        STRING genre
        BLOB banner_image
        STRING instagram
        STRING twitter
        STRING youtube
        STRING website
    }
    SONGS {
        LONG song_id PK
        LONG artist_id FK
        LONG album_id FK
        STRING title
        STRING genre
        INT duration
        BLOB audio_file
        BLOB cover_image
        INT play_count
        STRING visibility
        DATE release_date
    }
    ALBUMS {
        LONG album_id PK
        LONG artist_id FK
        STRING album_name
        STRING description
        DATE release_date
        BLOB cover_image
    }
    PLAYLISTS {
        LONG playlist_id PK
        LONG user_id FK
        STRING name
        STRING description
        STRING privacy
    }
    PLAYLIST_SONGS {
        LONG playlist_song_id PK
        LONG playlist_id FK
        LONG song_id FK
    }
    FAVORITES {
        LONG favorite_id PK
        LONG user_id FK
        LONG song_id FK
        TIMESTAMP added_at
    }
    LISTENING_HISTORY {
        LONG history_id PK
        LONG user_id FK
        LONG song_id FK
        TIMESTAMP played_at
    }

    USERS ||--o| ARTISTS : "is"
    ARTISTS ||--o{ SONGS : "uploads"
    ARTISTS ||--o{ ALBUMS : "creates"
    ALBUMS ||--o{ SONGS : "contains"
    USERS ||--o{ PLAYLISTS : "owns"
    PLAYLISTS ||--o{ PLAYLIST_SONGS : "has"
    SONGS ||--o{ PLAYLIST_SONGS : "in"
    USERS ||--o{ FAVORITES : "marks"
    SONGS ||--o{ FAVORITES : "favorited by"
    USERS ||--o{ LISTENING_HISTORY : "tracks"
    SONGS ||--o{ LISTENING_HISTORY : "played in"
```

---

## � Project Status: **Functional Completion Reached** ✅
All core features and user stories have been fully implemented, tested, and integrated. The application is now in the **Documentation Phase**, where we are polishing technical guides and finalizing architectural diagrams.

---

## �👥 Team & User Stories

### 👨‍💻 Manjunath (Person 1) — Authentication & Basic Profile ✅

**As a User (Listener), I should be able to:**
- ✅ Register and create an account with email, username, and password
- ✅ Login to my account using username and password
- ✅ View and edit my profile (display name, bio, profile picture)

**As a Musician/Artist, I should be able to:**
- ✅ Register as an artist with email, password, and artist details
- ✅ Login to my account
- ✅ Create and manage artist profile (artist name, bio, genre, profile picture)

**Music Discovery:**
- ✅ Browse music library with all available songs
- ✅ View song details (title, artist, album, duration, genre, release date)

**System & Infrastructure:**
- ✅ Implement system-wide Log4j2 logging for application startup, security configurations, services, and controllers

---

### 👩‍💻 Neha (Person 2) — Search, Categories & Artist/Album Views ✅

**As a User (Listener), I should be able to:**
- ✅ Search for songs, artists, albums, and playlists by keywords
- ✅ Browse content by categories (genre, artist, album)
- ✅ Filter songs by genre, artist, album, or release year

**Artist & Album Views:**
- ✅ View artist profiles with their songs and albums
- ✅ View album details with track list

**As a Musician/Artist, I should be able to:**
- ✅ View my artist profile as users see it
- ✅ Add social media links (Instagram, X, YouTube, Website)

---

### 👩‍💻 Ramya (Person 3) — Music Player & Playback Queue ✅

**As a User (Listener), I should be able to:**
- ✅ Play songs using integrated web music player
- ✅ Use player controls (play, pause, skip forward, skip backward, seek)
- ✅ View currently playing song with progress bar
- ✅ Create and manage playback queue
- ✅ Enable repeat mode (off, repeat one, repeat all)
- ✅ Enable shuffle mode
- ✅ Adjust volume control

**Listening History:**
- ✅ View recently played songs (last 50 songs)
- ✅ View complete listening history with date and time
- ✅ Clear listening history

---

### 👩‍💻 Indraja (Person 4) — Favorites & Playlist Management ✅

**As a User (Listener), I should be able to:**
- ✅ Mark songs as favorites
- ✅ Remove songs from favorites
- ✅ View all my favorite songs
- ✅ Quick access to favorite songs from player

**Playlist Management:**
- ✅ Create playlists with name and description
- ✅ Set playlist privacy (public or private)
- ✅ Add songs to my playlists
- ✅ Remove songs from my playlists
- ✅ Reorder songs in playlists
- ✅ Update playlist details (name, description, privacy)
- ✅ Delete playlists created by me
- ✅ View all my playlists
- ✅ View public playlists created by other users
- ✅ Follow/unfollow public playlists

---

### 👩‍💻 Pooja (Person 5) — Artist Upload & Analytics + Account Stats ✅

**As a Musician/Artist, I should be able to:**
- ✅ Upload songs with details (title, genre, duration, audio file)
- ✅ Upload album artwork/cover image
- ✅ Create albums with name, description, release date, and cover art
- ✅ Add songs to albums
- ✅ Remove songs from albums
- ✅ View all my uploaded songs
- ✅ View all my created albums
- ✅ Update song information (title, genre, album)
- ✅ Update album information (name, description, cover art)
- ✅ Delete my songs
- ✅ Delete my albums (if no songs are in the album)
- ✅ Set song visibility (public/unlisted)

**Analytics & Insights:**
- ✅ View dashboard with key metrics (total songs, total plays, total favorites)
- ✅ View play count for individual songs
- ✅ View songs sorted by popularity (most played)
- ✅ View list of users who favorited my songs
- ✅ View listening trends over time (daily/weekly/monthly plays)
- ✅ View top listeners (users who played my songs the most)

**Account Statistics:**
- ✅ View my account statistics (total playlists, favorite songs count, listening time)

---

## 🔐 Roles

| Role | Access |
|---|---|
| `SUBSCRIBER` | Browse music, manage profile, playlists, favorites, listening history |
| `ARTIST` | All subscriber features + Artist Studio (upload, manage songs/albums, analytics) |

---

## 📝 Technical Notes & Polish

- **Performance:** Replaced external Google Fonts with system-standard **Trebuchet MS** to eliminate blocking CDN requests and improve load times.
- **Error Handling:** Implemented a **Global Exception Handling** system with custom exceptions (`ResourceNotFoundException`, `UserAlreadyExistsException`, `InvalidFileException`) and a thematic error return page.
- **Code Health:** Performed a full project refactor, removing unused CSS, dead directory structures, and orphaned service methods.
- **UI Consistency:** Custom-styled all form elements (dropdowns, inputs) with an orange/snow design system, ensuring a premium "wow" factor across all pages.
- **Data URIs:** Profile and banner images are served as Base64 Data URIs directly from the Oracle BLOB store to minimize complex file system management.
- **Security:** Spring Security 6 integration using modern lambda-based DSL and role-based access control.
- **Logging:** Integrated **Log4j2** with a **RollingFileAppender** to capture application events, user actions, and errors in both console and persistent log files (`/logs` directory).
- **Testing:** Implemented automated unit testing with **JaCoCo** code coverage reporting to ensure high quality and maintainability of core logic.

```sql
-- --- RevPlay Database Cleanup (Oracle) ---
-- Run these to reset the schema if needed. 
-- WARNING: This will permanently delete all application data.

-- 1. Drop Tables (in order of dependency)
DROP TABLE USER_HISTORY CASCADE CONSTRAINTS;
DROP TABLE USER_LIKED_SONGS CASCADE CONSTRAINTS;
DROP TABLE USER_FOLLOWING_ARTISTS CASCADE CONSTRAINTS;
DROP TABLE PLAYLIST_SONGS CASCADE CONSTRAINTS;
DROP TABLE PLAYLISTS CASCADE CONSTRAINTS;
DROP TABLE SONGS CASCADE CONSTRAINTS;
DROP TABLE ALBUMS CASCADE CONSTRAINTS;
DROP TABLE ARTIST_PROFILES CASCADE CONSTRAINTS;
DROP TABLE USERS CASCADE CONSTRAINTS;

-- 2. Drop Sequences
DROP SEQUENCE USERS_SEQ;
DROP SEQUENCE SONGS_SEQ;
DROP SEQUENCE ALBUMS_SEQ;
DROP SEQUENCE PLAYLISTS_SEQ;
DROP SEQUENCE HISTORY_SEQ;
```