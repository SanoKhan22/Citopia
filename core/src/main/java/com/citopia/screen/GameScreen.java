package com.citopia.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.citopia.economy.CompanyFinance;
import com.citopia.map.TileMap;
import com.citopia.map.TileType;
import com.citopia.model.City;
import com.citopia.model.CityPlacementValidator;
import com.citopia.model.Route;
import com.citopia.pathfinding.AStarPathfinder;
import com.citopia.render.CityRenderer;
import com.citopia.render.RouteRenderer;
import com.citopia.render.TileMapRenderer;
import com.citopia.transport.RoutePlanner;
import com.citopia.transport.SimulationEngine;
import com.citopia.transport.TransportTrip;
import com.citopia.transport.VehicleShop;
import com.citopia.transport.VehicleType;

import java.util.List;

/**
 * Displays the main game map with camera navigation.
 */
public class GameScreen extends ScreenAdapter {

    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 2.5f;
    private static final float PAN_SPEED = 600f;
    private static final int MAP_SCALE = 2;
    private static final int BASE_MAP_WIDTH = 20;
    private static final int BASE_MAP_HEIGHT = 15;

    private final OrthographicCamera camera;
    private final ScreenViewport viewport;
    private final TileMap tileMap;
    private final TileMapRenderer tileMapRenderer;
    private final CityRenderer cityRenderer;
    private final RouteRenderer routeRenderer;
    private final List<City> cities;
    private final Route demoRoute;
    private final CompanyFinance companyFinance;
    private final VehicleShop vehicleShop;
    private final SimulationEngine simulationEngine;
    private final SpriteBatch overlayBatch;
    private final BitmapFont overlayFont;
    private final ShapeRenderer hudPanelRenderer;
    private String statusMessage;
    private City selectedCity;

    public GameScreen() {
        this.camera = new OrthographicCamera();
        this.viewport = new ScreenViewport(camera);
        this.tileMap = createDemoMap();
        this.tileMapRenderer = new TileMapRenderer();
        this.cityRenderer = new CityRenderer();
        this.routeRenderer = new RouteRenderer();
        this.cities = createCities(tileMap);
        this.demoRoute = new RoutePlanner(new AStarPathfinder()).planRoute(tileMap, cities.get(0), cities.get(1));
        this.companyFinance = new CompanyFinance(1500);
        this.vehicleShop = new VehicleShop(companyFinance);
        this.simulationEngine = new SimulationEngine(companyFinance);
        this.overlayBatch = new SpriteBatch();
        this.overlayFont = new BitmapFont();
        this.hudPanelRenderer = new ShapeRenderer();
        this.statusMessage = "Select a city, then press B to buy a donkey caravan";
    }

    @Override
    public void show() {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        camera.position.set(
            tileMap.getWidth() * TileMapRenderer.TILE_SIZE / 2f,
            tileMap.getHeight() * TileMapRenderer.TILE_SIZE / 2f,
            0f
        );
        camera.update();
        Gdx.input.setInputProcessor(new CameraInputHandler());
    }

    @Override
    public void render(float delta) {
        updateCamera(delta);
        handleShortcuts();
        ScreenUtils.clear(0.93f, 0.95f, 0.98f, 1f);
        tileMapRenderer.render(tileMap, camera);
        routeRenderer.render(demoRoute, camera);
        cityRenderer.render(cities, selectedCity, camera);
        renderHudPanels();
        renderFinanceOverlay();
        renderCityInfoOverlay();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        tileMapRenderer.dispose();
        routeRenderer.dispose();
        cityRenderer.dispose();
        overlayBatch.dispose();
        overlayFont.dispose();
        hudPanelRenderer.dispose();
    }

    private void handleShortcuts() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            if (selectedCity == null) {
                statusMessage = "Select a city first before purchase";
                return;
            }

            try {
                vehicleShop.purchaseVehicle(VehicleType.DONKEY_CARAVAN, selectedCity);
                statusMessage = "Purchased donkey caravan in " + selectedCity.getName();
            } catch (IllegalStateException exception) {
                statusMessage = "Purchase failed: insufficient funds";
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (vehicleShop.getOwnedVehicles().isEmpty()) {
                statusMessage = "Buy a vehicle first";
                return;
            }

            try {
                simulationEngine.assignTrip(vehicleShop.getOwnedVehicles().get(0), demoRoute, 10);
                statusMessage = "Assigned first vehicle to demo route";
            } catch (IllegalStateException exception) {
                statusMessage = "Assignment failed: vehicle already on active trip";
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            try {
                simulationEngine.tick();
                statusMessage = "Tick advanced";
            } catch (IllegalStateException exception) {
                statusMessage = "Tick failed: insufficient funds for operating cost";
            }
        }
    }

    private void renderFinanceOverlay() {
        float leftPanelX = 16f;
        float lineTop = 132f;

        overlayBatch.begin();
        overlayFont.draw(overlayBatch, String.format("Balance: %.0f", companyFinance.getBalance()), leftPanelX, lineTop);
        overlayFont.draw(overlayBatch, "Fleet: " + vehicleShop.getOwnedVehicles().size(), leftPanelX, lineTop - 24f);
        overlayFont.draw(overlayBatch, "Trips active/completed: "
            + simulationEngine.getActiveTrips().size() + "/" + simulationEngine.getCompletedTrips().size(), leftPanelX, lineTop - 48f);
        overlayFont.draw(overlayBatch, "B=buy  R=assign route  T=tick simulation", leftPanelX, lineTop - 72f);
        overlayFont.draw(overlayBatch, statusMessage, leftPanelX, lineTop - 96f);

        if (!simulationEngine.getActiveTrips().isEmpty()) {
            TransportTrip trip = simulationEngine.getActiveTrips().get(0);
            overlayFont.draw(overlayBatch,
                "Trip progress: " + trip.getProgressSteps() + "/" + trip.requiredSteps(),
                leftPanelX,
                lineTop - 120f
            );
        }
        overlayBatch.end();
    }

