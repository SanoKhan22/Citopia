package com.citopia.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.citopia.model.City;

import java.util.List;

/**
 * Draws city markers and labels on the map.
 */
public class CityRenderer {

    private static final float CITY_RADIUS = 10f;

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final SpriteBatch worldBatch;
    private final SpriteBatch overlayBatch;

    public CityRenderer() {
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.worldBatch = new SpriteBatch();
        this.overlayBatch = new SpriteBatch();
    }

    public void render(List<City> cities, City selectedCity, OrthographicCamera camera) {
        renderMarkers(cities, selectedCity, camera);
        renderLabels(cities, camera);
    }

    public void renderOverlay(City selectedCity) {
        if (selectedCity == null) {
            return;
        }

        overlayBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(overlayBatch, "Selected City", 16f, 80f);
        font.draw(overlayBatch, "Name: " + selectedCity.getName(), 16f, 56f);
        font.draw(overlayBatch, "Population: " + selectedCity.getPopulation(), 16f, 32f);
        overlayBatch.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        worldBatch.dispose();
        overlayBatch.dispose();
    }

    private void renderMarkers(List<City> cities, City selectedCity, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (City city : cities) {
            shapeRenderer.setColor(city.equals(selectedCity) ? Color.GOLD : Color.DARK_GRAY);
            shapeRenderer.circle(city.getX(), city.getY(), CITY_RADIUS);
        }

        shapeRenderer.end();
    }

    private void renderLabels(List<City> cities, OrthographicCamera camera) {
        worldBatch.setProjectionMatrix(camera.combined);
        worldBatch.begin();
        font.setColor(Color.BLACK);

        for (City city : cities) {
            font.draw(worldBatch, city.getName(), city.getX() + 12f, city.getY() + 28f);
        }

        worldBatch.end();
    }
}
