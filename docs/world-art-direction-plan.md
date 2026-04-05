# Citopia World Art Direction & Environment Plan

## Approach

Citopia should shift from a generic tile demo to a recognizable **historic Middle East desert transport world**.  
The world must read as: caravan-era routes, fortified cities, stone and mudbrick architecture, mountain passes, sparse vegetation, livestock movement, and market-road economies.

This plan converts that vision into asset production, map design rules, and milestone-ready implementation tasks for the current Java + LibGDX stack.

---

## Scope

- In:
  - Desert-first world style with minimal green areas
  - Distinct cities (multi-building clusters, fortifications, landmarks)
  - Hierarchical road network between cities and through mountain passes
  - Mountain, rock, cattle, oasis, and old-town visual identity
  - Playable map readability and gameplay-aware world layout
- Out:
  - Photorealistic rendering
  - Procedural civilization simulation beyond transport gameplay
  - Extra biomes not supporting the core fantasy (snow, jungle, modern city)

---

## Creative Pillars (Non-Negotiable)

1. **Desert Dominance**
   - 70–80% of traversable land should be sand, hardpan, gravel desert, and rocky scrub.
   - Green tiles are rare and purposeful (oasis, river-edge farming strips, palace gardens).

2. **Cities as Real Places**
   - A city is never a single icon: each city is a cluster of districts, houses, walls, gates, and landmarks.
   - Cities should visibly scale (small town, trade city, fortress capital).

3. **Roads Tell the Economy**
   - Roads are always visible, intentional structures: broad caravan routes + smaller branch paths.
   - Road intersections and city gates must feel like logistics hubs.

4. **Historic Regional Identity**
   - Visual language: mudbrick, sandstone, domes, towers, courtyards, bazaars, fort walls.
   - Include contextual props: date palms (limited), wells, tents, market carts, livestock pens.

5. **Readable Gameplay Over Detail Noise**
   - Every visual element should support transport decisions (where cities are, where roads go, where routes bottleneck).

---

## World Composition Blueprint

### Terrain Distribution Targets

- Sand / hard desert: **55%**
- Rocky desert / gravel / boulders: **20%**
- Mountain ranges / cliffs / impassable: **15%**
- Roads and paths footprint: **7%**
- Green/oasis/farmland strips: **3%**

### Macro Layout Rules

- Place 1–2 major mountain chains that create chokepoints.
- Ensure each city has at least 2 road exits (trade redundancy).
- Put high-value cities near crossroads, not map corners.
- Use passes to force strategic routes and longer alternatives.

---

## City Design System

### City Tiers

1. **Village Outpost**
   - 12–20 houses, 1 small market, 1 well, low wall fragments.
2. **Trade Town**
   - 25–45 houses, market square, caravanserai, full gate walls.
3. **Fortified City / Castle City**
   - 50+ structures, major citadel/castle, 2–4 gates, inner district layers.

### City Naming Roster (Middle Eastern & Desert Theme)
To bring the world to life, game cities will draw from historical caravan routes, desert outposts, and legendary trade hubs.

- **Tier 3 (Capitals & Fortresses):**
  - Damascus, Aleppo, Baghdad, Sana'a, Petra, Muscat, Isfahan, Samarkand.
- **Tier 2 (Trade Towns & Crossroads):**
  - Nizwa, Salalah, Aqaba, Palmyra (Tadmur), Basra, Aden, Merv, Tabuk.
- **Tier 1 (Oasis Villages & Outposts):**
  - Al-Waha (The Oasis), Bir Ali, Safita, Qamar, Ziz, Ibra, Khasab.

### Mandatory City Components

- Residential cluster sprites (multiple roof silhouettes)
- Market/core plaza tile group
- Defensive walls + gate modules
- Landmark structure (fort, keep, palace, large mosque-like dome silhouette)
- Connection points for incoming/outgoing roads

### Visual Readability Rules

- City footprint must be visible at normal zoom without opening UI.
- Distinguish city tiers by silhouette size and wall complexity.
- Keep district edges irregular to avoid artificial grid look.

---

## Road & Path Design System

### Road Hierarchy

- **Primary roads** (inter-city): wide, light stone-packed surface, milestone props.
- **Secondary roads** (regional): narrower dirt tracks.
- **Tertiary paths** (local): foot/cattle trails to nearby resources.

