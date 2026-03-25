package com.citopia.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.citopia.map.TileMap;
import com.citopia.map.TileType;

/**
 * Draws a tile map using simple colored rectangles.
 */
public class TileMapRenderer {

    public static final float TILE_SIZE = 64f;

    private final ShapeRenderer shapeRenderer;

    public TileMapRenderer() {
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(TileMap tileMap, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int y = 0; y < tileMap.getHeight(); y++) {
            for (int x = 0; x < tileMap.getWidth(); x++) {
                shapeRenderer.setColor(getTileColor(tileMap.getTile(x, y).getType()));
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        shapeRenderer.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.15f));

        for (int y = 0; y < tileMap.getHeight(); y++) {
            for (int x = 0; x < tileMap.getWidth(); x++) {
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

    private Color getTileColor(TileType tileType) {
        return switch (tileType) {
            case SAND -> new Color(0.86f, 0.76f, 0.52f, 1f);
            case WATER -> new Color(0.24f, 0.50f, 0.82f, 1f);
            case DUNE -> new Color(0.72f, 0.58f, 0.33f, 1f);
            case OASIS -> new Color(0.31f, 0.66f, 0.42f, 1f);
        };
    }
}
