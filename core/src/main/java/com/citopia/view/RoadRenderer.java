package com.citopia.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.citopia.world.MapConfig;
import com.citopia.world.TileMap;

/**
 * Auto-tiling road renderer using a single horizontal-road texture.
 *
 * <p>Neighbour bitmask (bits): North=8, East=4, South=2, West=1
 * All 16 possible combinations are mapped to a rotation + optional flip
 * of the single base horizontal-road sprite.
 *
 * <p>Draw size is slightly larger than one tile so roads look chunky.
 */
public final class RoadRenderer {

    /** Draw roads at 125% of tile size so they're easier to see. */
    private static final float ROAD_SCALE = 1.25f;

    /** Bitmask constants for readability. */
    private static final int N = 8, E = 4, S = 2, W = 1;

    private final TextureRegion roadRegion;
    private final TileMap tileMap;
    private final float tileSize;
    private final float drawSize;
    private final float offset; // to center oversized draw

    public RoadRenderer(TextureRegion roadRegion, TileMap tileMap) {
        this.roadRegion = roadRegion;
        this.tileMap    = tileMap;
        this.tileSize    = MapConfig.TILE_DRAW_SIZE;
        this.drawSize    = tileSize * ROAD_SCALE;
        this.offset      = (drawSize - tileSize) / 2f;
    }

    /**
     * Draw all road tiles visible in the given tile range.
     * Caller must have already called batch.begin() and set projection matrix.
     */
    public void drawRoads(SpriteBatch batch, int x0, int x1, int y0, int y1) {
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                if (!tileMap.hasRoad(x, y)) continue;
                drawRoadTile(batch, x, y);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void drawRoadTile(SpriteBatch batch, int tx, int ty) {
        int mask = neighborMask(tx, ty);

        // Pixel position (bottom-left of tile, adjusted for oversized draw)
        float px = tx * tileSize - offset;
        float py = ty * tileSize - offset;
        float cx = drawSize / 2f;  // origin for rotation (center of sprite)
        float cy = drawSize / 2f;

        // Determine rotation and whether to use the "cross" or "T" approach.
        // For complex cases we draw two layers (H + V) at 50% alpha each.
        switch (mask) {
            // ── Straight roads ───────────────────────────────────────
            case E | W:             // ─── horizontal
            case W: case E:
                drawRotated(batch, px, py, cx, cy, 0f, false, false);
                break;

            case N | S:             // │ vertical
            case N: case S:
                drawRotated(batch, px, py, cx, cy, 90f, false, false);
                break;

            // ── Dead ends (single connection) — drawn as short stubs ─
            // Reuse straight sprites; they look ok at small zoom
            case 0:                 // isolated — draw as a tiny cross
                drawRotated(batch, px, py, cx, cy, 0f,  false, false);
                drawRotated(batch, px, py, cx, cy, 90f, false, false);
                break;

            // ── Corners ──────────────────────────────────────────────
            case N | E:             // └ bottom-left corner (N + E open)
                drawCorner(batch, px, py, cx, cy, 0f);
                break;
            case N | W:             // ┘ bottom-right corner (N + W open)
                drawCorner(batch, px, py, cx, cy, 90f);
                break;
            case S | E:             // ┌ top-left corner (S + E open)
                drawCorner(batch, px, py, cx, cy, -90f);
                break;
            case S | W:             // ┐ top-right corner (S + W open)
                drawCorner(batch, px, py, cx, cy, 180f);
                break;

            // ── T-junctions ──────────────────────────────────────────
            case N | E | W:         // ┴ T open south missing
                drawRotated(batch, px, py, cx, cy, 0f, false, false);  // H base
                drawHalf(batch, px, py, cx, cy, 90f, true);            // N stub
                break;
            case S | E | W:         // ┬ T open north missing
                drawRotated(batch, px, py, cx, cy, 0f, false, false);
                drawHalf(batch, px, py, cx, cy, 90f, false);           // S stub
                break;
            case N | S | E:         // ├ T open west missing
                drawRotated(batch, px, py, cx, cy, 90f, false, false); // V base
                drawHalf(batch, px, py, cx, cy, 0f, true);             // E stub
                break;
            case N | S | W:         // ┤ T open east missing
                drawRotated(batch, px, py, cx, cy, 90f, false, false);
                drawHalf(batch, px, py, cx, cy, 0f, false);            // W stub
                break;

            // ── Cross ────────────────────────────────────────────────
            case N | E | S | W:     // ┼ full cross
                drawRotated(batch, px, py, cx, cy, 0f, false, false);
                drawRotated(batch, px, py, cx, cy, 90f, false, false);
                break;

            default:
                // Fallback: just draw as horizontal
                drawRotated(batch, px, py, cx, cy, 0f, false, false);
                break;
        }
    }

    /**
     * Bitmask of which of the 4 neighbours also have roads.
     * North=8 East=4 South=2 West=1.
     */
    private int neighborMask(int x, int y) {
        int mask = 0;
        if (tileMap.hasRoad(x, y + 1)) mask |= N;
        if (tileMap.hasRoad(x + 1, y)) mask |= E;
        if (tileMap.hasRoad(x, y - 1)) mask |= S;
        if (tileMap.hasRoad(x - 1, y)) mask |= W;
        return mask;
    }

    // ── Draw primitives ───────────────────────────────────────────────────────

    /** Draw the full road texture rotated around its centre. */
    private void drawRotated(SpriteBatch batch, float px, float py,
                             float cx, float cy, float degrees,
                             boolean flipX, boolean flipY) {
        batch.draw(
            roadRegion,
            px, py,
            cx, cy,
            drawSize, drawSize,
            1f, 1f,
            degrees
        );
    }

    /**
     * Draw a corner: two horizontal half-roads at 90° to each other.
     * We approximate this by drawing the full horizontal sprite at reduced
     * alpha as a stub — good enough at typical zoom levels.
     */
    private void drawCorner(SpriteBatch batch, float px, float py,
                            float cx, float cy, float degrees) {
        // Draw horizontal component
        drawRotated(batch, px, py, cx, cy, degrees, false, false);
        // Draw vertical component (rotated 90° from the corner base)
        drawRotated(batch, px, py, cx, cy, degrees + 90f, false, false);
    }

    /**
     * Draw a half-length stub extending either "forward" (positive) or
     * "backward" (negative) from centre in the given direction.
     * Used to build T-junctions from a base straight road.
     *
     * @param forward true = extend toward positive axis (N or E), false = S or W
     */
    private void drawHalf(SpriteBatch batch, float px, float py,
                          float cx, float cy, float baseRot, boolean forward) {
        // Just draw the same full road at 0.6 opacity for the stub arm.
        // This is a visual approximation — the seam is hidden by the overlapping base.
        batch.setColor(1f, 1f, 1f, 0.9f);
        drawRotated(batch, px, py, cx, cy, baseRot + (forward ? 0f : 180f), false, false);
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
