# 🏙️ Citopia — Mini Transport Tycoon

**ELTE Software Technology Practice 2026 — Group 05**

A simplified transport simulation game inspired by OpenTTD, built in Java with LibGDX.

---

## 👥 Team Members

| Name | GitLab Username | Role |
|------|----------------|------|
| Ehsanullah Ehsanullah | @Sano | TBD |
| Al Dabbas Tayma | @Tayma | TBD |
| Samma Mustapha | @Mustapha | TBD |
| Sultanalieva Aiperi | @Alieva | TBD |

> Roles will be assigned during Milestone 1 planning.

---

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

## 📅 Milestones

### Milestone 1 — Foundation & Architecture *(Due: March 14, 2026)*
> Project setup, architecture design, core class structure, initial testing

### Milestone 2 — Core Game Logic *(Due: March 28, 2026)*
> Vehicle movement, pathfinding, economic engine, game state management

### Milestone 3 — User Interface & Interaction *(Due: April 11, 2026)*
> LibGDX GUI, user controls, save/load, complete gameplay loop

### Milestone 4 — Polish & Documentation *(Due: April 25, 2026)*
> Testing (>80% coverage), optimization, final docs, presentation

See [MILESTONES.md](MILESTONES.md) for detailed task breakdown.

---

## 📁 Project Structure

```
citopia/
├── .gitlab/                    # GitLab templates
│   ├── issue_templates/        # Issue templates (Feature, Bug, Task)
│   └── merge_request_templates/# MR template
├── core/                       # Core game module
│   └── src/
│       ├── main/java/com/citopia/
│       │   ├── model/          # Game entities & logic
│       │   ├── view/           # Rendering & screens
│       │   ├── controller/     # Input & game control
│       │   ├── util/           # Algorithms & helpers
│       │   └── persistence/    # Save/load system
│       └── test/java/com/citopia/
│           ├── model/          # Model unit tests
│           ├── controller/     # Controller tests
│           └── util/           # Utility tests
├── desktop/                    # Desktop launcher module
├── assets/                     # Game assets (sprites, maps, sounds)
├── docs/                       # Documentation
│   ├── uml/                    # UML diagrams
│   └── wiki/                   # Local wiki backup
├── .gitlab-ci.yml              # CI/CD pipeline
├── MILESTONES.md               # Detailed milestone breakdown
├── CONTRIBUTING.md              # Development guidelines
└── build.gradle                # Root build configuration
```

---

## 🔀 Git Workflow

| Branch | Purpose |
|--------|---------|
| `master` | Stable, reviewed code only |
| `feature/[issue#]-description` | Feature development |
| `bugfix/[issue#]-description` | Bug fixes |

**Rules:**
- Never push directly to `master`
- All changes go through Merge Requests
- At least 1 approval required before merging
- CI pipeline must pass before merge

---

## 📄 License

Academic project — ELTE Software Technology Practice 2026. For educational purposes only.
