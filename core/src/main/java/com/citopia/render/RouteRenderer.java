package com.citopia.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.citopia.map.GridPoint;
import com.citopia.model.Route;

import java.util.List;

/**
 * Draws route overlays on top of the map.
 */
public class RouteRenderer {

    private final ShapeRenderer shapeRenderer;

    public RouteRenderer() {
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(Route route, OrthographicCamera camera) {
        if (route == null) {
            return;
        }

        List<GridPoint> tilePath = route.getTilePath();
        if (tilePath.size() < 2) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.86f, 0.15f, 0.15f, 1f));

        for (int i = 0; i < tilePath.size() - 1; i++) {
            GridPoint a = tilePath.get(i);
            GridPoint b = tilePath.get(i + 1);

            float ax = a.x() * TileMapRenderer.TILE_SIZE + TileMapRenderer.TILE_SIZE / 2f;
            float ay = a.y() * TileMapRenderer.TILE_SIZE + TileMapRenderer.TILE_SIZE / 2f;
            float bx = b.x() * TileMapRenderer.TILE_SIZE + TileMapRenderer.TILE_SIZE / 2f;
            float by = b.y() * TileMapRenderer.TILE_SIZE + TileMapRenderer.TILE_SIZE / 2f;

            shapeRenderer.line(ax, ay, bx, by);
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
