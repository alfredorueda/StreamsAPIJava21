# 🎵 Advanced Java 21 Streams & Lambdas Mastery

> "In learning you will teach, and in teaching you will learn." — Phil Collins

## 📘 Project Overview

This is an advanced academic project designed to help Java developers deeply practice and master key features introduced and enhanced in Java 21, including:
- ✅ Streams
- ✅ Lambdas
- ✅ Method References
- ✅ Records

Unlike the many beginner-level examples that can be found online, this repository is built with seasoned professionals in mind.

The exercises were originally created for a corporate training program delivered to a top-tier international tech company, where the participants were highly experienced software engineers preparing for the official Oracle Java 21 certification.

Each of the 15 exercises includes:
- A progressively increasing level of complexity.
- A real-world inspired domain (Spotify-style music streaming logic).
- Clean, idiomatic use of modern Java 21 features — using only the standard JDK, no external libraries.
- Detailed JUnit 5 test cases with clear documentation of the desired functionality.

## 🎯 Purpose

This repository is made public to support other professionals who:
- Are preparing for the Java 21 certification.
- Want to sharpen their skills with realistic, professionally relevant exercises.
- Are looking for examples that go beyond the basics, using functional programming patterns in Java at a senior engineering level.

## 🙌 Who Is This For?
- Senior backend developers
- Java educators and trainers
- Certification candidates
- Teams preparing for interviews or internal upskilling in modern Java

## 📋 Exercises Overview

The project contains 15 increasingly complex exercises in the `MusicAnalyticsService` class:

1. **Average Genre Popularity**: Calculate average popularity ratings for each music genre
2. **Popular Songs by Genre**: Find the most popular song in each music genre
3. **Playlist Duration**: Calculate the total playing time of all songs in a user's playlists
4. **User Genre Overlaps**: Find users who share musical taste based on genre preferences
5. **Top Songs Ranking**: Generate a ranked list of the most played songs across the platform
6. **Complex Album Filtering**: Find albums matching multiple complex criteria for content curation
7. **Album Summary Generation**: Generate rich album summaries for the platform's user interface
8. **User Statistics**: Generate detailed user profile statistics for personalization
9. **Premium vs. Free Analysis**: Generate listening statistics comparing premium and free user behavior
10. **Personalized Recommendations**: Generate personalized song recommendations based on user preferences
11. **Catalog Analysis**: Analyze music catalog distribution by decade and genre
12. **Artist Collaborations**: Discover artist collaboration networks within the music catalog
13. **Genre Affinity Scoring**: Calculate users' affinity scores for different music genres
14. **Dynamic Playlist Generation**: Generate smart playlists tailored to specific activities and moods
15. **Track Transitions**: Analyze track transition patterns to improve shuffle and radio algorithms

## 🚀 Getting Started

1. **Prerequisites**
   - Java 21 JDK installed on your machine
   - Maven installed

2. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/java21-streams-lambdas.git
   cd java21-streams-lambdas
   ```

3. **Build with Maven**
   ```bash
   mvn clean install
   ```

4. **Run the tests**
   ```bash
   mvn test
   ```

5. **Study the code**
   - Start by examining the model classes in `com.streamexercises.model`
   - Review each exercise in `MusicAnalyticsService.java`
   - Understand the test cases in `MusicAnalyticsServiceTest.java`

## 🏗️ Project Structure

```
src/
├── main/java/com/streamexercises/
│   ├── model/
│   │   ├── Album.java          # Music album representation
│   │   ├── Genre.java          # Music genre enum
│   │   ├── Playlist.java       # User playlist implementation
│   │   ├── Song.java           # Song with metadata
│   │   └── User.java           # User profile with preferences
│   └── service/
│       └── MusicAnalyticsService.java  # Contains all 15 exercises
└── test/java/com/streamexercises/
    └── service/
        └── MusicAnalyticsServiceTest.java  # Comprehensive tests
```

## 🛠️ Technologies Used
- Java 21 (standard JDK only)
- JUnit 5
- Maven

## 💡 Learning Path Recommendation

1. Start with the simplest exercises (1-5)
2. Move to intermediate complexity (6-10)
3. Challenge yourself with advanced exercises (11-15)
4. Try implementing variations of each solution

## 📄 License

MIT License

## ✨ Credits

This project was designed as part of a high-level corporate training initiative for senior software engineers preparing for Java 21 certification.

---

*"The function of good software is to make the complex appear to be simple." — Grady Booch*