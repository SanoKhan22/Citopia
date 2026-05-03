package com.citopia.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.citopia.CitopiaGame;
import com.citopia.assets.AssetId;
import com.citopia.model.PlayerState;
import com.citopia.model.Vehicle;
import com.citopia.model.VehicleType;
import com.citopia.world.CitySite;
import com.citopia.world.MapConfig;
import com.citopia.world.TileMap;
import com.citopia.world.ZoneType;
import com.citopia.world.transport.Route;
import com.citopia.world.transport.RouteNetwork;
import java.util.List;
import java.util.Random;

public class GameScreen extends ScreenAdapter {

    // ── Build modes ──────────────────────────────────────────────
    private enum BuildMode { POINTER, ROAD, DEMOLISH }

    // Oasis constants kept only for drawThreeExtraLakeTrees & drawStackedMountainStones
    private static final int OASIS_HALF_WIDTH  = 6;
    private static final int OASIS_HALF_HEIGHT = 4;
    private static final int MINIMAP_SIZE_PX    = 220;
    private static final int MINIMAP_PADDING_PX = 16;
    private static final float MINIMAP_MARKER_SIZE = 4f;

    // Toolbar layout constants
    private static final float BTN_W    = 110f;
    private static final float BTN_H    = 44f;
    private static final float BTN_GAP  = 6f;
    private static final float TOOLBAR_PADDING = 8f;

    private final CitopiaGame game;
    private final OrthographicCamera camera;
    private final ScreenViewport viewport;
    private final TileMap tileMap;
    private final RouteNetwork routeNetwork;

    private final TextureRegion[] groundRegions;
    private final TextureRegion desertSandRegion;
    private final TextureRegion oasisLakeRegion;
    private final TextureRegion inhabitantGroundRegion;
    private final TextureRegion greenery6Region;
    private final TextureRegion greenery10Region;
    private final TextureRegion stones1Region;
    private final TextureRegion stones4Region;
    private final TextureRegion stones7Region;
    private final TextureRegion bigStonesBase12Region;
    private final TextureRegion bigStonesMid11Region;
    private final TextureRegion bigStonesPeak10Region;
    private final TextureRegion decor3Region;
    private final TextureRegion building1Region;
    private final TextureRegion building2Region;
    private final TextureRegion building3Region;
    private final TextureRegion building4Region;
    private final TextureRegion building5Region;
    private final TextureRegion wellRegion;
    private final TextureRegion decor5Region;
    private final TextureRegion tree8Region;
    private final TextureRegion treeLargeRegion;
    private final TextureRegion houseRegion;
    private final TextureRegion castleSquareRegion;
    private final TextureRegion castleRoundRegion;
    private final TextureRegion blueBannerRegion;
    private final TextureRegion rock01Region;
    private final TextureRegion rock02Region;
    private final TextureRegion rock03Region;
    private final TextureRegion rock04Region;
    private final TextureRegion rock05Region;
    private final TextureRegion campfireRegion;
    private final TextureRegion tentStandardRegion;
    private final TextureRegion tentDesertRegion;
    private final TextureRegion deadTree1Region;
    private final TextureRegion deadTree2Region;
    private final TextureRegion decor8Region;
    private final TextureRegion woodenCartRegion;
    private final TextureRegion magicTowerRegion;
    private final TextureRegion windmillRegion;
    private final TextureRegion roadRegion;      // raw road sprite (full_Road.png)
    private final RoadRenderer  roadRenderer;    // auto-tiling road renderer
    private final BitmapFont hudFont;
    private final Pixmap minimapPixmap;
    private final Texture minimapTexture;
    private final Texture hudPixel;
    private final BitmapFont goldFont;   // slightly larger font for gold counter
    private float pendingScrollY;

    // ── Build menu state ─────────────────────────────────────────
    private BuildMode buildMode = BuildMode.POINTER;
    private int hoverTileX = -1;   // world tile the mouse is over
    private int hoverTileY = -1;
    private CitySite selectedCity;
    private CitySite routeOriginCity;
    private boolean vehicleMarketOpen;
    /** Feedback message shown bottom-centre (e.g. "Not enough gold!"). Fades over time. */
    private String feedbackMsg  = "";
    private float  feedbackTimer = 0f;
    private static final float FEEDBACK_DURATION = 2.5f;

    // Economy: in-game time accumulator (1 month = 30 real seconds at normal speed)
    private static final float MONTH_DURATION_SECONDS = 30f;
    private float monthTimer = 0f;

