package com.citopia.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
<<<<<<< HEAD
import com.badlogic.gdx.graphics.Texture;
import com.citopia.model.City;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
=======
import com.citopia.model.City;

import java.util.List;
>>>>>>> gitlab/feature/18-route-creation

/**
 * Draws city markers and labels on the map.
 */
public class CityRenderer {

    private static final float CITY_RADIUS = 10f;
<<<<<<< HEAD
    private static final float CITY_ICON_SIZE = 32f;
=======
>>>>>>> gitlab/feature/18-route-creation

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final SpriteBatch worldBatch;
    private final SpriteBatch overlayBatch;
<<<<<<< HEAD
    private final Map<String, Texture> cityTextures;
=======
>>>>>>> gitlab/feature/18-route-creation

    public CityRenderer() {
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.worldBatch = new SpriteBatch();
        this.overlayBatch = new SpriteBatch();
<<<<<<< HEAD
        this.cityTextures = new HashMap<>();
        // Load city icons with error logging
        loadCityIcon("Jerusalem");
        loadCityIcon("Tehran");
        loadCityIcon("Cairo");
        loadCityIcon("Baghdad");
        loadCityIcon("Damascus");
        loadCityIcon("Riyadh");
        loadCityIcon("Doha");
        loadCityIcon("Muscat");
        loadCityIcon("Jeddah");
    }

    private void loadCityIcon(String cityName) {
        String path = "images/cities/" + cityName.toLowerCase() + ".png";
        try {
            Texture tex = new Texture(path);
            cityTextures.put(cityName, tex);
            System.out.println("Loaded icon for " + cityName + " from " + path);
        } catch (Exception e) {
            System.out.println("Failed to load icon for " + cityName + " from " + path + ": " + e.getMessage());
        }
=======
>>>>>>> gitlab/feature/18-route-creation
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
<<<<<<< HEAD
        for (Texture t : cityTextures.values()) {
            t.dispose();
        }
    }

    private void renderMarkers(List<City> cities, City selectedCity, OrthographicCamera camera) {
        // Draw city icons first
        worldBatch.setProjectionMatrix(camera.combined);
        worldBatch.begin();
        for (City city : cities) {
            Texture icon = cityTextures.get(city.getName());
            if (icon != null) {
                float drawX = city.getX() - CITY_ICON_SIZE / 2f;
                float drawY = city.getY() - CITY_ICON_SIZE / 2f;
                worldBatch.draw(icon, drawX, drawY, CITY_ICON_SIZE, CITY_ICON_SIZE);
            }
        }
        worldBatch.end();
        // Draw fallback circles for cities with no icon
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (City city : cities) {
            if (!cityTextures.containsKey(city.getName())) {
                shapeRenderer.setColor(city.equals(selectedCity) ? Color.GOLD : Color.DARK_GRAY);
                shapeRenderer.circle(city.getX(), city.getY(), CITY_RADIUS);
            }
        }
=======
    }

    private void renderMarkers(List<City> cities, City selectedCity, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (City city : cities) {
            shapeRenderer.setColor(city.equals(selectedCity) ? Color.GOLD : Color.DARK_GRAY);
            shapeRenderer.circle(city.getX(), city.getY(), CITY_RADIUS);
        }

>>>>>>> gitlab/feature/18-route-creation
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
