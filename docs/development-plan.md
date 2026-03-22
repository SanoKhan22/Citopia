# 🚀 Citopia Development Plan - How to Start Building

## Immediate Next Steps (This Week)

### 1. **Complete Milestone 1 Documentation** 
Create these critical documents with Egyptian theme:

```bash
# Create documentation structure
mkdir -p docs/{specifications,diagrams,mockups}
```

#### Required Documents:
- `docs/specifications/functional-requirements.md` - What the Egyptian transport game will do
- `docs/specifications/non-functional-requirements.md` - Performance, usability requirements  
- `docs/user-stories.md` - Player scenarios with Egyptian context
- `docs/diagrams/use-case.puml` - UML use-case diagram
- `docs/diagrams/class-diagram.puml` - Core classes and relationships
- `docs/mockups/` - UI mockup images (screens, menus, Egyptian styling)

### 2. **Team Role Assignment** (Critical for March 14 deadline)
Based on equal responsibility:

| Team Member | Primary Role | Responsibilities |
|-------------|--------------|------------------|
| **Ehsanullah** (@Sano) | **Game Engine Lead** | Pathfinding, vehicle movement, simulation loop |
| **Al Dabbas Tayma** (@Tayma) | **UI/Graphics Lead** | LibGDX rendering, Egyptian visual assets, user interface |
| **Samma Mustapha** (@Mustapha) | **Economic System Lead** | Cargo system, financial simulation, game balance |
| **Sultanalieva Aiperi** (@Alieva) | **Quality Assurance Lead** | Testing strategy, documentation, bug tracking |

---

## Technical Foundation (Week 1-2)

### Core Architecture Classes to Design:

```java
// Main game classes
com.citopia.game.GameEngine          // Simulation loop, time management
com.citopia.world.GameWorld          // Map, tiles, cities
com.citopia.world.City               // Egyptian cities with unique properties
com.citopia.transport.Vehicle        // Camels, feluccas, trucks, etc.
com.citopia.transport.Route          // Paths between cities
com.citopia.economy.CargoSystem      // Stone blocks, spices, gold, tourists
com.citopia.economy.FinanceManager   // Money, costs, revenue
com.citopia.pathfinding.Pathfinder  // A* algorithm for route finding
```

### LibGDX Project Structure:
```
core/src/main/java/com/citopia/
├── CitopiaGame.java                 // Main game entry point
├── screens/
│   ├── MenuScreen.java              // Main menu with Egyptian styling
│   ├── GameScreen.java              // Main gameplay view
│   └── LoadingScreen.java           // Asset loading
├── world/
│   ├── GameWorld.java               // Map representation
│   ├── Tile.java                    // Desert, river, oasis tiles
│   └── City.java                    // Egyptian cities
├── transport/
│   ├── Vehicle.java                 // Base vehicle class
│   ├── CaravanVehicle.java          // Camels, donkeys
│   ├── RiverVehicle.java            // Feluccas, barges
│   └── RouteManager.java            // Route planning
├── economy/
│   ├── Cargo.java                   // Goods system
│   ├── EgyptianCargo.java           // Themed cargo types
│   └── Economy.java                 // Financial simulation
├── graphics/
│   ├── MapRenderer.java             // Tile-based rendering
│   ├── VehicleRenderer.java         // Vehicle animation
│   └── UIRenderer.java              // Egyptian-themed UI
└── pathfinding/
    └── AStarPathfinder.java         // Route finding algorithm
```

---

## Development Workflow

### Week-by-Week Plan:

#### **Week 1 (March 6-12):** Documentation & Design
- [ ] Write functional requirements with Egyptian theme
- [ ] Create UI mockups with hieroglyphic elements
- [ ] Design class diagram focusing on Egyptian game mechanics
- [ ] Plan art asset requirements (desert tiles, vehicles, buildings)

#### **Week 2 (March 13-19):** Core Foundation
- [ ] Implement basic `GameWorld` with tile system
- [ ] Create `City` class with Egyptian city properties
- [ ] Build basic `Vehicle` hierarchy (camel, felucca, etc.)
- [ ] Set up rendering pipeline with LibGDX

#### **Week 3 (March 20-26):** Game Logic
- [ ] Implement A* pathfinding for desert/river routes
- [ ] Build vehicle movement system
- [ ] Create economic system with Egyptian cargo types
- [ ] Add game time progression

#### **Week 4 (March 27-April 2):** Integration
- [ ] Connect all systems together
- [ ] Add basic UI for vehicle purchase/route creation
- [ ] Implement save/load functionality
- [ ] Polish and bug fixing

---

## Getting Started Right Now

### 1. **Set up proper project structure:**

```bash
cd /home/sano/Desktop/Citopia
mkdir -p docs/{specifications,diagrams,mockups}
mkdir -p assets/{images,audio,fonts}
mkdir -p core/src/main/java/com/citopia/{world,transport,economy,graphics,pathfinding}
```

### 2. **Create team Kanban board** (GitLab issues)
- Use GitLab Issues to track tasks
- Create labels: `documentation`, `feature`, `bug`, `egyptian-theme`
- Assign team members to specific tasks

### 3. **Art Asset Pipeline**
- Use **free Egyptian-themed assets** from OpenGameArt.org
- Create consistent **color palette**: #D4AF37 (gold), #008080 (teal), #CD853F (sandy brown)
- **Pixel art style** for retro feel (16x16 or 32x32 tiles)

### 4. **Technical Setup**
- Ensure all team members have **Java 17** and **Gradle** installed
- Set up **shared development environment**
- Configure **GitLab CI** for automated building

---

## Success Criteria

By **March 14** (Milestone 1 deadline):
- ✅ Complete Egyptian theme specification
- ✅ Functional requirements document
- ✅ UML diagrams (use case, class, component)
- ✅ UI mockups with Egyptian styling
- ✅ Team roles assigned
- ✅ Development roadmap finalized

By **March 28** (Milestone 2):
- ✅ Working Egyptian map with Nile river and desert
- ✅ Camel caravans moving between oasis cities
- ✅ Basic economic system with spices/gold cargo
- ✅ Pathfinding through desert terrain

Would you like me to help create any specific document first, or set up the development environment?