    public GameScreen(CitopiaGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new ScreenViewport(camera);
        this.tileMap = new TileMap(MapConfig.MAP_WIDTH_TILES, MapConfig.MAP_HEIGHT_TILES, 42L);
        this.routeNetwork = new RouteNetwork(tileMap);
        this.routeNetwork.generateNetwork();
        this.hudFont = new BitmapFont();
        this.hudFont.getData().setScale(1.1f);

        this.groundRegions = new TextureRegion[MapConfig.GROUND_TILE_VARIANTS];
        for (int i = 0; i < MapConfig.GROUND_TILE_VARIANTS; i++) {
            String number = String.format("%02d", i + 1);
            String regionName = "Top-Down Simple Summer_Ground " + number;
            groundRegions[i] = game.assets.region(regionName);
        }

        this.desertSandRegion = safeRegion(AssetId.TERRAIN_DESERT_SAND, groundRegions[0]);
        this.oasisLakeRegion = safeRegion(AssetId.TERRAIN_OASIS_LAKE, groundRegions[1]);
        this.inhabitantGroundRegion = groundRegions[22];
        this.greenery6Region = safeRegion(AssetId.PROP_GREENERY_6, groundRegions[2]);
        this.greenery10Region = safeRegion(AssetId.PROP_GREENERY_10, greenery6Region);
        this.stones1Region = safeRegion(AssetId.PROP_STONES_1,
                safeRegion(AssetId.TERRAIN_DESERT_STONE_1, greenery6Region));
        this.stones4Region = safeRegion(AssetId.PROP_STONES_4, stones1Region);
        this.stones7Region = safeRegion(AssetId.PROP_STONES_7, stones1Region);
        this.bigStonesBase12Region = safeRegion(AssetId.PROP_BIGSTONES_BASE_12, stones1Region);
        this.bigStonesMid11Region = safeRegion(AssetId.PROP_BIGSTONES_MID_11, bigStonesBase12Region);
        this.bigStonesPeak10Region = safeRegion(AssetId.PROP_BIGSTONES_PEAK_10, bigStonesMid11Region);
        this.decor3Region = safeRegion(AssetId.PROP_DECOR_3, greenery10Region);
        this.building1Region = safeRegion(AssetId.PROP_BUILDING_1,
                safeRegion(AssetId.PROP_CITY_HOUSE, greenery10Region));
        this.building2Region = safeRegion(AssetId.PROP_BUILDING_2, building1Region);
        this.building3Region = safeRegion(AssetId.PROP_BUILDING_3, building1Region);
        this.building4Region = safeRegion(AssetId.PROP_BUILDING_4, building3Region);
        this.building5Region = safeRegion(AssetId.PROP_BUILDING_5,
                safeRegion(AssetId.PROP_CITY_HOUSE, building1Region));
        this.wellRegion = safeRegion(AssetId.PROP_WELL, building5Region);
        this.decor5Region = safeRegion(AssetId.PROP_DECOR_5, greenery10Region);
        this.tree8Region = safeRegion(AssetId.PROP_TREE_8, safeRegion(AssetId.PROP_TREE_MEDIUM, greenery10Region));
        this.treeLargeRegion = safeRegion(AssetId.PROP_TREE_LARGE,
                safeRegion(AssetId.PROP_TREE_MEDIUM, greenery10Region));
        this.houseRegion = safeRegion(AssetId.PROP_HOUSE_SUMMER, greenery10Region);
        this.castleSquareRegion = safeRegion(AssetId.PROP_CASTLE_SQUARE, greenery10Region);
        this.castleRoundRegion = safeRegion(AssetId.PROP_CASTLE_ROUND, greenery10Region);
        this.blueBannerRegion = safeRegion(AssetId.PROP_BLUE_BANNER, greenery10Region);
        this.rock01Region = safeRegion(AssetId.PROP_ROCK_01_SUMMER, stones1Region);
        this.rock02Region = safeRegion(AssetId.PROP_ROCK_02_SUMMER, rock01Region);
        this.rock03Region = safeRegion(AssetId.PROP_ROCK_03_SUMMER, rock01Region);
        this.rock04Region = safeRegion(AssetId.PROP_ROCK_04_SUMMER, rock01Region);
        this.rock05Region = safeRegion(AssetId.PROP_ROCK_05_SUMMER, rock01Region);
        this.campfireRegion = safeRegion(AssetId.PROP_CAMPFIRE_SUMMER, wellRegion);
        this.tentStandardRegion = safeRegion(AssetId.PROP_CAMP_TENT_1, wellRegion);
        this.tentDesertRegion = safeRegion(AssetId.PROP_CAMP_TENT_2, wellRegion);
        this.deadTree1Region = safeRegion(AssetId.PROP_DEAD_TREE_1, rock01Region);
        this.deadTree2Region = safeRegion(AssetId.PROP_DEAD_TREE_2, rock01Region);
        this.decor8Region = safeRegion(AssetId.PROP_DECOR_8, rock01Region);
        this.woodenCartRegion = safeRegion(AssetId.VEHICLE_WOODEN_CART, rock01Region);
        this.magicTowerRegion = safeRegion(AssetId.PROP_MAGIC_TOWER, rock01Region);
        this.windmillRegion = safeRegion(AssetId.PROP_WINDMILL, houseRegion);
        this.roadRegion     = game.assets.texture("full_Road.png");
        this.roadRenderer   = new RoadRenderer(roadRegion, tileMap);

        this.minimapPixmap = new Pixmap(tileMap.width(), tileMap.height(), Pixmap.Format.RGBA8888);
        buildMinimapPixmap();
        this.minimapTexture = new Texture(minimapPixmap);

        Pixmap pixelPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixelPixmap.setColor(Color.WHITE);
        pixelPixmap.fill();
        this.hudPixel = new Texture(pixelPixmap);
        pixelPixmap.dispose();

        this.goldFont = new BitmapFont();
        this.goldFont.getData().setScale(1.4f);
        this.goldFont.setColor(1f, 0.87f, 0.27f, 1f); // warm gold colour

        camera.position.set(
                (tileMap.width() * MapConfig.TILE_DRAW_SIZE) / 2f,
                (tileMap.height() * MapConfig.TILE_DRAW_SIZE) / 2f,
                0f);
        camera.zoom = 2.0f;
        camera.update();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        centerCameraOnCityZone();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void show() {
        centerCameraOnCityZone();
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean scrolled(float amountX, float amountY) {
                pendingScrollY += amountY;
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    if (vehicleMarketOpen && handleVehicleMarketClick(screenX, screenY)) return true;
                    // Check toolbar button clicks first
                    if (handleToolbarClick(screenX, screenY)) return true;
                    // Otherwise handle world-tile action
                    handleWorldClick(screenX, screenY);
                    return true;
                }
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                updateHoverTile(screenX, screenY);
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                // Allow painting roads by dragging in ROAD mode
                if (buildMode == BuildMode.ROAD) {
                    handleWorldClick(screenX, screenY);
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (vehicleMarketOpen) {
                    handleVehicleMarketKey(keycode);
                    return true;
                }
                switch (keycode) {
                    case Input.Keys.R -> {
                        routeOriginCity = null;
                        buildMode = BuildMode.ROAD;
                    }
                    case Input.Keys.X -> {
                        routeOriginCity = null;
                        buildMode = BuildMode.DEMOLISH;
                    }
                    case Input.Keys.V -> openVehicleMarket();
                    case Input.Keys.C -> startRouteCreation();
                    case Input.Keys.ESCAPE -> {
                        vehicleMarketOpen = false;
                        routeOriginCity = null;
                        buildMode = BuildMode.POINTER;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    private void handleInput(float delta) {
        float moveSpeed = 900f * camera.zoom * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += moveSpeed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom = Math.min(7.0f, camera.zoom + 0.8f * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom = Math.max(0.35f, camera.zoom - 0.8f * delta);
        }

        if (pendingScrollY != 0f) {
            camera.zoom = clamp(camera.zoom + pendingScrollY * 0.1f, 0.35f, 7.0f);
            pendingScrollY = 0f;
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            camera.position.x -= Gdx.input.getDeltaX() * camera.zoom;
            camera.position.y += Gdx.input.getDeltaY() * camera.zoom;
        }

        float halfWorldWidth = (Gdx.graphics.getWidth() * camera.zoom) / 2f;
        float halfWorldHeight = (Gdx.graphics.getHeight() * camera.zoom) / 2f;
        float mapPixelWidth = tileMap.width() * MapConfig.TILE_DRAW_SIZE;
        float mapPixelHeight = tileMap.height() * MapConfig.TILE_DRAW_SIZE;

        camera.position.x = clamp(camera.position.x, halfWorldWidth, mapPixelWidth - halfWorldWidth);
        camera.position.y = clamp(camera.position.y, halfWorldHeight, mapPixelHeight - halfWorldHeight);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    // ── Build-mode helpers ─────────────────────────────────────────

    /** Convert screen coords to world tile and update hover state. */
    private void updateHoverTile(int screenX, int screenY) {
        com.badlogic.gdx.math.Vector3 world = new com.badlogic.gdx.math.Vector3(screenX, screenY, 0);
        camera.unproject(world);
        hoverTileX = (int) (world.x / MapConfig.TILE_DRAW_SIZE);
        hoverTileY = (int) (world.y / MapConfig.TILE_DRAW_SIZE);
    }

    /**
     * Returns true if a toolbar button was clicked, consuming the event.
     * Toolbar is bottom-centre of screen.
     */
    private boolean handleToolbarClick(int screenX, int screenY) {
        // Convert to LibGDX y-up screen coords
        int gdxY = Gdx.graphics.getHeight() - screenY;
        BuildMode[] modes = { BuildMode.POINTER, BuildMode.ROAD, BuildMode.DEMOLISH };
        for (int i = 0; i < modes.length; i++) {
            float bx = toolbarButtonX(i);
            float by = TOOLBAR_PADDING;
            if (screenX >= bx && screenX <= bx + BTN_W && gdxY >= by && gdxY <= by + BTN_H) {
                buildMode = modes[i];
                routeOriginCity = null;
                return true;
            }
        }
        return false;
    }

    /** Place/demolish a tile when the player clicks the world. */
    private void handleWorldClick(int screenX, int screenY) {
        updateHoverTile(screenX, screenY);
        int tx = hoverTileX;
        int ty = hoverTileY;
        if (!tileMap.inBounds(tx, ty)) return;

        switch (buildMode) {
            case ROAD -> {
                if (tileMap.hasRoad(tx, ty)) return; // already a road
                if (!game.playerState.canAfford(PlayerState.COST_ROAD)) {
                    showFeedback("Not enough gold! (need " + PlayerState.COST_ROAD + "g)");
                    return;
                }
                game.playerState.spend(PlayerState.COST_ROAD);
                tileMap.placeRoad(tx, ty);
                refreshMinimapTile(tx, ty);
            }
            case DEMOLISH -> {
                if (!tileMap.hasRoad(tx, ty)) return; // nothing to demolish
                if (!game.playerState.canAfford(PlayerState.COST_DEMOLISH)) {
                    showFeedback("Not enough gold! (need " + PlayerState.COST_DEMOLISH + "g)");
                    return;
                }
                game.playerState.spend(PlayerState.COST_DEMOLISH);
                tileMap.removeRoad(tx, ty);
                refreshMinimapTile(tx, ty);
            }
            case POINTER -> handlePointerClick(tx, ty);
        }
    }

    private void showFeedback(String msg) {
        feedbackMsg   = msg;
        feedbackTimer = FEEDBACK_DURATION;
    }

    private void selectCityAt(int tileX, int tileY) {
        CitySite city = tileMap.cityAt(tileX, tileY);
        selectedCity = city;
        if (city == null) {
            vehicleMarketOpen = false;
            showFeedback("No city at this tile. Select a city footprint or switch tools.");
            return;
        }
        showFeedback("Selected " + city.name);
    }

    private void handlePointerClick(int tileX, int tileY) {
        if (routeOriginCity != null) {
            completeRouteCreation(tileX, tileY);
            return;
        }
        selectCityAt(tileX, tileY);
    }

    private void startRouteCreation() {
        if (selectedCity == null) {
            showFeedback("Select a city before creating a route.");
            return;
        }

        vehicleMarketOpen = false;
        buildMode = BuildMode.POINTER;
        routeOriginCity = selectedCity;
        showFeedback("Route origin set: " + selectedCity.name + ". Click a destination city.");
    }

    private void completeRouteCreation(int tileX, int tileY) {
        CitySite destination = tileMap.cityAt(tileX, tileY);
        if (destination == null) {
            showFeedback("Choose a destination city footprint for the route.");
            return;
        }
        if (destination == routeOriginCity) {
            showFeedback("Route needs a different destination city.");
            return;
        }

        Route route = routeNetwork.computeShortestRoute(routeOriginCity, destination);
        if (route == null) {
            showFeedback("No road route found. Build roads between those cities first.");
            return;
        }

        selectedCity = destination;
        routeOriginCity = null;
        if (!game.playerState.addRoute(route)) {
            showFeedback("Route already exists between those cities.");
            return;
        }

        showFeedback("Route created: " + route.getName() + " (" + route.getLength() + " tiles)");
    }

    private void openVehicleMarket() {
        if (selectedCity == null) {
            showFeedback("Select a city before opening the vehicle market.");
            return;
        }
        routeOriginCity = null;
        vehicleMarketOpen = true;
        buildMode = BuildMode.POINTER;
        showFeedback("Vehicle market opened for " + selectedCity.name);
    }

    private boolean handleVehicleMarketKey(int keycode) {
        if (!vehicleMarketOpen) {
            return false;
        }
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.V) {
            vehicleMarketOpen = false;
            return true;
        }

        VehicleType[] types = VehicleType.values();
        int index = switch (keycode) {
            case Input.Keys.NUM_1, Input.Keys.NUMPAD_1 -> 0;
            case Input.Keys.NUM_2, Input.Keys.NUMPAD_2 -> 1;
            case Input.Keys.NUM_3, Input.Keys.NUMPAD_3 -> 2;
            case Input.Keys.NUM_4, Input.Keys.NUMPAD_4 -> 3;
            default -> -1;
        };
        if (index >= 0 && index < types.length) {
            buyVehicle(types[index]);
            return true;
        }
        return false;
    }

    private boolean handleVehicleMarketClick(int screenX, int screenY) {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        int gdxY = screenH - screenY;

        float panelW = Math.min(430f, Math.max(320f, screenW - 32f));
        float panelH = 300f;
        float panelX = (screenW - panelW) / 2f;
        float panelY = (screenH - panelH) / 2f;

        if (screenX < panelX || screenX > panelX + panelW || gdxY < panelY || gdxY > panelY + panelH) {
            vehicleMarketOpen = false;
            return true;
        }

        float rowX = panelX + 18f;
        float rowW = panelW - 36f;
        float rowH = 48f;
        float firstRowY = panelY + panelH - 88f;
        VehicleType[] types = VehicleType.values();
        for (int i = 0; i < types.length; i++) {
            float rowY = firstRowY - i * (rowH + 8f);
            if (screenX >= rowX && screenX <= rowX + rowW && gdxY >= rowY && gdxY <= rowY + rowH) {
                buyVehicle(types[i]);
                return true;
            }
        }
        return true;
    }

    private void buyVehicle(VehicleType type) {
        if (selectedCity == null) {
            showFeedback("Select a city before buying vehicles.");
            vehicleMarketOpen = false;
            return;
        }

        Vehicle vehicle = game.playerState.purchaseVehicle(type, selectedCity.name);
        if (vehicle == null) {
            showFeedback("Not enough gold for " + type.displayName() + " (" + type.price() + "g)");
            return;
        }

        showFeedback("Purchased " + vehicle.displayName() + " in " + selectedCity.name);
    }

    /** X position of the i-th toolbar button (in screen / HUD coordinates). */
    private float toolbarButtonX(int index) {
        int btnCount = 3;
        float totalW = btnCount * BTN_W + (btnCount - 1) * BTN_GAP;
        float startX = (Gdx.graphics.getWidth() - totalW) / 2f;
        return startX + index * (BTN_W + BTN_GAP);
    }

    private TextureRegion safeRegion(AssetId assetId, TextureRegion fallback) {
        try {
            return game.assets.region(assetId);
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }

    private void centerCameraOnCityZone() {
        camera.position.set(
                (tileMap.width() * MapConfig.TILE_DRAW_SIZE) / 2f,
                (tileMap.height() * MapConfig.TILE_DRAW_SIZE) / 2f,
                0f);
        camera.update();
    }

    private void buildMinimapPixmap() {
        for (int y = 0; y < tileMap.height(); y++) {
            for (int x = 0; x < tileMap.width(); x++) {
                applyMinimapColor(x, y);
            }
        }
    }

    private void applyMinimapColor(int x, int y) {
        if (tileMap.hasRoad(x, y)) {
            minimapPixmap.setColor(0.45f, 0.35f, 0.20f, 1f);
            // Draw roads a bit thicker (3x3) so they don't disappear when the minimap is scaled down
            minimapPixmap.fillRectangle(x - 1, tileMap.height() - 1 - y - 1, 3, 3);
        } else {
            ZoneType zone = tileMap.zone(x, y);
            switch (zone) {
                case OASIS_LAKE        -> minimapPixmap.setColor(0.16f, 0.47f, 0.70f, 1f);
                case OASIS_STONE       -> minimapPixmap.setColor(0.56f, 0.53f, 0.47f, 1f);
                case OASIS_TREE        -> minimapPixmap.setColor(0.22f, 0.46f, 0.23f, 1f);
                case OASIS_GREENERY    -> minimapPixmap.setColor(0.25f, 0.56f, 0.29f, 1f);
                case LAKE_COAST_DECOR  -> minimapPixmap.setColor(0.22f, 0.46f, 0.23f, 1f);
                case CITY              -> minimapPixmap.setColor(0.68f, 0.57f, 0.34f, 1f);
                default                -> minimapPixmap.setColor(0.84f, 0.64f, 0.39f, 1f);
            }
            minimapPixmap.drawPixel(x, tileMap.height() - 1 - y);
        }
    }

    public void refreshMinimapTile(int x, int y) {
        if (x < 0 || y < 0 || x >= tileMap.width() || y >= tileMap.height()) {
            return;
        }

        applyMinimapColor(x, y);
        minimapTexture.draw(minimapPixmap, 0, 0);
    }

    // ── Zone- and noise-based texture selectors (data-driven, not recomputed) ──

    private TextureRegion selectOasisStone(int x, int y) {
        int noise = Math.floorMod((x * 29) ^ (y * 83), 100);
        return noise < 50 ? stones1Region : stones7Region;
    }

    private TextureRegion selectOasisTree(int x, int y) {
        int noise = Math.floorMod((x * 31) ^ (y * 61), 100);
        return noise < 70 ? treeLargeRegion : tree8Region;
    }

    private TextureRegion selectOasisGreenery(int x, int y) {
        int noise = Math.floorMod((x * 67) ^ (y * 59), 100);
        return noise < 68 ? greenery6Region : greenery10Region;
    }

    private void drawOasisFoliage(int x, int y, TextureRegion region) {
        float drawX = x * MapConfig.TILE_DRAW_SIZE;
        float drawY = y * MapConfig.TILE_DRAW_SIZE;

        if (region == treeLargeRegion) {
            float width = MapConfig.TILE_DRAW_SIZE * 1.6f;
            float height = MapConfig.TILE_DRAW_SIZE * 1.8f;
            float centeredX = drawX - (width - MapConfig.TILE_DRAW_SIZE) * 0.5f;
            game.batch.draw(region, centeredX, drawY, width, height);
            return;
        }

        if (region == tree8Region) {
            float width = MapConfig.TILE_DRAW_SIZE * 1.45f;
            float height = MapConfig.TILE_DRAW_SIZE * 1.95f;
            float centeredX = drawX - (width - MapConfig.TILE_DRAW_SIZE) * 0.5f;
            game.batch.draw(region, centeredX, drawY, width, height);
            return;
        }

        game.batch.draw(region, drawX, drawY, MapConfig.TILE_DRAW_SIZE, MapConfig.TILE_DRAW_SIZE);
    }

    private void drawThreeExtraLakeTrees() {
        int midX = tileMap.width() / 2;
        int midY = tileMap.height() / 2;

        int[][] extraTreeTiles = new int[][] {
                { midX - (OASIS_HALF_WIDTH + 3), midY + 1 },
                { midX + (OASIS_HALF_WIDTH + 3), midY },
                { midX, midY - (OASIS_HALF_HEIGHT + 4) }
        };

        for (int[] tile : extraTreeTiles) {
            drawOasisFoliage(tile[0], tile[1], treeLargeRegion);
        }
    }

    private void drawStackedMountainStones() {
        int midX = tileMap.width() / 2;
        int midY = tileMap.height() / 2;

        float anchorTileX = midX - (OASIS_HALF_WIDTH + 12f);
        float anchorTileY = midY + OASIS_HALF_HEIGHT + 3f;

        float anchorX = anchorTileX * MapConfig.TILE_DRAW_SIZE;
        float anchorY = anchorTileY * MapConfig.TILE_DRAW_SIZE;

        float baseWidth = MapConfig.TILE_DRAW_SIZE * 9.2f;
        float baseHeight = MapConfig.TILE_DRAW_SIZE * 6.2f;
        float midWidth = MapConfig.TILE_DRAW_SIZE * 7.6f;
        float midHeight = MapConfig.TILE_DRAW_SIZE * 5.3f;
        float peakWidth = MapConfig.TILE_DRAW_SIZE * 5.8f;
        float peakHeight = MapConfig.TILE_DRAW_SIZE * 4.3f;

        game.batch.draw(bigStonesBase12Region, anchorX, anchorY, baseWidth, baseHeight);
        game.batch.draw(bigStonesMid11Region, anchorX + MapConfig.TILE_DRAW_SIZE * 0.8f,
                anchorY + MapConfig.TILE_DRAW_SIZE * 1.0f, midWidth, midHeight);
        game.batch.draw(bigStonesPeak10Region, anchorX + MapConfig.TILE_DRAW_SIZE * 1.7f,
                anchorY + MapConfig.TILE_DRAW_SIZE * 1.9f, peakWidth, peakHeight);
    }

    private void drawNorthCity() {
        int midX = tileMap.width() / 2;
        int midY = tileMap.height() / 2;

        float topRowY = midY + OASIS_HALF_HEIGHT + 20f;
        float midRowY = topRowY - 5.8f;
        float homeRowY = topRowY - 10.0f;
        float outerHomeRowY = homeRowY - 3.8f;
        float villageCenterY = topRowY - 7.3f;

        // North skyline anchors
        drawCityBuilding(building3Region, midX - 16.0f, topRowY + 0.3f, 1.9f, 3.0f);
        drawCityBuilding(building1Region, midX - 3.0f, topRowY, 2.8f, 3.4f);
        drawCityBuilding(building4Region, midX + 10.5f, topRowY + 0.2f, 1.9f, 3.0f);

        // Main street anchors (with intentional open gaps between)
        drawCityBuilding(building2Region, midX - 11.5f, midRowY, 3.4f, 2.2f);
        drawCityBuilding(building2Region, midX + 4.5f, midRowY - 0.2f, 3.4f, 2.2f);

        // Village center well
        drawCityBuilding(wellRegion, midX - 1.2f, villageCenterY, 2.8f, 2.8f);

        // Two stones_4 near major homes/building anchors
        drawCityBuilding(stones4Region, midX - 6.6f, topRowY - 1.5f, 1.2f, 1.2f);
        drawCityBuilding(stones4Region, midX + 8.7f, midRowY - 1.1f, 1.2f, 1.2f);

        // Scattered homes cluster 1 (west side)
        drawCityBuilding(building5Region, midX - 14.0f, homeRowY, 1.8f, 2.1f);
        drawCityBuilding(building5Region, midX - 9.4f, homeRowY + 0.7f, 1.8f, 2.1f);
        drawCityBuilding(building5Region, midX - 6.0f, homeRowY - 0.3f, 1.8f, 2.1f);

        // Scattered homes cluster 2 (center-east side)
        drawCityBuilding(building5Region, midX + 0.6f, homeRowY + 0.5f, 1.8f, 2.1f);
        drawCityBuilding(building5Region, midX + 5.2f, homeRowY - 0.4f, 1.8f, 2.1f);
        drawCityBuilding(building5Region, midX + 9.6f, homeRowY + 0.3f, 1.8f, 2.1f);

        // Outer homes for larger village footprint
        drawCityBuilding(building5Region, midX + 16.2f, outerHomeRowY + 1.1f, 1.8f, 2.1f);
        drawCityBuilding(building5Region, midX - 1.8f, outerHomeRowY + 0.4f, 1.8f, 2.1f);
        drawCityBuilding(building5Region, midX + 12.8f, outerHomeRowY - 0.2f, 1.8f, 2.1f);
    }

    private void drawSouthVillage() {
        int midX = tileMap.width() / 2;
        int midY = tileMap.height() / 2;

        // Magic Stone Tower, very unique, 8 tiles out from oasis
        float magicTowerY = midY - OASIS_HALF_HEIGHT - 8.0f;
        drawCityBuilding(magicTowerRegion, midX - 3.5f, magicTowerY, 3.5f, 4.5f);

        // South Village grows towards the oasis
        // Houses start 12 tiles away from oasis edge
        float housesBaseY = midY - OASIS_HALF_HEIGHT - 12f;
        // Towers and flag are 18-20 tiles away from oasis edge (further out)
        float towersBaseY = midY - OASIS_HALF_HEIGHT - 19f;

        // Use a consistent random seed for village layout
        Random villageRandom = new Random(42L);

        // Draw rocks first so they appear behind buildings and don't overlap them
        // incorrectly
        Random rockRandom = new Random(123L);
        drawSouthVillageRocks(rockRandom, midX, housesBaseY, towersBaseY);

        // Castle Round on the left (outer perimeter)
        drawCityBuilding(castleRoundRegion, midX - 10.0f, towersBaseY, 4.0f, 4.0f);

        // Blue Banner Flag in the center (outer perimeter, tallest point)
        drawCityBuilding(blueBannerRegion, midX - 1.0f, towersBaseY + 1.0f, 2.5f, 2.5f);

        // Castle Square on the right (outer perimeter)
        drawCityBuilding(castleSquareRegion, midX + 8.0f, towersBaseY, 4.0f, 4.0f);

        // Five houses spread across South-East to South-West, closer to oasis
        // We limit X randomness to a strict range (+/- 0.6f) so they do not overlap
        // each other

        // Add 2 Wells (Placed between/behind houses). Draw them FIRST so they stay
        // behind.
        drawCityBuilding(wellRegion, midX - 10.5f, housesBaseY + 2.5f, 1.8f, 1.8f);
        drawCityBuilding(wellRegion, midX + 10.5f, housesBaseY + 2.2f, 1.8f, 1.8f);

        // House 5 (Far West, Highest Y first so it stays behind if overlapping)
        float house5X = midX - 13.5f + (villageRandom.nextFloat() * 1.2f - 0.6f);
        float house5Y = housesBaseY + 1.2f + (villageRandom.nextFloat() * 1.0f - 0.5f);
        drawCityBuilding(houseRegion, house5X, house5Y, 2.8f, 2.8f);

        // House 1 (Far East)
        float house1X = midX + 13.0f + (villageRandom.nextFloat() * 1.2f - 0.6f);
        float house1Y = housesBaseY + 1.0f + (villageRandom.nextFloat() * 1.0f - 0.5f);
        drawCityBuilding(houseRegion, house1X, house1Y, 2.8f, 2.8f);

        // House 4 (Mid-West)
        float house4X = midX - 10.0f + (villageRandom.nextFloat() * 1.2f - 0.6f);
        float house4Y = housesBaseY + 0.8f + (villageRandom.nextFloat() * 1.0f - 0.5f);
        drawCityBuilding(houseRegion, house4X, house4Y, 2.8f, 2.8f);

        // House 2 (East)
        float house2X = midX + 8.5f + (villageRandom.nextFloat() * 1.2f - 0.6f);
        float house2Y = housesBaseY + 0.5f + (villageRandom.nextFloat() * 1.0f - 0.5f);
        drawCityBuilding(houseRegion, house2X, house2Y, 2.8f, 2.8f);

        // House 3 (West-Center)
        float house3X = midX - 6.5f + (villageRandom.nextFloat() * 1.2f - 0.6f);
        float house3Y = housesBaseY + (villageRandom.nextFloat() * 1.0f - 0.5f);
        drawCityBuilding(houseRegion, house3X, house3Y, 2.8f, 2.8f);

        // Add 2 Campfires (Placed in front of the houses, drawn LAST to stay on top)
        drawCityBuilding(campfireRegion, midX - 10.5f, housesBaseY - 2.0f, 1.5f, 1.5f);
        drawCityBuilding(campfireRegion, midX + 10.5f, housesBaseY - 1.5f, 1.5f, 1.5f);

        // Add Eastern and Western Camps (Nomad camps, fully apart from South Village)
        drawNomadCamps(villageRandom, midX, midY);
    }

    private void drawNomadCamps(Random villageRandom, int midX, int midY) {
        // True West Camp (Desert Tents, dead trees, decor8, cart)
        // Set at Y = midY entirely, and X = midX - 25 so it is cleanly in the West
        float westCampX = midX - 25.0f;
        float westCampY = midY;

        drawCityBuilding(deadTree1Region, westCampX - 2.0f, westCampY + 3.0f, 2.5f, 3.5f);
        drawCityBuilding(decor8Region, westCampX + 3.0f, westCampY + 2.0f, 1.2f, 1.2f);

        // 3 Tents (Desert style) scattered around the campfire, with at least 1 tile
        // spacing
        for (int i = 0; i < 3; i++) {
            float tentX = westCampX - 4.0f + (i * 3.5f) + (villageRandom.nextFloat() * 0.6f - 0.3f);
            float tentY = westCampY - 1.0f + (villageRandom.nextFloat() * 1.0f - 0.5f);
            drawCityBuilding(tentDesertRegion, tentX, tentY, 2.4f, 2.4f);
        }

        drawCityBuilding(woodenCartRegion, westCampX + 1.5f, westCampY - 3.0f, 1.5f, 1.5f);
        drawCityBuilding(campfireRegion, westCampX, westCampY - 1.5f, 1.3f, 1.3f);
        drawCityBuilding(deadTree2Region, westCampX - 4.0f, westCampY - 4.0f, 2.0f, 3.0f);

        // True East Camp (Standard Tents, dead trees, decor8, cart)
        // Set at Y = midY entirely, and X = midX + 25 so it is cleanly in the East
        float eastCampX = midX + 25.0f;
        float eastCampY = midY;

        drawCityBuilding(deadTree2Region, eastCampX + 1.0f, eastCampY + 3.5f, 2.5f, 3.5f);
        drawCityBuilding(decor8Region, eastCampX - 3.5f, eastCampY + 2.5f, 1.2f, 1.2f);

        // 4 Tents (Standard style), with at least 1 tile spacing
        for (int i = 0; i < 4; i++) {
            float tentX = eastCampX - 5.5f + (i * 3.5f) + (villageRandom.nextFloat() * 0.6f - 0.3f);
            float tentY = eastCampY - 0.5f + (villageRandom.nextFloat() * 1.0f - 0.5f);
            drawCityBuilding(tentStandardRegion, tentX, tentY, 2.4f, 2.4f);
        }

        drawCityBuilding(woodenCartRegion, eastCampX + 3.5f, eastCampY - 2.5f, 1.5f, 1.5f);
        drawCityBuilding(campfireRegion, eastCampX, eastCampY - 2.0f, 1.3f, 1.3f);
        drawCityBuilding(deadTree1Region, eastCampX - 3.5f, eastCampY - 3.5f, 2.0f, 3.0f);
    }

    private void drawWindmills() {
        int midX = tileMap.width() / 2;
        int midY = tileMap.height() / 2;

        // North-West Windmill
        float nwX = midX - 20.0f;
        float nwY = midY + 18.0f;
        drawCityBuilding(windmillRegion, nwX, nwY, 3.5f, 4.5f);

        // South-East Windmill
        float seX = midX + 22.0f;
        float seY = midY - 22.0f;
        drawCityBuilding(windmillRegion, seX, seY, 3.5f, 4.5f);
    }

    private void drawSouthVillageRocks(Random villageRandom, int midX, float housesBaseY, float towersBaseY) {
        // Place rocks away from the center banner (-1.0f) and the main houses
        float[] clusterX = { midX - 15.0f, midX - 5.0f, midX + 3.0f, midX + 13.0f };
        float centerBandY = (housesBaseY + towersBaseY) * 0.5f;

        for (float baseX : clusterX) {
            for (int i = 0; i < 3; i++) {
                float rockX = baseX + (villageRandom.nextFloat() * 4.0f - 2.0f);
                float rockY = centerBandY + (villageRandom.nextFloat() * 6.0f - 3.0f);
                float rockSize = 1.4f + (villageRandom.nextFloat() * 1.1f);
                drawCityBuilding(randomRockRegion(villageRandom), rockX, rockY, rockSize, rockSize);
            }
        }
    }

    private TextureRegion randomRockRegion(Random villageRandom) {
        return switch (villageRandom.nextInt(5)) {
            case 0 -> rock01Region;
            case 1 -> rock02Region;
            case 2 -> rock03Region;
            case 3 -> rock04Region;
            default -> rock05Region;
        };
    }

    private void drawVillageConnectorDecor() {
        int midX = tileMap.width() / 2;
        int midY = tileMap.height() / 2;

        float corridorBaseY = midY + OASIS_HALF_HEIGHT + 7.5f;

        drawCityBuilding(decor3Region, midX - 3.4f, corridorBaseY, 1.1f, 1.1f);
        drawCityBuilding(decor3Region, midX + 2.2f, corridorBaseY + 1.6f, 1.1f, 1.1f);
    }

    private void drawCityBuilding(TextureRegion region, float tileX, float tileY, float tileWidth, float tileHeight) {
        float drawX = tileX * MapConfig.TILE_DRAW_SIZE;
        float drawY = tileY * MapConfig.TILE_DRAW_SIZE;
        float width = tileWidth * MapConfig.TILE_DRAW_SIZE;
        float height = tileHeight * MapConfig.TILE_DRAW_SIZE;
        game.batch.draw(region, drawX, drawY, width, height);
    }

    /** Sum of all city blend alphas at a tile — used for city ground rendering. */
    private float cityBlendAlpha(int x, int y) {
        float alpha = 0f;
        for (CitySite city : tileMap.cities()) {
            alpha = Math.max(alpha, city.blendAlpha(x, y));
        }
        return Math.min(1f, alpha);
    }

    // ── Road tile rendering ─────────────────────────────────────────

    /** Delegate to auto-tiling RoadRenderer. */
    private void drawRoads(int startTileX, int endTileX, int startTileY, int endTileY) {
        roadRenderer.drawRoads(game.batch, startTileX, endTileX, startTileY, endTileY);
    }

    private void drawPlayerRoutes(int startTileX, int endTileX, int startTileY, int endTileY) {
        if (game.playerState.getRouteCount() == 0) {
            return;
        }

        game.batch.setColor(0.08f, 0.78f, 0.84f, 0.70f);
        float markerSize = MapConfig.TILE_DRAW_SIZE * 0.42f;
        float offset = (MapConfig.TILE_DRAW_SIZE - markerSize) * 0.5f;
        for (Route route : game.playerState.getRoutes()) {
            if (route.getPath() == null || route.getPath().isEmpty()) {
                continue;
            }

            List<GridPoint2> steps = route.getPath().getSteps();
            int stride = Math.max(1, steps.size() / 180);
            for (int i = 0; i < steps.size(); i += stride) {
                GridPoint2 step = steps.get(i);
                if (step.x < startTileX || step.x > endTileX || step.y < startTileY || step.y > endTileY) {
                    continue;
                }
                game.batch.draw(hudPixel,
                        step.x * MapConfig.TILE_DRAW_SIZE + offset,
                        step.y * MapConfig.TILE_DRAW_SIZE + offset,
                        markerSize,
                        markerSize);
            }
        }
        game.batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawRouteCreationMarker() {
        if (routeOriginCity == null) {
            return;
        }

        float tileSize = MapConfig.TILE_DRAW_SIZE;
        float drawX = (routeOriginCity.centerX - CitySite.CORE_HALF_SIZE) * tileSize;
        float drawY = (routeOriginCity.centerY - CitySite.CORE_HALF_SIZE) * tileSize;
        float size = (CitySite.CORE_HALF_SIZE * 2 + 1) * tileSize;
        float brd = 4f;

        game.batch.setColor(0.12f, 0.90f, 0.92f, 0.85f);
        game.batch.draw(hudPixel, drawX, drawY, size, brd);
        game.batch.draw(hudPixel, drawX, drawY + size - brd, size, brd);
        game.batch.draw(hudPixel, drawX, drawY, brd, size);
        game.batch.draw(hudPixel, drawX + size - brd, drawY, brd, size);
        game.batch.setColor(1f, 1f, 1f, 1f);
    }

    // ── Hover tile highlight ────────────────────────────────────────

    private void drawHoverHighlight() {
        if (buildMode == BuildMode.POINTER) return;
        if (!tileMap.inBounds(hoverTileX, hoverTileY)) return;

        float drawX = hoverTileX * MapConfig.TILE_DRAW_SIZE;
        float drawY = hoverTileY * MapConfig.TILE_DRAW_SIZE;
        float s     = MapConfig.TILE_DRAW_SIZE;
        float brd   = 2f;

        // Choose highlight colour by mode + affordability
        if (buildMode == BuildMode.ROAD) {
            boolean canAfford = game.playerState.canAfford(PlayerState.COST_ROAD);
            game.batch.setColor(canAfford ? 0.2f : 0.9f,
                                canAfford ? 0.9f : 0.2f,
                                0.2f, 0.65f);
        } else { // DEMOLISH
            // Permanent roads: orange "locked" colour; regular roads: red
            boolean isPermanent = tileMap.isPermanentRoad(hoverTileX, hoverTileY);
            if (isPermanent) {
                game.batch.setColor(1.0f, 0.55f, 0.0f, 0.75f); // orange = locked
            } else {
                game.batch.setColor(0.95f, 0.25f, 0.25f, 0.65f); // red = demolishable
            }
        }

        // Draw border around tile using hudPixel
        game.batch.draw(hudPixel, drawX,           drawY,       s,   brd);  // bottom
        game.batch.draw(hudPixel, drawX,           drawY + s - brd, s, brd); // top
        game.batch.draw(hudPixel, drawX,           drawY,       brd, s);    // left
        game.batch.draw(hudPixel, drawX + s - brd, drawY,       brd, s);    // right
        game.batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void render(float delta) {
        handleInput(delta);

        // Advance in-game calendar
        monthTimer += delta;
        if (monthTimer >= MONTH_DURATION_SECONDS) {
            monthTimer -= MONTH_DURATION_SECONDS;
            game.playerState.tick(tileMap.cities().size());
        }

        camera.update();

        ScreenUtils.clear(0.08f, 0.12f, 0.10f, 1f);

        game.batch.setProjectionMatrix(camera.combined);

        float halfWorldWidth = (Gdx.graphics.getWidth() * camera.zoom) / 2f;
        float halfWorldHeight = (Gdx.graphics.getHeight() * camera.zoom) / 2f;

        int startTileX = Math.max(0, (int) ((camera.position.x - halfWorldWidth) / MapConfig.TILE_DRAW_SIZE) - 1);
        int startTileY = Math.max(0, (int) ((camera.position.y - halfWorldHeight) / MapConfig.TILE_DRAW_SIZE) - 1);
        int endTileX = Math.min(tileMap.width() - 1,
                (int) ((camera.position.x + halfWorldWidth) / MapConfig.TILE_DRAW_SIZE) + 1);
        int endTileY = Math.min(tileMap.height() - 1,
                (int) ((camera.position.y + halfWorldHeight) / MapConfig.TILE_DRAW_SIZE) + 1);

        game.batch.begin();

        // Layer 1: Desert base + city ground fade (all cities at once)
        for (int y = startTileY; y <= endTileY; y++) {
            for (int x = startTileX; x <= endTileX; x++) {
                float drawX = x * MapConfig.TILE_DRAW_SIZE;
                float drawY = y * MapConfig.TILE_DRAW_SIZE;

                game.batch.setColor(1f, 1f, 1f, 1f);
                game.batch.draw(desertSandRegion, drawX, drawY, MapConfig.TILE_DRAW_SIZE, MapConfig.TILE_DRAW_SIZE);

                float blendAlpha = cityBlendAlpha(x, y);
                if (blendAlpha > 0f) {
                    game.batch.setColor(1f, 1f, 1f, blendAlpha);
                    game.batch.draw(inhabitantGroundRegion, drawX, drawY, MapConfig.TILE_DRAW_SIZE,
                            MapConfig.TILE_DRAW_SIZE);
                }
            }
        }
        game.batch.setColor(1f, 1f, 1f, 1f);

        // Layer 2: Oasis lake
        for (int y = startTileY; y <= endTileY; y++) {
            for (int x = startTileX; x <= endTileX; x++) {
                if (tileMap.zone(x, y) != ZoneType.OASIS_LAKE) continue;
                float drawX = x * MapConfig.TILE_DRAW_SIZE;
                float drawY = y * MapConfig.TILE_DRAW_SIZE;
                game.batch.draw(oasisLakeRegion, drawX, drawY, MapConfig.TILE_DRAW_SIZE, MapConfig.TILE_DRAW_SIZE);
            }
        }

        // Layer 3: Oasis stone gaps
        for (int y = startTileY; y <= endTileY; y++) {
            for (int x = startTileX; x <= endTileX; x++) {
                if (tileMap.zone(x, y) != ZoneType.OASIS_STONE) continue;
                float drawX = x * MapConfig.TILE_DRAW_SIZE;
                float drawY = y * MapConfig.TILE_DRAW_SIZE;
                game.batch.draw(selectOasisStone(x, y), drawX, drawY, MapConfig.TILE_DRAW_SIZE,
                        MapConfig.TILE_DRAW_SIZE);
            }
        }

        // Layer 3b: Lake coast decor
        for (int y = startTileY; y <= endTileY; y++) {
            for (int x = startTileX; x <= endTileX; x++) {
                if (tileMap.zone(x, y) != ZoneType.LAKE_COAST_DECOR) continue;
                float drawX = x * MapConfig.TILE_DRAW_SIZE;
                float drawY = y * MapConfig.TILE_DRAW_SIZE;
                game.batch.draw(decor5Region, drawX, drawY, MapConfig.TILE_DRAW_SIZE, MapConfig.TILE_DRAW_SIZE);
            }
        }

        // Layer 4: Oasis greenery ring
        for (int y = startTileY; y <= endTileY; y++) {
            for (int x = startTileX; x <= endTileX; x++) {
                if (tileMap.zone(x, y) != ZoneType.OASIS_GREENERY) continue;
                drawOasisFoliage(x, y, selectOasisGreenery(x, y));
            }
        }

        // Layer 5: Oasis trees
        for (int y = startTileY; y <= endTileY; y++) {
            for (int x = startTileX; x <= endTileX; x++) {
                if (tileMap.zone(x, y) != ZoneType.OASIS_TREE) continue;
                drawOasisFoliage(x, y, selectOasisTree(x, y));
            }
        }

        // Layer 6: Roads (player placed)
        drawRoads(startTileX, endTileX, startTileY, endTileY);
        drawPlayerRoutes(startTileX, endTileX, startTileY, endTileY);
        drawRouteCreationMarker();

        // Layer 7: Hover tile highlight (drawn in world space before buildings)
        drawHoverHighlight();

        // Layer 5b: Add exactly three extra big trees around the lake
        drawThreeExtraLakeTrees();

        // Layer 6: Stacked mountain stones (12 base -> 11 mid -> 10 peak)
        drawStackedMountainStones();

        // Layer 7: Connector decor between oasis and village
        drawVillageConnectorDecor();

        // Layer 8: North-side city layout
        drawNorthCity();

        // Layer 8b: South-side city layout
        drawSouthVillage();

        // Layer 8c: Far-out distinct landmarks
        drawWindmills();

        // Layer 9: Directional Labels for City Planning
        drawDirectionalPlanners();
        game.batch.end();

        // Draw HUD for direction
        drawHUD(delta);
    }

    private void drawDirectionalPlanners() {
        int midX = tileMap.width() / 2;
        int midY = tileMap.height() / 2;

        float offset = 28f * MapConfig.TILE_DRAW_SIZE; // 28 tiles out from center
        float centerX = midX * MapConfig.TILE_DRAW_SIZE;
        float centerY = midY * MapConfig.TILE_DRAW_SIZE;

        hudFont.getData().setScale(3.5f); // Scale up text so it's readable when zoomed out

        hudFont.draw(game.batch, "NORTH", centerX, centerY + offset);
        hudFont.draw(game.batch, "SOUTH", centerX, centerY - offset);
        hudFont.draw(game.batch, "EAST", centerX + offset, centerY);
        hudFont.draw(game.batch, "WEST", centerX - offset, centerY);

        hudFont.draw(game.batch, "NORTH-EAST", centerX + offset * 0.7f, centerY + offset * 0.7f);
        hudFont.draw(game.batch, "NORTH-WEST", centerX - offset * 0.7f, centerY + offset * 0.7f);
        hudFont.draw(game.batch, "SOUTH-EAST", centerX + offset * 0.7f, centerY - offset * 0.7f);
        hudFont.draw(game.batch, "SOUTH-WEST", centerX - offset * 0.7f, centerY - offset * 0.7f);

        hudFont.getData().setScale(1.1f); // Reset to HUD scale
    }

    private void drawHUD(float delta) {
        Matrix4 uiMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.setProjectionMatrix(uiMatrix);
        game.batch.begin();

        float mapScaleX = MINIMAP_SIZE_PX / (float) tileMap.width();
        float mapScaleY = MINIMAP_SIZE_PX / (float) tileMap.height();
        float minimapX = MINIMAP_PADDING_PX;
        float minimapY = Gdx.graphics.getHeight() - MINIMAP_PADDING_PX - MINIMAP_SIZE_PX;

        game.batch.setColor(1f, 1f, 1f, 0.95f);
        game.batch.draw(minimapTexture, minimapX, minimapY, MINIMAP_SIZE_PX, MINIMAP_SIZE_PX);
        game.batch.setColor(0f, 0f, 0f, 0.85f);
        game.batch.draw(hudPixel, minimapX - 2, minimapY - 2, MINIMAP_SIZE_PX + 4, 2);
        game.batch.draw(hudPixel, minimapX - 2, minimapY + MINIMAP_SIZE_PX, MINIMAP_SIZE_PX + 4, 2);
        game.batch.draw(hudPixel, minimapX - 2, minimapY - 2, 2, MINIMAP_SIZE_PX + 4);
        game.batch.draw(hudPixel, minimapX + MINIMAP_SIZE_PX, minimapY - 2, 2, MINIMAP_SIZE_PX + 4);

        float cameraTileX = camera.position.x / MapConfig.TILE_DRAW_SIZE;
        float cameraTileY = camera.position.y / MapConfig.TILE_DRAW_SIZE;
        float viewTileWidth = (Gdx.graphics.getWidth() * camera.zoom) / MapConfig.TILE_DRAW_SIZE;
        float viewTileHeight = (Gdx.graphics.getHeight() * camera.zoom) / MapConfig.TILE_DRAW_SIZE;

        float minTileX = clamp(cameraTileX - viewTileWidth / 2f, 0f, tileMap.width());
        float maxTileX = clamp(cameraTileX + viewTileWidth / 2f, 0f, tileMap.width());
        float minTileY = clamp(cameraTileY - viewTileHeight / 2f, 0f, tileMap.height());
        float maxTileY = clamp(cameraTileY + viewTileHeight / 2f, 0f, tileMap.height());

        float viewRectX = minimapX + minTileX * mapScaleX;
        float viewRectY = minimapY + (tileMap.height() - maxTileY) * mapScaleY;
        float viewRectW = Math.max(2f, (maxTileX - minTileX) * mapScaleX);
        float viewRectH = Math.max(2f, (maxTileY - minTileY) * mapScaleY);

        game.batch.setColor(1f, 1f, 1f, 0.95f);
        game.batch.draw(hudPixel, viewRectX, viewRectY, viewRectW, 1.8f);
        game.batch.draw(hudPixel, viewRectX, viewRectY + viewRectH - 1.8f, viewRectW, 1.8f);
        game.batch.draw(hudPixel, viewRectX, viewRectY, 1.8f, viewRectH);
        game.batch.draw(hudPixel, viewRectX + viewRectW - 1.8f, viewRectY, 1.8f, viewRectH);

        float markerX = minimapX + cameraTileX * mapScaleX - MINIMAP_MARKER_SIZE / 2f;
        float markerY = minimapY + (tileMap.height() - cameraTileY) * mapScaleY - MINIMAP_MARKER_SIZE / 2f;
        game.batch.setColor(0.88f, 0.12f, 0.12f, 1f);
        game.batch.draw(hudPixel, markerX, markerY, MINIMAP_MARKER_SIZE, MINIMAP_MARKER_SIZE);
        game.batch.setColor(1f, 1f, 1f, 1f);

        float centerX = (tileMap.width() * MapConfig.TILE_DRAW_SIZE) / 2f;
        float centerY = (tileMap.height() * MapConfig.TILE_DRAW_SIZE) / 2f;
        float dx = centerX - camera.position.x;
        float dy = centerY - camera.position.y;
        float distancePixels = (float) Math.sqrt(dx * dx + dy * dy);
        int distanceTiles = (int) (distancePixels / MapConfig.TILE_DRAW_SIZE);
        float dxTiles = dx / MapConfig.TILE_DRAW_SIZE;
        float dyTiles = dy / MapConfig.TILE_DRAW_SIZE;
        String direction = directionToCenter(dxTiles, dyTiles);

        if (distanceTiles > CitySite.CORE_HALF_SIZE) {
            String text = "City Zone: " + distanceTiles + " tiles " + direction;
            hudFont.draw(game.batch, text, minimapX, minimapY - 12f);
        } else {
            hudFont.draw(game.batch, "City Zone: inside", minimapX, minimapY - 12f);
        }

        // ── Gold / Economy HUD (top-right) ────────────────────────────────────
        PlayerState ps = game.playerState;
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();

        // Dark pill background
        float panelW = 230f;
        float panelH = 52f;
        float panelX = screenW - panelW - MINIMAP_PADDING_PX;
        float panelY = screenH - panelH - MINIMAP_PADDING_PX;

        game.batch.setColor(0.08f, 0.08f, 0.08f, 0.78f);
        game.batch.draw(hudPixel, panelX, panelY, panelW, panelH);

        // Gold border
        float brd = 1.5f;
        game.batch.setColor(0.80f, 0.65f, 0.10f, 0.90f);
        game.batch.draw(hudPixel, panelX,              panelY,              panelW, brd);     // bottom
        game.batch.draw(hudPixel, panelX,              panelY + panelH - brd, panelW, brd);  // top
        game.batch.draw(hudPixel, panelX,              panelY,              brd, panelH);     // left
        game.batch.draw(hudPixel, panelX + panelW - brd, panelY,           brd, panelH);     // right

        // Coin dot
        float dotSize = 10f;
        game.batch.setColor(1f, 0.87f, 0.27f, 1f);
        game.batch.draw(hudPixel, panelX + 10f, panelY + (panelH - dotSize) / 2f, dotSize, dotSize);
        game.batch.setColor(1f, 1f, 1f, 1f);

        // Gold amount
        String goldText = ps.formattedGold() + " g";
        goldFont.setColor(ps.getGold() >= 0 ? new Color(1f, 0.87f, 0.27f, 1f)
                                            : new Color(1f, 0.25f, 0.25f, 1f));
        goldFont.draw(game.batch, goldText,
                panelX + 26f,
                panelY + panelH - 10f);

        // Year / Month line
        hudFont.setColor(0.72f, 0.72f, 0.72f, 1f);
        String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun",
                               "Jul","Aug","Sep","Oct","Nov","Dec"};
        String dateText = "Year " + ps.getYear() + " - " + monthNames[ps.getMonth() - 1];
        hudFont.draw(game.batch, dateText,
                panelX + 26f,
                panelY + 18f);
        hudFont.setColor(Color.WHITE);

        // ── Bottom Toolbar (Build Menu) ────────────────────────────────
        drawToolbar();

        // ── Selected City Panel ────────────────────────────────────────
        drawSelectedCityPanel(screenW, screenH);

        // ── Vehicle Market Dialog ──────────────────────────────────────
        if (vehicleMarketOpen) {
            drawVehicleMarket(screenW, screenH);
        }

        // ── Feedback message (centre-bottom) ───────────────────────────
        if (feedbackTimer > 0f) {
            feedbackTimer -= delta;
            float alpha = Math.min(1f, feedbackTimer / 0.5f);
            hudFont.setColor(1f, 0.35f, 0.35f, alpha);
            com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(hudFont, feedbackMsg);
            float fx = (Gdx.graphics.getWidth() - layout.width) / 2f;
            float fy = BTN_H + TOOLBAR_PADDING * 2 + 28f;
            hudFont.draw(game.batch, feedbackMsg, fx, fy);
            hudFont.setColor(Color.WHITE);
        }

        game.batch.end();
    }

    private void drawSelectedCityPanel(int screenW, int screenH) {
        float panelW = Math.min(300f, Math.max(240f, screenW - 32f));
        float panelH = 180f;
        float panelX = Math.max(16f, screenW - panelW - MINIMAP_PADDING_PX);
        float panelY = screenH - MINIMAP_PADDING_PX - 52f - 12f - panelH;
        if (panelY < BTN_H + TOOLBAR_PADDING * 2 + 18f) {
            panelY = BTN_H + TOOLBAR_PADDING * 2 + 18f;
        }

        game.batch.setColor(0.08f, 0.08f, 0.08f, 0.82f);
        game.batch.draw(hudPixel, panelX, panelY, panelW, panelH);

        float brd = 1.5f;
        game.batch.setColor(0.80f, 0.65f, 0.10f, 0.90f);
        game.batch.draw(hudPixel, panelX, panelY, panelW, brd);
        game.batch.draw(hudPixel, panelX, panelY + panelH - brd, panelW, brd);
        game.batch.draw(hudPixel, panelX, panelY, brd, panelH);
        game.batch.draw(hudPixel, panelX + panelW - brd, panelY, brd, panelH);

        if (selectedCity == null) {
            hudFont.setColor(0.80f, 0.72f, 0.52f, 1f);
            hudFont.draw(game.batch, "No city selected", panelX + 14f, panelY + panelH - 20f);
            hudFont.setColor(0.64f, 0.64f, 0.64f, 1f);
            hudFont.draw(game.batch, "Pointer: click a city", panelX + 14f, panelY + panelH - 48f);
            hudFont.draw(game.batch, "Road: R   Demolish: X", panelX + 14f, panelY + panelH - 72f);
            hudFont.draw(game.batch, "Select city, then C route", panelX + 14f, panelY + panelH - 96f);
            hudFont.draw(game.batch, "Esc returns to pointer", panelX + 14f, panelY + panelH - 120f);
            hudFont.setColor(Color.WHITE);
            game.batch.setColor(1f, 1f, 1f, 1f);
            return;
        }

        hudFont.setColor(1f, 0.87f, 0.27f, 1f);
        hudFont.draw(game.batch, selectedCity.name, panelX + 14f, panelY + panelH - 18f);

        hudFont.setColor(0.82f, 0.82f, 0.78f, 1f);
        hudFont.draw(game.batch, "Type: " + cityTypeLabel(selectedCity.type), panelX + 14f, panelY + panelH - 44f);
        hudFont.draw(game.batch, "Tile: " + selectedCity.centerX + ", " + selectedCity.centerY,
                panelX + 14f, panelY + panelH - 68f);
        hudFont.draw(game.batch, "Demand: " + cityDemandLabel(selectedCity.type), panelX + 14f, panelY + panelH - 92f);
        hudFont.draw(game.batch, "Cargo: " + cityCargoLabel(selectedCity.type), panelX + 14f, panelY + panelH - 116f);
        hudFont.draw(game.batch, "Routes: " + game.playerState.getRouteCount(), panelX + 14f, panelY + panelH - 140f);

        hudFont.setColor(0.66f, 0.83f, 0.82f, 1f);
        String actionText = routeOriginCity == null
                ? "V: market   C: create route"
                : "Route from " + routeOriginCity.name;
        hudFont.draw(game.batch, actionText, panelX + 14f, panelY + 18f);
        hudFont.setColor(Color.WHITE);
        game.batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawVehicleMarket(int screenW, int screenH) {
        float panelW = Math.min(430f, Math.max(320f, screenW - 32f));
        float panelH = 300f;
        float panelX = (screenW - panelW) / 2f;
        float panelY = (screenH - panelH) / 2f;

        game.batch.setColor(0.04f, 0.03f, 0.02f, 0.92f);
        game.batch.draw(hudPixel, panelX, panelY, panelW, panelH);

        float brd = 2f;
        game.batch.setColor(0.80f, 0.65f, 0.10f, 1f);
        game.batch.draw(hudPixel, panelX, panelY, panelW, brd);
        game.batch.draw(hudPixel, panelX, panelY + panelH - brd, panelW, brd);
        game.batch.draw(hudPixel, panelX, panelY, brd, panelH);
        game.batch.draw(hudPixel, panelX + panelW - brd, panelY, brd, panelH);

        String title = selectedCity == null ? "Vehicle Market" : selectedCity.name + " Vehicle Market";
        hudFont.setColor(1f, 0.87f, 0.27f, 1f);
        hudFont.draw(game.batch, title, panelX + 18f, panelY + panelH - 18f);
        hudFont.setColor(0.72f, 0.72f, 0.68f, 1f);
        hudFont.draw(game.batch, "Click a vehicle or press 1-4. Esc closes.", panelX + 18f, panelY + panelH - 42f);

        float rowX = panelX + 18f;
        float rowW = panelW - 36f;
        float rowH = 48f;
        float firstRowY = panelY + panelH - 88f;
        VehicleType[] types = VehicleType.values();
        for (int i = 0; i < types.length; i++) {
            VehicleType type = types[i];
            boolean affordable = game.playerState.canAfford(type.price());
            float rowY = firstRowY - i * (rowH + 8f);

            game.batch.setColor(affordable ? 0.14f : 0.20f, affordable ? 0.13f : 0.08f, 0.07f, 0.96f);
            game.batch.draw(hudPixel, rowX, rowY, rowW, rowH);
            game.batch.setColor(affordable ? 0.62f : 0.50f, affordable ? 0.50f : 0.20f, 0.14f, 0.90f);
            game.batch.draw(hudPixel, rowX, rowY, rowW, 1.5f);
            game.batch.draw(hudPixel, rowX, rowY + rowH - 1.5f, rowW, 1.5f);
            game.batch.draw(hudPixel, rowX, rowY, 1.5f, rowH);
            game.batch.draw(hudPixel, rowX + rowW - 1.5f, rowY, 1.5f, rowH);

            hudFont.setColor(affordable ? Color.WHITE : new Color(0.84f, 0.42f, 0.36f, 1f));
            hudFont.draw(game.batch, (i + 1) + ". " + type.displayName() + " - " + type.price() + "g",
                    rowX + 10f, rowY + rowH - 10f);
            hudFont.setColor(0.72f, 0.72f, 0.68f, 1f);
            String specs = "Cap " + type.capacity() + "  Speed " + type.speed() + "  " + type.role();
            hudFont.draw(game.batch, specs, rowX + 10f, rowY + 18f);
        }

        hudFont.setColor(0.66f, 0.83f, 0.82f, 1f);
        hudFont.draw(game.batch, "Owned vehicles: " + game.playerState.getVehicleCount(),
                panelX + 18f, panelY + 18f);
        hudFont.setColor(Color.WHITE);
        game.batch.setColor(1f, 1f, 1f, 1f);
    }

    private String cityTypeLabel(CitySite.CityType type) {
        return switch (type) {
            case CAPITAL -> "Capital trade hub";
            case NORTH -> "Northern trade town";
            case SOUTH -> "Southern quarry post";
            case EAST -> "Eastern caravan gate";
            case WEST -> "Western oasis market";
        };
    }

    private String cityDemandLabel(CitySite.CityType type) {
        return switch (type) {
            case CAPITAL -> "Stone blocks, tourists";
            case NORTH -> "Cotton, papyrus";
            case SOUTH -> "Gold, stone blocks";
            case EAST -> "Spices, artifacts";
            case WEST -> "Dates, figs";
        };
    }

    private String cityCargoLabel(CitySite.CityType type) {
        return switch (type) {
            case CAPITAL -> "Tourists";
            case NORTH -> "Cotton";
            case SOUTH -> "Gold";
            case EAST -> "Spices";
            case WEST -> "Dates";
        };
    }

    // ── Toolbar drawing ──────────────────────────────────────────

    private void drawToolbar() {
        String[] labels = { "[Esc] Pointer", "[R] Road", "[X] Demolish" };
        int[]    costs  = { 0, PlayerState.COST_ROAD, PlayerState.COST_DEMOLISH };
        BuildMode[] modes = { BuildMode.POINTER, BuildMode.ROAD, BuildMode.DEMOLISH };

        for (int i = 0; i < modes.length; i++) {
            float bx = toolbarButtonX(i);
            float by = TOOLBAR_PADDING;
            boolean active = buildMode == modes[i];
            boolean affordable = costs[i] == 0 || game.playerState.canAfford(costs[i]);

            // Background
            if (active) {
                game.batch.setColor(0.20f, 0.20f, 0.20f, 0.95f);
            } else {
                game.batch.setColor(0.10f, 0.10f, 0.10f, 0.80f);
            }
            game.batch.draw(hudPixel, bx, by, BTN_W, BTN_H);

            // Border: gold if active, grey otherwise
            float brd = 1.5f;
            if (active) {
                game.batch.setColor(0.85f, 0.70f, 0.15f, 1f);
            } else if (!affordable) {
                game.batch.setColor(0.70f, 0.20f, 0.20f, 0.85f);
            } else {
                game.batch.setColor(0.40f, 0.40f, 0.40f, 0.70f);
            }
            game.batch.draw(hudPixel, bx,              by,              BTN_W, brd);
            game.batch.draw(hudPixel, bx,              by + BTN_H - brd, BTN_W, brd);
            game.batch.draw(hudPixel, bx,              by,              brd,   BTN_H);
            game.batch.draw(hudPixel, bx + BTN_W - brd, by,            brd,   BTN_H);
            game.batch.setColor(1f, 1f, 1f, 1f);

            // Label
            hudFont.setColor(active ? new Color(1f, 0.87f, 0.27f, 1f)
                    : (affordable ? Color.WHITE : new Color(0.7f, 0.3f, 0.3f, 1f)));
            hudFont.draw(game.batch, labels[i], bx + 8f, by + BTN_H - 10f);

            // Cost sub-label
            if (costs[i] > 0) {
                hudFont.setColor(affordable ? new Color(0.6f, 0.9f, 0.6f, 1f)
                                           : new Color(0.85f, 0.3f, 0.3f, 1f));
                hudFont.draw(game.batch, costs[i] + "g / tile", bx + 8f, by + BTN_H - 26f);
            }
            hudFont.setColor(Color.WHITE);
        }
    }

    private String directionToCenter(float dxTiles, float dyTiles) {
        float threshold = 0.5f;
        String vertical = dyTiles > threshold ? "north" : (dyTiles < -threshold ? "south" : "");
        String horizontal = dxTiles > threshold ? "east" : (dxTiles < -threshold ? "west" : "");

        if (!vertical.isEmpty() && !horizontal.isEmpty()) {
            return vertical + "-" + horizontal;
        }
        if (!vertical.isEmpty()) {
            return vertical;
        }
        if (!horizontal.isEmpty()) {
            return horizontal;
        }
        return "center";
    }

    @Override
    public void dispose() {
        hudFont.dispose();
        goldFont.dispose();
        minimapTexture.dispose();
        minimapPixmap.dispose();
        hudPixel.dispose();
    }
}