### Required Road Tiles

- Straight (N/S/E/W)
- Curves (all 4 corners)
- T-junction (4 variants)
- Crossroad
- City-gate connector
- Bridge/pass connector for mountain chokepoints

### Gameplay Constraints

- Roads should not be purely decorative: route planning uses actual path network.
- Visual width and quality should imply speed/cost differences later.

---

## Asset Production Backlog (Unique & Concrete)

## Atlas Group A — Ground & Terrain
- 18 sand variants (tone/noise variation)
- 10 rocky ground variants
- 8 cliff/mountain edge tiles
- 6 mountain mass tiles
- 4 oasis/water edge tiles
- 5 dry riverbed/wadi tiles

## Atlas Group B — Roads
- 24 road topology tiles (all directions + intersections)
- 8 gate and city-entry transition tiles
- 6 pass/bridge/stone crossing tiles

## Atlas Group C — City Building Modules
- 20 small house variants
- 14 medium house/shop variants
- 8 landmark structures (citadel, palace, large courtyard buildings)
- 12 wall and tower segments
- 6 gate modules

## Atlas Group D — Props & Life
- 10 market props (stalls, crates, fabric awnings)
- 8 livestock props (cattle, pens, water troughs)
- 10 environmental props (rocks, dead trees, palms, wells, tents)
- 6 road props (milestones, carts, signposts)

## Atlas Group E — Route Identity
- 8 route marker overlays (player readability)
- 6 city badge overlays (tier and demand hint)

---

## Implementation Mapping to Current Tech Stack

- Keep simulation entities in `core/src/main/java/com/citopia/model`.
- Expand map rules in `core/src/main/java/com/citopia/world`.
- Implement rendering layers and city composition in `core/src/main/java/com/citopia/view`.
- Register all atlas regions via `core/src/main/java/com/citopia/assets/AssetRegistry.java`.
- Store generated atlas files under `assets/atlas` and themed PNGs under `assets/citopiaassest/PNG`.

---

## Action Items (Atomic, Ordered)

- [ ] Define a final visual bible (palette, architecture motifs, silhouette examples).
- [ ] Create terrain tile set Group A and integrate into atlas packer flow.
- [ ] Implement road topology tiles Group B and render with map-layer priority.
- [ ] Build city module set Group C with tier-based composition rules.
- [ ] Add prop/life set Group D with spawn rules by biome and city tier.
- [ ] Add route/city overlays Group E for gameplay readability.
- [ ] Refactor map generator to enforce terrain distribution and mountain chokepoints.
- [ ] Implement city footprint generation so each city renders as district clusters.
- [ ] Hook road graph to route creation to avoid “icon-only city” behavior.
- [ ] Run visual pass and gameplay readability playtest; fix clutter and ambiguity.

---

## Milestone Alignment (April 2026 Reality)

### Immediate (Now → April 11, Milestone 3)
- Deliver vertical slice with:
  - one complete desert biome,
  - tiered city footprints,
  - functioning primary/secondary roads,
  - at least one fortress/castle city,
  - visible cattle + market props,
  - updated playable map renderer.

### Finalization (April 12 → April 25, Milestone 4)
- Add remaining tile variants and landmark diversity.
- Balance map readability vs. decoration density.
- Add tests for map generation constraints and road connectivity.
- Optimize draw order/culling and finalize documentation screenshots.

---

## Validation Checklist

- [ ] At default zoom, players can instantly distinguish terrain, roads, and city tiers.
- [ ] Every city has meaningful road connectivity and visible district scale.
- [ ] Mountains create route decisions, not random obstacles.
- [ ] Green terrain stays sparse and intentional (<5%).
- [ ] No route-critical object blends into background due to palette conflict.
- [ ] Frame time remains stable on desktop target while rendering dense city clusters.

---

## Risks & Mitigations

- **Risk: Over-decorated map hurts readability**  
  Mitigation: lock layer priorities and enforce minimum contrast for roads/cities.

- **Risk: Asset scope too large for deadline**  
  Mitigation: prioritize Groups A+B+C for Milestone 3; treat D+E expansion as Milestone 4 polish.

- **Risk: Cultural style becomes generic/incorrect**  
  Mitigation: create reference board and approve motifs before asset production.
