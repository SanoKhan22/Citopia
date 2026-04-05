# Citopia Minimap Architectural Design

Based on 2D game principles and clean architecture constraints, here is the technical plan for implementing an efficient, real-time updatable minimap in Citopia.

## Minimap Core Functions
1. **Orientation & Navigation:** Show the player's current camera viewport relative to the vast 512x512 tile world.
2. **Spatial Awareness:** Highlight points of interest (the central 40x40 City Zone, oases, player vehicles).
3. **Dynamic Updates:** When you build a new city, road, or entity in the future, the minimap must update to reflect that change without causing lag.

---

## Proposed Technical Solutions (LibGDX)

There are three ways to build a minimap in LibGDX. I highly recommend **Method 1** for this game.

### Method 1: The `Pixmap` Texture Approach (Highly Recommended)
We create a 2D image array in RAM (`Pixmap`) where **1 Pixel = 1 Map Tile**.
- **How it works:** We loop through the 512x512 `TileMap` once on startup, plotting colors (Yellow for sand, Green for trees, Gray for cities) onto a 512x512 pixel image. We upload this to the GPU as a `Texture` and draw it on the HUD.
- **Handling Future Updates:** When you "build" a new city or wall, we simply invoke `pixmap.setColor()` and `pixmap.drawPixel(x, y)`, then update the GPU texture. It is lightning fast ($O(1)$) and avoids re-rendering the whole map.
- **Dependencies needed:** **None**. LibGDX has `Pixmap` and `Texture` rendering built-in.

### Method 2: The `FrameBuffer` (FBO) & Second Camera Approach
- **How it works:** We create a second `OrthographicCamera` zoomed out to cover the whole map map. We render the actual game sprites to an invisible screen (FBO), then plaster that screen onto the UI.
- **Trade-offs:** Visually identical to the game world, but rendering $512 \times 512 = 262,144$ tiles entirely tanks the framerate. Not recommended for strategy/transport games with massive maps.

### Method 3: `ShapeRenderer` Overlay
- **How it works:** We use LibGDX's `ShapeRenderer` to draw solid UI rectangles for every object on the screen.
- **Trade-offs:** Good for showing dynamic vehicles/carts, but drawing 262,144 rectangles for the ground layer causes severe performance drops.

---

## Architectural Implementation Plan (Method 1)

If we proceed with the `Pixmap` method, we will add the following to `GameScreen.java` or extract it into a dedicated `MinimapRenderer.java` class:

### 1. Minimap Generation (Initialization)
```java
// 1 pixel per tile
Pixmap mapPixmap = new Pixmap(tileMap.width(), tileMap.height(), Pixmap.Format.RGBA8888);

// Loop once on load
for(int x=0; x < tileMap.width(); x++) {
    for(int y=0; y < tileMap.height(); y++) {
        if(isInhabitantZone(x, y)) mapPixmap.setColor(Color.DARK_GRAY);
        else if(hasTree(x, y)) mapPixmap.setColor(Color.FOREST);
        else if(isRoadTile(x, y)) mapPixmap.setColor(Color.SCARLET);
        else mapPixmap.setColor(Color.GOLD); // Desert sand
        
        // LibGDX Pixmap origin is top-left, map origin is bottom-left, so we invert Y
        mapPixmap.drawPixel(x, tileMap.height() - 1 - y);
    }
}
Texture minimapTexture = new Texture(mapPixmap);
```

### 2. HUD Drawing (Every Frame)
We draw the `minimapTexture` to a fixed corner of the UI screen. Next, we calculate the main camera's exact bounding box, convert pixel coordinates back to tile map coordinates, and draw an empty white rectangle over the minimap texture to represent **"What the player is currently looking at"**.

### 3. Dynamic Future Registration (Event-Driven Updates)
To fulfill your requirement: *"in future if I obtain something, it should be updated in the minimapters"*

We will implement an Observer Pattern (as outlined in `architect-review.md`):
When a city building is added in `TileMap` data:
1. `TileMap` fires an `OnTileChangedEvent`.
2. `MinimapRenderer` catches the event.
3. It overwrites that exact pixel on the `Pixmap` and pushes the updated texture memory to the GPU via `minimapTexture.draw(mapPixmap, 0, 0)`.

## Summary
You do **not** need external frameworks or downloads to make an industry-standard 2D minimap! LibGDX's core API is more than powerful enough. 

Let me know if you approve of the **Pixmap approach**, and I will write the code directly into your game right now!