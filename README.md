# 🏙️ Citopia — Mini Transport Tycoon

**ELTE Software Technology Practice 2026 — Group 05**

A simplified transport simulation game inspired by OpenTTD, built in Java with LibGDX.

[](url)

## 🎮 About the Game

Citopia is a **mini transport tycoon** game where players build and manage a transport network connecting cities. Players purchase vehicles, plan routes, transport cargo and passengers, and grow their transport empire while managing finances.

### Core Features (Target)
- 🗺️ Tile-based game map with cities and terrain
- 🚛 Vehicle management (purchase, assign routes)
- 🛤️ Route planning and pathfinding between cities
- 💰 Economic simulation (revenue, costs, profit/loss)
- 📦 Cargo and passenger transport
- 💾 Save / Load game state
- 🖥️ 2D graphical interface with LibGDX

---

## 🛠️ Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17 |
| Game Framework | LibGDX 1.11.0 |
| ECS | Ashley 1.7.4 |
| UI Library | VisUI 1.5.0 |
| Build Tool | Gradle 7.6 |
| Testing | JUnit 5, Mockito |
| Coverage | JaCoCo |
| Version Control | GitLab |
| CI/CD | GitLab CI |

---

## 🚀 Quick Start

### Prerequisites
- Java JDK 17+
- Git

### Clone & Run
```bash
git clone https://szofttech.inf.elte.hu/software-technology-2026/group-05/citopia.git
cd citopia
./gradlew desktop:run
```

### Run Tests
```bash
./gradlew core:test
```

### Generate Coverage Report
```bash
./gradlew core:jacocoTestReport
# Report: core/build/reports/jacoco/test/html/index.html
```

### Build Distributable JAR
```bash
./gradlew desktop:dist
# Output: desktop/build/libs/
```

---

---