    private void renderCityInfoOverlay() {
        if (selectedCity == null) {
            return;
        }

        float panelX = Gdx.graphics.getWidth() - 260f;
        float lineTop = 132f;

        overlayBatch.begin();
        overlayFont.draw(overlayBatch, "Selected City", panelX, lineTop);
        overlayFont.draw(overlayBatch, "Name: " + selectedCity.getName(), panelX, lineTop - 24f);
        overlayFont.draw(overlayBatch, "Population: " + selectedCity.getPopulation(), panelX, lineTop - 48f);
        overlayBatch.end();
    }

    private void renderHudPanels() {
        float screenWidth = Gdx.graphics.getWidth();
        float panelY = 8f;
        float panelHeight = 136f;

        hudPanelRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hudPanelRenderer.setColor(0f, 0f, 0f, 0.45f);
        hudPanelRenderer.rect(8f, panelY, 500f, panelHeight);

        if (selectedCity != null) {
            hudPanelRenderer.rect(screenWidth - 268f, panelY, 260f, 80f);
        }
        hudPanelRenderer.end();
    }

    private void updateCamera(float delta) {
        float speed = PAN_SPEED * camera.zoom * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= speed;
        }

        clampCamera();
        camera.update();
    }

    private void clampCamera() {
        float halfViewportWidth = viewport.getWorldWidth() * 0.5f * camera.zoom;
        float halfViewportHeight = viewport.getWorldHeight() * 0.5f * camera.zoom;
        float mapWidth = tileMap.getWidth() * TileMapRenderer.TILE_SIZE;
        float mapHeight = tileMap.getHeight() * TileMapRenderer.TILE_SIZE;

        camera.position.x = clamp(camera.position.x, halfViewportWidth, mapWidth - halfViewportWidth);
        camera.position.y = clamp(camera.position.y, halfViewportHeight, mapHeight - halfViewportHeight);
    }

    private float clamp(float value, float min, float max) {
        if (min > max) {
            return (min + max) * 0.5f;
        }
        return Math.max(min, Math.min(max, value));
    }

    private int scaled(int value) {
        return value * MAP_SCALE;
    }

    private void setScaledTile(TileMap map, int tileX, int tileY, TileType tileType) {
        int startX = scaled(tileX);
        int startY = scaled(tileY);
        for (int x = startX; x < startX + MAP_SCALE; x++) {
            for (int y = startY; y < startY + MAP_SCALE; y++) {
                map.setTile(x, y, tileType);
            }
        }
    }

    private TileMap createDemoMap() {
        TileMap map = new TileMap(scaled(BASE_MAP_WIDTH), scaled(BASE_MAP_HEIGHT), TileType.SAND);

        for (int x = 0; x < map.getWidth(); x++) {
            map.setTile(x, 0, TileType.WATER);
            map.setTile(x, map.getHeight() - 1, TileType.WATER);
        }

        for (int x = scaled(5); x < scaled(7); x++) {
            for (int y = scaled(3); y < scaled(11); y++) {
                map.setTile(x, y, TileType.WATER);
            }
        }

        for (int x = scaled(12); x < scaled(17); x++) {
            for (int y = scaled(9); y < scaled(11); y++) {
                map.setTile(x, y, TileType.DUNE);
            }
        }

        setScaledTile(map, 14, 11, TileType.DUNE);
        setScaledTile(map, 15, 11, TileType.DUNE);
        setScaledTile(map, 2, 4, TileType.OASIS);
        setScaledTile(map, 3, 4, TileType.OASIS);
        setScaledTile(map, 10, 6, TileType.OASIS);
        setScaledTile(map, 11, 6, TileType.OASIS);
        setScaledTile(map, 15, 4, TileType.OASIS);
        return map;
    }

    private List<City> createCities(TileMap map) {
        return List.of(
            CityPlacementValidator.createTileAlignedCity("Riyadh", scaled(3), scaled(4), 1750000, map),
            CityPlacementValidator.createTileAlignedCity("Doha", scaled(10), scaled(6), 200000, map),
            CityPlacementValidator.createTileAlignedCity("Muscat", scaled(9), scaled(12), 160000, map),
            CityPlacementValidator.createTileAlignedCity("Jeddah", scaled(15), scaled(4), 130000, map)
        );
    }

    private City findClickedCity(float worldX, float worldY) {
        float selectionRadius = 18f;
        for (City city : cities) {
            float dx = worldX - city.getX();
            float dy = worldY - city.getY();
            if (dx * dx + dy * dy <= selectionRadius * selectionRadius) {
                return city;
            }
        }
        return null;
    }

    private final class CameraInputHandler extends InputAdapter {
        @Override
        public boolean scrolled(float amountX, float amountY) {
            camera.zoom = clamp(camera.zoom + amountY * 0.1f, MIN_ZOOM, MAX_ZOOM);
            clampCamera();
            camera.update();
            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (button != Input.Buttons.LEFT) {
                return false;
            }

            Vector3 worldPosition = viewport.unproject(new Vector3(screenX, screenY, 0f));
            selectedCity = findClickedCity(worldPosition.x, worldPosition.y);
            return selectedCity != null;
        }
    }
}
