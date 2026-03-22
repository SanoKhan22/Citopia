# 📜 Citopia: Egyptian Transport Empire - Functional Requirements

## 1. **Game Overview**

**Citopia** is a 2D tile-based transport simulation game set in ancient and modern Egypt. Players build and manage transport networks connecting Egyptian cities along the Nile River and across desert terrain, transporting traditional and modern Egyptian goods while managing finances.

---

## 2. **Core Game Features**

### 2.1 **Egyptian Game World**
- **Requirement ID:** FR-001
- **Description:** The game world represents Egypt with authentic geographical features
- **Details:**
  - Nile River running north-south as primary transport artery
  - Desert terrain covering majority of map (movement penalty for non-camel vehicles)
  - Oases scattered throughout desert (natural trading posts)
  - Egyptian cities: Cairo, Alexandria, Thebes, Aswan, Memphis, Siwa
  - Terrain types: Desert sand, Nile river, fertile farmland, rocky mountains
  - Landmarks: Pyramids, temples, sphinx as visual markers

### 2.2 **Egyptian Vehicle System**
- **Requirement ID:** FR-002  
- **Description:** Authentic Egyptian-themed vehicles with unique characteristics
- **Vehicle Types:**
  - **Donkey Caravans** - Cheap, slow, basic desert transport
  - **Camel Trains** - Medium speed, high desert efficiency, no movement penalty in sand
  - **Felucca Boats** - Traditional Nile sailing boats, wind-dependent speed
  - **Cargo Barges** - Heavy freight transport on Nile River
  - **Horse Carts** - Fast on roads, expensive maintenance
  - **Modern Trucks** - High capacity, road-dependent (late game)

### 2.3 **Egyptian Cargo & Economy**
- **Requirement ID:** FR-003
- **Description:** Economic simulation based on traditional and modern Egyptian goods
- **Cargo Types:**
  - **Stone Blocks** - For pyramid/temple construction (high weight, short distance)
  - **Papyrus** - High value, lightweight, long-distance trade
  - **Gold** - Mined in southern regions, extremely high value
  - **Spices** - Cinnamon, cardamom, moderate value
  - **Dates & Figs** - Oasis agriculture, perishable goods
  - **Cotton** - Nile Delta farming, textile export
  - **Tourists** - Passenger transport to historical sites
  - **Archaeological Artifacts** - Special museum deliveries

### 2.4 **Route Planning & Pathfinding**
- **Requirement ID:** FR-004
- **Description:** A* pathfinding algorithm considering Egyptian terrain
- **Requirements:**
  - Automatic route calculation between cities
  - Terrain-aware pathfinding (desert penalty, river bonus)
  - Player-defined waypoints for custom routes
  - Route efficiency display (time, cost, distance)
  - Multiple route options with trade-offs

### 2.5 **Financial Management**
- **Requirement ID:** FR-005
- **Description:** Economic system with Egyptian-themed elements
- **Features:**
  - Vehicle purchase costs (camels cheaper than trucks)
  - Route maintenance costs (desert routes more expensive)
  - Cargo delivery revenue based on distance and goods type
  - Seasonal modifiers (Nile flood season affects river transport)
  - Random events (sandstorms, archaeological discoveries)

---

## 3. **Egyptian-Specific Game Mechanics**

### 3.1 **Pharaoh's Contract System**
- **Requirement ID:** FR-006
- **Description:** Government contracts offering special objectives
- **Features:**
  - Pyramid construction projects requiring massive stone deliveries
  - Temple renovation contracts
  - Tourist season passenger quotas
  - Time-limited high-reward missions

### 3.2 **Seasonal Events**
- **Requirement ID:** FR-007
- **Description:** Egyptian climate and cultural events affecting gameplay
- **Events:**
  - **Nile Flood Season** - River transport capacity increases
  - **Sandstorm Events** - Desert routes temporarily blocked
  - **Tourist Season** - Increased passenger demand to pyramids/temples
  - **Harvest Festival** - Agricultural cargo bonus from fertile regions

### 3.3 **Historical Progression**
- **Requirement ID:** FR-008
- **Description:** Technology progression from ancient to modern Egypt
- **Progression Stages:**
  - **Ancient Period** - Camels, feluccas, basic desert routes
  - **Classical Period** - Improved roads, larger boats
  - **Modern Period** - Trucks, railways, container ships
  - **Contemporary** - Air transport, oil extraction, solar infrastructure

---

## 4. **User Interface Requirements**

### 4.1 **Egyptian Visual Theme**
- **Requirement ID:** FR-009
- **Description:** Authentic Egyptian visual design throughout UI
- **Visual Elements:**
  - Hieroglyphic-inspired icons and buttons
  - Egyptian color palette: Gold (#D4AF37), Teal (#008080), Sandy Brown (#CD853F)
  - Papyrus-style background textures
  - Egyptian architectural elements in UI panels

### 4.2 **Game Screens**
- **Requirement ID:** FR-010
- **Description:** Complete set of game screens with Egyptian styling
- **Required Screens:**
  - Main Menu with pyramid silhouettes
  - World Map showing Egyptian geography
  - Vehicle Purchase Dialog (marketplace theme)
  - Route Planning Interface
  - Financial Summary Dashboard
  - Pharaoh's Contract Board

---

## 5. **Technical Requirements**

### 5.1 **Performance**
- **Requirement ID:** NFR-001
- **Description:** Game performance standards
- **Requirements:**
  - 60 FPS on medium-spec hardware
  - <3 second loading times
  - Smooth vehicle animation
  - Real-time pathfinding calculations

### 5.2 **Save/Load System**  
- **Requirement ID:** FR-011
- **Description:** Game state persistence
- **Features:**
  - Save current game progress
  - Load previous game sessions
  - Multiple save slots
  - Auto-save functionality

### 5.3 **Platform Compatibility**
- **Requirement ID:** NFR-002
- **Description:** Cross-platform deployment
- **Targets:**
  - Windows Desktop
  - Linux Desktop  
  - MacOS Desktop
  - (Future: Android/Web deployment)

---

## 6. **Success Criteria**

1. **Core Gameplay Loop:** Player can purchase vehicles → create routes → transport cargo → earn money → expand network
2. **Egyptian Authenticity:** Game feels authentically Egyptian through visuals, cargo types, and cultural elements
3. **Economic Balance:** Transport routes are profitable but require strategic planning
4. **User Experience:** Intuitive interface allowing both casual and strategic play
5. **Technical Stability:** Game runs smoothly without crashes or performance issues

---

## 7. **Future Expansion Ideas**

- **Multiplayer Mode** - Multiple pharaohs competing for trade dominance
- **Campaign Mode** - Historical scenarios (Building pyramids, Suez Canal construction)
- **Mod Support** - Community-created Egyptian scenarios and assets
- **VR Mode** - First-person camel caravan experience