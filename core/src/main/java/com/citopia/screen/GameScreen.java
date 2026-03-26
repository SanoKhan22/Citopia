package com.citopia.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        cityRenderer.renderOverlay(selectedCity);
        renderFinanceOverlay();
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
        overlayBatch.begin();
        overlayFont.draw(overlayBatch, String.format("Balance: %.0f", companyFinance.getBalance()), 16f, 132f);
        overlayFont.draw(overlayBatch, "Fleet: " + vehicleShop.getOwnedVehicles().size(), 16f, 108f);
        overlayFont.draw(overlayBatch, "Trips active/completed: "
            + simulationEngine.getActiveTrips().size() + "/" + simulationEngine.getCompletedTrips().size(), 16f, 84f);
        overlayFont.draw(overlayBatch, "B=buy  R=assign route  T=tick simulation", 16f, 60f);
        overlayFont.draw(overlayBatch, statusMessage, 16f, 36f);

        if (!simulationEngine.getActiveTrips().isEmpty()) {
            TransportTrip trip = simulationEngine.getActiveTrips().get(0);
            overlayFont.draw(overlayBatch,
                "Trip progress: " + trip.getProgressSteps() + "/" + trip.requiredSteps(),
                16f,
                12f
            );
        }
        overlayBatch.end();
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

    private TileMap createDemoMap() {
        TileMap map = new TileMap(20, 15, TileType.SAND);

        for (int x = 0; x < map.getWidth(); x++) {
            map.setTile(x, 0, TileType.WATER);
            map.setTile(x, map.getHeight() - 1, TileType.WATER);
        }

        for (int y = 3; y < 11; y++) {
            map.setTile(5, y, TileType.WATER);
            map.setTile(6, y, TileType.WATER);
        }

        for (int x = 12; x < 17; x++) {
            map.setTile(x, 9, TileType.DUNE);
            map.setTile(x, 10, TileType.DUNE);
        }

        map.setTile(14, 11, TileType.DUNE);
        map.setTile(15, 11, TileType.DUNE);
        map.setTile(2, 4, TileType.OASIS);
        map.setTile(3, 4, TileType.OASIS);
        map.setTile(10, 6, TileType.OASIS);
        map.setTile(11, 6, TileType.OASIS);
        map.setTile(15, 4, TileType.OASIS);
        return map;
    }

    private List<City> createCities(TileMap map) {
        return List.of(
            CityPlacementValidator.createTileAlignedCity("Riyadh", 3, 4, 1750000, map),
            CityPlacementValidator.createTileAlignedCity("Doha", 10, 6, 200000, map),
            CityPlacementValidator.createTileAlignedCity("Muscat", 9, 12, 160000, map),
            CityPlacementValidator.createTileAlignedCity("Jeddah", 15, 4, 130000, map)
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
