# AI Image Generation Guide: Citopia Assets

To bring our historic Middle East desert transport world to life, we need a cohesive set of assets. Since Citopia uses a 2D tile-based top-down perspective, the images you generate need to follow strict formatting rules to ensure they align properly on a grid and don't clash stylistically.

## 🛠️ Core Rules for Image Generation

1. **Perspective:** Always specify **"Top-down orthographic 2D game asset"** so the camera looks straight down or at a slight flat angle, matching your current map renderer.
2. **Lighting:** Demand **"flat lighting, no cast shadows"** so you can apply your own shadows, or ensure shadows all fall in the exact same direction (e.g., "shadows falling bottom-right").
3. **Backgrounds:** For objects/buildings, request a **"solid white background"** or **"solid neutral gray background"** so you can easily remove it later in Photoshop, GIMP, or an online background remover.
4. **Seamlessness:** For ground tiles (sand, rock), request **"seamless repeating texture"** so they tile perfectly when placed next to each other on the grid.
5. **Art Style:** Pick one and stick to it in every prompt. I recommend **"hand-painted digital art style, crisp edges, similar to classic 2D strategy games"** or **"clean 2D pixel art style"**.

---


## 🎨 Asset Generation Prompts

Copy and paste these exact prompts into Midjourney, DALL-E 3, or Stable Diffusion. 

### Group A: The Ground (Seamless Land Tiles)
To fill your massive 100x100 map without looking repetative, you need highly specific, non-distorted generation prompts. 

**Pro-Tip for Land Generation:** AI often sneaks 3D perspective into top-down prompts. Using words like "albedo map", "flat lay", and "exactly 90-degree camera" forces it to behave. All of these *must* be seamless squares.

*   **Deep Desert Dunes (Infertile - Primary Filler):**
    > **Prompt:** A perfect seamless repeating square texture of dry wind-blown desert sand dunes, exactly 90-degree top-down camera angle, NO perspective distortion, flat uniform lighting, zero cast shadows, hand-painted digital art style albedo map, warm ochre and terracotta yellow palette, smooth soft curves of sand, very subtle texture.

*   **Cracked Hardpan Mud (Infertile - Drought Zones):**
    > **Prompt:** A perfect seamless repeating square texture of baked, cracked hardpan clay and dirt, exactly 90-degree top-down camera angle, NO perspective distortion, flat uniform lighting, zero cast shadows, hand-painted digital art style albedo map, polygonal mud cracks, pale dusty brown and beige colors, dry and barren wasteland.

*   **Rocky Scrubland (Infertile Transition - Edge of Mountains):**
    > **Prompt:** A perfect seamless repeating square texture of coarse gravel, small scattered sandstone pebbles, and very sparse dry brown scrub brush, exactly 90-degree top-down camera angle, NO perspective distortion, flat uniform lighting, hand-painted digital art style albedo map, gritty terrain, earthy rust and brown tones.

*   **Lush Oasis Soil (Fertile - Rare River Edges):**
    > **Prompt:** A perfect seamless repeating square texture of dark fertile riverbank soil mixed with patches of vibrant green moss and short grass, exactly 90-degree top-down camera angle, NO perspective distortion, flat uniform lighting, hand-painted digital art style albedo map, rich damp brown dirt contrasting with bright green vegetation patches.

*   **Dry Riverbed / Wadi (Transition - Pathing Route):**
    > **Prompt:** A perfect seamless repeating square texture of a dried-up riverbed, smooth water-worn pebbles and pale sand, exactly 90-degree top-down camera angle, NO perspective distortion, flat uniform lighting, hand-painted digital art style albedo map, chalky white and pale gray stone colors with sandy undertones.

*   **
:**
    > **Prompt:** A perfect seamless repeating square texture of dense, jagged sandstone rock formations and heavy boulders packed tightly together, exactly 90-degree top-down camera angle, NO perspective distortion, flat uniform lighting, hand-painted digital art style albedo map, deep canyon red and orange rock colors, impassable rough terrain.

### Group B: The Roads (Overlays)
Roads will be drawn over the sand. You want straight lines, curves, and intersections.
*   **Dirt/Caravan Road (Straight N/S):**
    > **Prompt:** Top-down orthographic 2D game asset of a wide, packed-dirt caravan road running perfectly vertically from top to bottom, faded wheel ruts, faded footprints, placed on a transparent or solid white background, hand-painted digital art style, flat lighting.

### Group C: Citopia Architecture (Buildings & Walls)
These will physically build your cities. They need to look like historic structures.

*   **Basic Mudbrick House:**
    > **Prompt:** Top-down orthographic 2D game sprite of a small, historic Middle Eastern mudbrick house, flat roof with a low parapet, wooden support beams slightly protruding, small rooftop awning, hand-painted digital art style, solid white background, flat orthographic projection, no cast shadow.
*   **Market Bazaar / Trade Tent:**
    > **Prompt:** Top-down orthographic 2D game sprite of an ancient desert market stall, colorful striped fabric awning stretching over merchants goods, pottery and woven rugs beneath, hand-painted digital art style, solid white background, flat orthographic projection.
*   **Fortress Citadel (Landmark):**
    > **Prompt:** Top-down orthographic 2D game sprite of a large historic desert fortress keep, thick sandstone walls, defensive battlements along the top edges, central courtyard, hand-painted digital art style, solid white background, flat orthographic projection.
*   **Defensive City Wall (Straight Segment):**
    > **Prompt:** Top-down orthographic 2D game sprite of a straight segment of thick ancient mudbrick defensive wall with a walkway on top, hand-painted digital art style, solid white background, flat orthographic projection.

### Group D: Props & Environment (Scenery)
These make the map feel alive.
*   **Scattered Boulders:**
    > **Prompt:** Top-down orthographic 2D game sprite sheet of 5 different sized sandstone boulders and rocks, hand-painted digital art style, solid white background, flat lighting.
*   **Livestock / Cattle:**
    > **Prompt:** Top-down orthographic 2D game sprite of a resting camel and a small pack mule, viewed from directly above, hand-painted digital art style, solid white background, flat orthographic projection.
*   **Date Palms / Oasis Trees:**
    > **Prompt:** Top-down orthographic 2D game sprite of a cluster of lush green date palm trees viewed from directly above, radial fronds, isolated on a solid white background, hand-painted digital art style, crisp edges.

---

## ⚙️ How to Process & Use Them in Citopia

Once you download the images from the AI generator, you must prepare them for LibGDX:

1.  **Format:** Convert all generated images into `.png` format.
2.  **Remove Backgrounds:** For buildings, props, and roads, you must remove the solid white background. You want the background checkerboard (transparent).
3.  **Resize / Standardize Square Bounds:**
    *   Open them in an image editor.
    *   **Crucial for 500x500 or 1024x1024 AI outputs:** AI generators usually spit out large images (e.g., 500x500 or 1024x1024). You **must** scale these down before putting them in your game. Large files will quickly fill up your `game-assets.atlas` max size (usually 2048x2048) and cause rendering performance issues.
    *   Shrink ground tiles to a "Power of Two" size like `128x128` or `64x64` pixels.
    *   For houses and trees, fit them tightly inside a square canvas with a transparent background, then resize to match the scale of your tiles (e.g., `128x128`).
4.  **Save to Assets:** Drop these `.png` files into `assets/citopiaassest/PNG/`.
5.  **Re-Pack Atlas:** Run your Gradle tool `./gradlew desktop:packAssets` to bundle these new pngs into `game-assets.atlas` which the `GameScreen.java` will read.