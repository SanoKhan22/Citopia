# 📅 Citopia — Milestone & Task Breakdown

---

## Milestone 1 — Foundation & Architecture
**Due: March 14, 2026 (Weeks 1–2)**

> Goal: Set up the project, design the architecture, create the base classes, and establish team workflows.

| # | Task | Type | Priority | Assignee |
|---|------|------|----------|----------|
| 1 | Set up LibGDX project with Gradle build system | architecture | critical | — |
| 2 | Configure GitLab CI/CD pipeline (build + test) | architecture | critical | — |
| 3 | Design core class hierarchy (City, Vehicle, Route, Cargo) | architecture | high | — |
| 4 | Create UML class diagram for model package | documentation | high | — |
| 5 | Implement `City` model class with unit tests | feature | high | — |
| 6 | Implement `Vehicle` model class with unit tests | feature | high | — |
| 7 | Implement `Route` model class with unit tests | feature | high | — |
| 8 | Implement `Cargo` / `CargoType` model classes with tests | feature | medium | — |
| 9 | Implement `GameState` class (holds world data) | feature | high | — |
| 10 | Set up JUnit 5 + JaCoCo test infrastructure | testing | high | — |
| 11 | Write project README and CONTRIBUTING guide | documentation | medium | — |
| 12 | Define team roles and assign responsibilities | task | medium | — |

**Deliverables:**
- Working Gradle project that compiles and runs tests in CI
- Core model classes with >90% test coverage
- UML class diagram
- README, CONTRIBUTING, MILESTONES documentation

---

## Milestone 2 — Core Game Logic
**Due: March 28, 2026 (Weeks 3–4)**

> Goal: Implement the simulation engine — vehicles move, cargo is transported, money is earned/spent.

| # | Task | Type | Priority | Assignee |
|---|------|------|----------|----------|
| 1 | Implement tile-based game world / map representation | feature | critical | — |
| 2 | Implement A* pathfinding between cities | feature | critical | — |
| 3 | Implement vehicle movement system along routes | feature | critical | — |
| 4 | Build economic engine (purchase costs, running costs, revenue) | feature | high | — |
| 5 | Implement cargo loading/unloading at stations | feature | high | — |
| 6 | Add game time system with tick-based progression | feature | high | — |
| 7 | Implement `GameEngine` class to run simulation loop | feature | high | — |
| 8 | Create integration tests for full transport scenarios | testing | high | — |
| 9 | Add CLI / debug interface for testing game logic | feature | medium | — |
| 10 | Update UML diagrams with new classes | documentation | medium | — |
| 11 | Write unit tests for pathfinding algorithm | testing | high | — |
| 12 | Write unit tests for economic calculations | testing | high | — |

**Deliverables:**
- Functional simulation: vehicles travel routes, deliver cargo, generate profit
- Pathfinding algorithm with tests
- Economic system with tests
- Integration test covering a complete transport scenario
- Updated UML diagrams

---

## Milestone 3 — User Interface & Interaction
**Due: April 11, 2026 (Weeks 5–6)**

> Goal: Build the graphical interface so the game is playable with mouse/keyboard interaction.

| # | Task | Type | Priority | Assignee |
|---|------|------|----------|----------|
| 1 | Create main menu screen (New Game, Load, Quit) | feature | high | — |
| 2 | Implement game map renderer (tiles, cities, roads) | feature | critical | — |
| 3 | Render vehicles moving on the map | feature | critical | — |
| 4 | Build sidebar UI panel (finances, game controls) | feature | high | — |
| 5 | Implement vehicle purchase dialog | feature | high | — |
| 6 | Implement route creation UI (click city A → city B) | feature | high | — |
| 7 | Add camera controls (pan, zoom) | feature | medium | — |
| 8 | Implement game speed controls (pause, 1x, 2x, 3x) | feature | medium | — |
| 9 | Implement save game functionality (JSON serialization) | feature | high | — |
| 10 | Implement load game functionality (JSON deserialization) | feature | high | — |
| 11 | Add city information popup (name, population, demand) | feature | medium | — |
| 12 | Add vehicle information popup (status, cargo, profit) | feature | medium | — |

**Deliverables:**
- Fully playable game with graphical interface
- Interactive city/vehicle/route management
- Working save/load system
- Camera navigation and game speed control

---

## Milestone 4 — Polish & Documentation
**Due: April 25, 2026 (Weeks 7–8)**

> Goal: Quality assurance, performance, comprehensive documentation, and final presentation.

| # | Task | Type | Priority | Assignee |
|---|------|------|----------|----------|
| 1 | Achieve ≥80% overall test coverage | testing | critical | — |
| 2 | Write missing unit tests for view/controller classes | testing | high | — |
| 3 | Perform and document performance testing | testing | medium | — |
| 4 | Fix all known bugs from issues tracker | bug | high | — |
| 5 | Complete JavaDoc for all public classes and methods | documentation | high | — |
| 6 | Write user manual (how to play the game) | documentation | high | — |
| 7 | Write installation / setup guide | documentation | medium | — |
| 8 | Create final UML diagrams (class + sequence) | documentation | high | — |
| 9 | Balance game economy (costs, revenues, difficulty) | feature | medium | — |
| 10 | Add visual polish (better colors, transitions, feedback) | feature | low | — |
| 11 | Prepare presentation slides | documentation | high | — |
| 12 | Final integration test and release build | testing | critical | — |

**Deliverables:**
- ≥80% test coverage with JaCoCo report
- Complete JavaDoc documentation
- User manual and developer guide
- Final UML diagrams (class diagram + sequence diagrams)
- Presentation-ready demo build
- Final release JAR artifact

---

## Summary Timeline

```
Week 1–2  ██████████░░░░░░░░░░░░░░  Milestone 1: Foundation
Week 3–4  ░░░░░░░░░░██████████░░░░  Milestone 2: Game Logic
Week 5–6  ░░░░░░░░░░░░░░░░░░██████  Milestone 3: UI
Week 7–8  ░░░░░░░░░░░░░░░░░░░░░░██  Milestone 4: Polish
```
