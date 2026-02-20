# 🎵 RevPlay – Music Streaming Web Application

## 📌 Project Overview

RevPlay is a full-stack monolithic music streaming web application developed as part of Revature training.  
The application allows users to stream music, create playlists, mark favorites, and explore artists.  
Artists can upload songs, manage albums, and track engagement analytics.

This project demonstrates enterprise-level backend architecture using Spring Boot, Oracle Database, Thymeleaf, and Spring Security.

---

## 🏢 Organization

**Company:** Revature  
**Project Name:** RevPlay  
**Application Type:** Full-Stack Monolithic Web Application  

---

## 🛠️ Tech Stack

### Backend
- Java 17
- Spring Boot 3.5.10
- Spring Data JPA
- Spring Security
- Thymeleaf
- Maven
- Log4J2
- JUnit4

### Database
- Oracle Database
- PL/SQL
- Sequences
- Triggers
- Stored Procedures

### Frontend
- Thymeleaf Templates
- HTML5
- CSS3
- Bootstrap (Optional)
- JavaScript (for player controls)

---

## 👥 User Roles

1. USER (Listener)
2. ARTIST

---

# 🎧 Core Features

---

## 🔐 Authentication & Security

- User Registration
- Artist Registration
- Login / Logout
- Role-Based Authorization
- Password Encryption using BCrypt
- Session-Based Authentication

---

## 👤 User Features

### Profile Management
- View Profile
- Edit Profile (Display Name, Bio, Profile Picture)
- View Account Statistics

### Music Discovery
- Browse All Songs
- Search Songs, Artists, Albums, Playlists
- Filter by Genre, Artist, Album, Release Year
- View Song Details
- View Artist Profiles
- View Album Details

### Music Player
- Play / Pause
- Skip Forward / Backward
- Seek Bar
- Shuffle Mode
- Repeat Mode
- Volume Control
- Playback Queue

### Favorites
- Add to Favorites
- Remove from Favorites
- View All Favorite Songs

### Playlist Management
- Create Playlist
- Edit Playlist
- Delete Playlist
- Add / Remove Songs
- Reorder Songs
- Set Privacy (Public / Private)
- View Public Playlists
- Follow / Unfollow Playlist

### Listening History
- View Recently Played Songs
- View Complete History
- Clear Listening History

---

## 🎤 Artist Features

Artists have all USER features plus:

### Artist Profile
- Create Artist Profile
- Upload Profile Picture
- Upload Banner Image
- Add Social Media Links
- View Profile as Public

### Music Upload & Management
- Upload Songs (BLOB)
- Upload Album Artwork (BLOB)
- Create Albums
- Add Songs to Album
- Edit Song Details
- Edit Album Details
- Delete Songs
- Delete Albums
- Set Song Visibility (Public / Unlisted)

### Analytics Dashboard
- Total Songs
- Total Plays
- Total Favorites
- Most Played Songs
- Listening Trends
- Top Listeners

---

# 🗄️ Database Schema

Main Tables:

- USERS
- ARTISTS
- SONGS
- ALBUMS
- PLAYLISTS
- PLAYLIST_SONGS
- FAVORITES
- LISTENING_HISTORY

Database Features:

- Sequences for Auto Increment
- Triggers
- Stored Procedure (Increment Play Count)
- Function (Get Artist Total Plays)

---

# 🏗️ Project Architecture

Layered Architecture:

```
com.revature.revplay
│
├── config
├── controller
├── service
│    └── impl
├── repository
├── model
├── dto
├── utils
├── exception
└── RevPlayApplication.java
```

Architecture Type:
- Monolithic
- MVC Pattern
- Layered Design

---

# ▶️ How to Run the Project

### 1️⃣ Clone the Repository

```
git clone <repository-url>
```

### 2️⃣ Configure Database

Update `application.properties`:

```
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3️⃣ Build the Project

```
mvn clean install
```

### 4️⃣ Run Application

```
mvn spring-boot:run
```

Application will start at:

```
http://localhost:8080
```

---

# 🧪 Testing

- Unit Testing using JUnit4
- Service Layer Testing
- Repository Testing
- Security Testing

---

# 📊 Logging

Log4J2 is configured to log:

- Login attempts
- Song uploads
- Playlist operations
- Errors and exceptions

---

# 📄 Documentation Included

- ER Diagram
- Application Architecture Diagram
- README.md
- Testing Artifacts

---

# 🎯 Learning Outcomes

- Enterprise Spring Boot Development
- Role-Based Security Implementation
- File Upload & Streaming using BLOB
- PL/SQL Integration
- Layered Architecture Design
- Exception Handling & Logging
- Full-Stack Web Application Development

---

# 🚀 Future Enhancements

- REST API Version
- JWT Authentication
- Microservices Architecture
- Real-Time Notifications
- Cloud Deployment (AWS / Azure)

---

# 📌 Authors
 Team Lead
  Manjunath
  1. Manjunath
  2. Pooja
  3. Ramya
  4. Indraja
  5. Neha B 

---

# 📜 License

This project is developed for educational and training purposes under Revature.
