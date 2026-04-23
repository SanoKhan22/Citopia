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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.citopia.CitopiaGame;
import com.citopia.assets.AssetId;
import com.citopia.model.PlayerState;
import com.citopia.world.CitySite;
import com.citopia.world.MapConfig;
import com.citopia.world.TileMap;
import com.citopia.world.ZoneType;
import com.badlogic.gdx.audio.Music;
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
    private static final float BTN_W    = 58f;
    private static final float BTN_H    = 58f;
    private static final float BTN_GAP  = 6f;
    private static final float TOOLBAR_PADDING = 8f;

    private final CitopiaGame game;
    private final OrthographicCamera camera;
    private final ScreenViewport viewport;
    private final TileMap tileMap;

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
    private final TextureRegion uiGoldIcon;
    private final com.badlogic.gdx.graphics.g2d.NinePatch uiPanel;
    private final com.badlogic.gdx.graphics.g2d.NinePatch uiButton;
    private final com.badlogic.gdx.graphics.g2d.NinePatch uiButtonActive;
    private final TextureRegion uiIconPointer;
    private final TextureRegion uiIconRoad;
    private final TextureRegion uiIconDemolish;
    
    private final BitmapFont goldFont;   // slightly larger font for gold counter
    private float displayGold; // for rolling number animation
    
    private final Music bgm;
    private boolean isMusicMuted = false;
    
    private float pendingScrollY;

    // ── Build menu state ─────────────────────────────────────────
    private BuildMode buildMode = BuildMode.POINTER;
    private int hoverTileX = -1;   // world tile the mouse is over
    private int hoverTileY = -1;
    /** Feedback message shown bottom-centre (e.g. "Not enough gold!"). Fades over time. */
    private String feedbackMsg  = "";
    private float  feedbackTimer = 0f;
    private static final float FEEDBACK_DURATION = 2.5f;

    // Economy: in-game time accumulator (1 month = 30 real seconds at normal speed)
    private static final float MONTH_DURATION_SECONDS = 30f;
    private float monthTimer = 0f;

    // Animation timer (accumulated real-time for visual effects)
    private float animTime = 0f;

    public GameScreen(CitopiaGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new ScreenViewport(camera);
        this.tileMap = new TileMap(MapConfig.MAP_WIDTH_TILES, MapConfig.MAP_HEIGHT_TILES, 42L);
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
        this.roadRegion     = game.assets.region("road_desert_dirt");
        this.roadRenderer   = new RoadRenderer(roadRegion, tileMap);

        this.minimapPixmap = new Pixmap(tileMap.width(), tileMap.height(), Pixmap.Format.RGBA8888);
        buildMinimapPixmap();
        this.minimapTexture = new Texture(minimapPixmap);

        // Generic white pixel for solid colours
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        this.hudPixel = new Texture(pm);
        pm.dispose();

        // ── Load new UI assets ─────────────────────────────────────
        this.uiGoldIcon = game.assets.region("UI/ui_gold_icon");
        
        // Panels and buttons are 1024x1024, so 100px margins should preserve borders
        TextureRegion panelReg = game.assets.region("UI/ui_panel");
        this.uiPanel = new com.badlogic.gdx.graphics.g2d.NinePatch(panelReg, 100, 100, 100, 100);
        
        TextureRegion btnReg = game.assets.region("UI/ui_button");
        this.uiButton = new com.badlogic.gdx.graphics.g2d.NinePatch(btnReg, 80, 80, 80, 80);
        
        TextureRegion btnActReg = game.assets.region("UI/ui_button_active");
        this.uiButtonActive = new com.badlogic.gdx.graphics.g2d.NinePatch(btnActReg, 80, 80, 80, 80);

        this.uiIconPointer = game.assets.region("UI/ui_icon_pointer");
        this.uiIconRoad = game.assets.region("UI/ui_icon_road");
        this.uiIconDemolish = game.assets.region("UI/ui_icon_demolish");

        this.goldFont = new BitmapFont();
        this.goldFont.getData().setScale(1.4f);
        this.goldFont.setColor(1f, 0.87f, 0.27f, 1f); // warm gold colour
        
        this.displayGold = game.playerState.getGold();

        this.bgm = game.assets.music("audio/desertBG.wav");
        this.bgm.setLooping(true);
        this.bgm.setVolume(1.0f);
        this.bgm.play();

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
                switch (keycode) {
                    case Input.Keys.R -> buildMode = BuildMode.ROAD;
                    case Input.Keys.X -> buildMode = BuildMode.DEMOLISH;
                    case Input.Keys.ESCAPE -> buildMode = BuildMode.POINTER;
                    case Input.Keys.M -> {
                        isMusicMuted = !isMusicMuted;
                        if (isMusicMuted) {
                            bgm.pause();
                        } else {
                            bgm.play();
                        }
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
            default -> { /* POINTER – no action */ }
        }
    }

    private void showFeedback(String msg) {
        feedbackMsg   = msg;
        feedbackTimer = FEEDBACK_DURATION;
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
        animTime += delta;

        // Advance in-game calendar
        monthTimer += delta;
        if (monthTimer >= MONTH_DURATION_SECONDS) {
            monthTimer -= MONTH_DURATION_SECONDS;
            game.playerState.tick(tileMap.cities().size());
        }

        // Rolling numbers logic for gold
        float targetGold = game.playerState.getGold();
        if (displayGold != targetGold) {
            float diff = targetGold - displayGold;
            // Roll by at least 15 per second, or larger if the difference is huge
            float rate = Math.max(15f, Math.abs(diff) * 2.5f);
            if (diff > 0) {
                displayGold += rate * delta;
                if (displayGold > targetGold) displayGold = targetGold;
            } else {
                displayGold -= rate * delta;
                if (displayGold < targetGold) displayGold = targetGold;
            }
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

        // Layer 10: City name labels
        drawCityLabels();

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

    /**
     * Draws animated, visually rich city name labels centered above each city.
     * Features: floating bob, pulsing scale, gold shimmer for capital,
     * dark banner plate behind text, and decorative accent lines.
     */
    private void drawCityLabels() {
        float labelOffsetY = (CitySite.CORE_HALF_SIZE + 5) * MapConfig.TILE_DRAW_SIZE;

        for (int i = 0; i < tileMap.cities().size(); i++) {
            CitySite city = tileMap.cities().get(i);
            boolean isCapital = city.type == CitySite.CityType.CAPITAL;

            // ── Per-city phase offset so labels don't all bob in sync ──
            float phase = animTime + i * 1.3f;

            // ── Floating bob: gentle sine wave vertical movement ──
            float bobAmplitude = MapConfig.TILE_DRAW_SIZE * 0.6f;
            float bobY = (float) Math.sin(phase * 1.2f) * bobAmplitude;

            // ── Pulsing scale: subtle breathing effect ──
            float baseScale = isCapital ? 6.0f : 4.5f;
            float pulseScale = baseScale + 0.3f * (float) Math.sin(phase * 1.8f);
            hudFont.getData().setScale(pulseScale);

            String name = city.name.toUpperCase();
            com.badlogic.gdx.graphics.g2d.GlyphLayout layout =
                    new com.badlogic.gdx.graphics.g2d.GlyphLayout(hudFont, name);

            float worldX = city.centerX * MapConfig.TILE_DRAW_SIZE + MapConfig.TILE_DRAW_SIZE * 0.5f;
            float worldY = city.centerY * MapConfig.TILE_DRAW_SIZE + labelOffsetY + bobY;
            float textX = worldX - layout.width / 2f;
            float textY = worldY + layout.height / 2f;

            // ── Dark banner plate behind text ──
            float padX = layout.width * 0.18f;
            float padY = layout.height * 0.55f;
            float bannerX = textX - padX;
            float bannerY = textY - layout.height - padY;
            float bannerW = layout.width + padX * 2f;
            float bannerH = layout.height + padY * 2f;

            game.batch.setColor(0.05f, 0.03f, 0.01f, 0.65f);
            game.batch.draw(hudPixel, bannerX, bannerY, bannerW, bannerH);

            // ── Decorative accent lines on sides of banner ──
            float lineThickness = 2.5f;
            float lineInset = padX * 0.3f;
            if (isCapital) {
                // Gold accent borders for capital
                float shimmer = 0.8f + 0.2f * (float) Math.sin(phase * 3.0f);
                game.batch.setColor(shimmer, 0.75f * shimmer, 0.15f, 0.9f);
            } else {
                game.batch.setColor(0.85f, 0.78f, 0.62f, 0.6f);
            }
            // Top line
            game.batch.draw(hudPixel, bannerX + lineInset, bannerY + bannerH - lineThickness,
                    bannerW - lineInset * 2f, lineThickness);
            // Bottom line
            game.batch.draw(hudPixel, bannerX + lineInset, bannerY,
                    bannerW - lineInset * 2f, lineThickness);
            // Left line
            game.batch.draw(hudPixel, bannerX, bannerY + lineInset,
                    lineThickness, bannerH - lineInset * 2f);
            // Right line
            game.batch.draw(hudPixel, bannerX + bannerW - lineThickness, bannerY + lineInset,
                    lineThickness, bannerH - lineInset * 2f);

            // ── Outer glow pass (slightly offset in 4 directions) ──
            float glowDist = 2.5f;
            if (isCapital) {
                float glow = 0.4f + 0.15f * (float) Math.sin(phase * 2.5f);
                hudFont.setColor(1f, 0.7f, 0.1f, glow);
            } else {
                hudFont.setColor(0.9f, 0.85f, 0.7f, 0.25f);
            }
            hudFont.draw(game.batch, name, textX + glowDist, textY);
            hudFont.draw(game.batch, name, textX - glowDist, textY);
            hudFont.draw(game.batch, name, textX, textY + glowDist);
            hudFont.draw(game.batch, name, textX, textY - glowDist);

            // ── Drop shadow ──
            hudFont.setColor(0f, 0f, 0f, 0.75f);
            hudFont.draw(game.batch, name, textX + 4f, textY - 4f);

            // ── Main text with shimmer color ──
            if (isCapital) {
                // Gold shimmer cycling through warm tones
                float t = (float) Math.sin(phase * 2.0f) * 0.5f + 0.5f;
                float r = 1.0f;
                float g = 0.78f + 0.12f * t;
                float b = 0.15f + 0.15f * t;
                hudFont.setColor(r, g, b, 1f);
            } else {
                // Subtle warm white with gentle pulse
                float t = 0.9f + 0.1f * (float) Math.sin(phase * 1.5f);
                hudFont.setColor(t, t * 0.97f, t * 0.90f, 1f);
            }
            hudFont.draw(game.batch, name, textX, textY);
        }

        // Reset font state
        game.batch.setColor(1f, 1f, 1f, 1f);
        hudFont.setColor(1f, 1f, 1f, 1f);
        hudFont.getData().setScale(1.1f);
    }

    private void drawHUD(float delta) {
        Matrix4 uiMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.setProjectionMatrix(uiMatrix);
        game.batch.begin();

        float mapScaleX = MINIMAP_SIZE_PX / (float) tileMap.width();
        float mapScaleY = MINIMAP_SIZE_PX / (float) tileMap.height();
        float minimapX = MINIMAP_PADDING_PX;
        float minimapY = Gdx.graphics.getHeight() - MINIMAP_PADDING_PX - MINIMAP_SIZE_PX;

        game.batch.setColor(1f, 1f, 1f, 1f);
        float mapPad = 12f;
        uiPanel.draw(game.batch, minimapX - mapPad, minimapY - mapPad, 
                     MINIMAP_SIZE_PX + mapPad * 2, MINIMAP_SIZE_PX + mapPad * 2);
        game.batch.draw(minimapTexture, minimapX, minimapY, MINIMAP_SIZE_PX, MINIMAP_SIZE_PX);

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

        // Decorative Parchment/Wood Panel background
        float panelW = 260f;
        float panelH = 80f;
        float panelX = screenW - panelW - MINIMAP_PADDING_PX;
        float panelY = screenH - panelH - MINIMAP_PADDING_PX;

        game.batch.setColor(1f, 1f, 1f, 1f);
        uiPanel.draw(game.batch, panelX, panelY, panelW, panelH);

        // Gold coin icon (animating gently)
        float iconSize = 42f;
        float iconBob = (float) Math.sin(animTime * 2f) * 3f;
        game.batch.draw(uiGoldIcon, panelX + 15f, panelY + (panelH - iconSize) / 2f + iconBob, iconSize, iconSize);

        // Gold amount
        String goldText = String.format("%,d g", (int) displayGold);
        goldFont.setColor(displayGold >= 0 ? new Color(1f, 0.87f, 0.27f, 1f)
                                           : new Color(1f, 0.25f, 0.25f, 1f));
        goldFont.draw(game.batch, goldText,
                panelX + iconSize + 25f,
                panelY + panelH - 20f);

        // Year / Month line
        // Darkened text to look like ink on parchment/wood
        hudFont.setColor(0.35f, 0.25f, 0.15f, 1f);
        String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun",
                               "Jul","Aug","Sep","Oct","Nov","Dec"};
        String dateText = "Year " + ps.getYear() + " - " + monthNames[ps.getMonth() - 1];
        hudFont.draw(game.batch, dateText,
                panelX + iconSize + 25f,
                panelY + 30f);
        hudFont.setColor(Color.WHITE);

        // Music Mute status (top-left)
        String musicStatus = "Music: " + (isMusicMuted ? "OFF" : "ON") + " [M]";
        hudFont.draw(game.batch, musicStatus, 20f, screenH - 20f);

        // ── Bottom Toolbar (Build Menu) ────────────────────────────────
        drawToolbar();

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

    // ── Toolbar drawing ──────────────────────────────────────────

    private void drawToolbar() {
        TextureRegion[] icons = { uiIconPointer, uiIconRoad, uiIconDemolish };
        int[]    costs  = { 0, PlayerState.COST_ROAD, PlayerState.COST_DEMOLISH };
        BuildMode[] modes = { BuildMode.POINTER, BuildMode.ROAD, BuildMode.DEMOLISH };

        for (int i = 0; i < modes.length; i++) {
            float bx = toolbarButtonX(i);
            float by = TOOLBAR_PADDING;
            boolean active = buildMode == modes[i];
            boolean affordable = costs[i] == 0 || game.playerState.canAfford(costs[i]);

            // Background and border
            game.batch.setColor(1f, 1f, 1f, 1f);
            if (active) {
                uiButtonActive.draw(game.batch, bx, by, BTN_W, BTN_H);
            } else {
                if (!affordable) {
                    game.batch.setColor(0.9f, 0.4f, 0.4f, 1f); // Tint red for unaffordable
                }
                uiButton.draw(game.batch, bx, by, BTN_W, BTN_H);
                game.batch.setColor(1f, 1f, 1f, 1f); // Revert tint
            }

            // Draw Icon
            float iconSize = 40f;
            float ix = bx + (BTN_W - iconSize) / 2f;
            float iy = by + (BTN_H - iconSize) / 2f;
            
            if (!affordable) {
                game.batch.setColor(0.5f, 0.3f, 0.3f, 1f);
            }
            game.batch.draw(icons[i], ix, iy, iconSize, iconSize);
            game.batch.setColor(1f, 1f, 1f, 1f);
            
            // Draw shortcut key in top left corner
            hudFont.getData().setScale(0.8f);
            String[] shortcuts = {"Esc", "R", "X"};
            hudFont.setColor(0.8f, 0.8f, 0.8f, 1f);
            hudFont.draw(game.batch, shortcuts[i], bx + 4f, by + BTN_H - 4f);
            hudFont.getData().setScale(1f);
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
