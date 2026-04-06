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
import com.citopia.world.MapConfig;
import com.citopia.world.TileMap;

public class GameScreen extends ScreenAdapter {

    private static final int INHABITANT_HALF_SIZE = 20;
    private static final int MINIMAP_SIZE_PX = 220;
    private static final int MINIMAP_PADDING_PX = 16;
    private static final float MINIMAP_MARKER_SIZE = 4f;

    private final CitopiaGame game;
    private final OrthographicCamera camera;
    private final ScreenViewport viewport;
    private final TileMap tileMap;

    private final TextureRegion[] groundRegions;
    private final TextureRegion desertSandRegion;
    private final TextureRegion inhabitantGroundRegion;
    // private final TextureRegion infertileLandRegion; // Not used for now
    private final TextureRegion treeRegion;
    private final TextureRegion cartRegion;
    private final TextureRegion[] stoneRegions;
    private final BitmapFont hudFont;
    private final Pixmap minimapPixmap;
    private final Texture minimapTexture;
    private final Texture hudPixel;
    private float pendingScrollY;

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
        this.inhabitantGroundRegion = groundRegions[22];
        // this.infertileLandRegion = safeRegion(AssetId.TERRAIN_CRACKED_HARDPAN, groundRegions[1]); // Not used for now
        this.treeRegion = safeRegion(AssetId.PROP_TREE_MEDIUM, groundRegions[2]);
        this.cartRegion = safeRegion(AssetId.VEHICLE_WOODEN_CART, groundRegions[3]);
        this.stoneRegions = new TextureRegion[] {
            safeRegion(AssetId.TERRAIN_DESERT_STONE_1, groundRegions[4]),
            safeRegion(AssetId.TERRAIN_DESERT_STONE_2, groundRegions[5]),
            safeRegion(AssetId.TERRAIN_DESERT_STONE_3, groundRegions[6]),
            safeRegion(AssetId.TERRAIN_DESERT_STONE_4, groundRegions[7]),
            safeRegion(AssetId.TERRAIN_DESERT_STONE_5, groundRegions[8])
        };

        this.minimapPixmap = new Pixmap(tileMap.width(), tileMap.height(), Pixmap.Format.RGBA8888);
        buildMinimapPixmap();
        this.minimapTexture = new Texture(minimapPixmap);

        Pixmap pixelPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixelPixmap.setColor(Color.WHITE);
        pixelPixmap.fill();
        this.hudPixel = new Texture(pixelPixmap);
        pixelPixmap.dispose();

        camera.position.set(
                (tileMap.width() * MapConfig.TILE_DRAW_SIZE) / 2f,
                (tileMap.height() * MapConfig.TILE_DRAW_SIZE) / 2f,
                0f
        );
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
            camera.zoom = Math.min(2.5f, camera.zoom + 0.8f * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom = Math.max(0.35f, camera.zoom - 0.8f * delta);
        }

        if (pendingScrollY != 0f) {
            camera.zoom = clamp(camera.zoom + pendingScrollY * 0.1f, 0.35f, 2.5f);
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
                0f
        );
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
        if (isInhabitantZone(x, y)) {
            minimapPixmap.setColor(0.68f, 0.57f, 0.34f, 1f);
        } else if (isStoneTile(x, y)) {
            minimapPixmap.setColor(0.55f, 0.52f, 0.46f, 1f);
        } else if (hasTree(x, y)) {
            minimapPixmap.setColor(0.20f, 0.49f, 0.24f, 1f);
        } else {
            minimapPixmap.setColor(0.84f, 0.64f, 0.39f, 1f);
        }

        minimapPixmap.drawPixel(x, tileMap.height() - 1 - y);
    }

    public void refreshMinimapTile(int x, int y) {
        if (x < 0 || y < 0 || x >= tileMap.width() || y >= tileMap.height()) {
            return;
        }

        applyMinimapColor(x, y);
        minimapTexture.draw(minimapPixmap, 0, 0);
    }

    private boolean isInhabitantZone(int x, int y) {
        int midX = tileMap.width() / 2;
        int midY = tileMap.height() / 2;
        return Math.abs(x - midX) <= INHABITANT_HALF_SIZE
            && Math.abs(y - midY) <= INHABITANT_HALF_SIZE;
    }

    private float inhabitantBlendAlpha(int x, int y) {
        int midX = tileMap.width() / 2;
        int midY = tileMap.height() / 2;
        int distanceFromCenter = Math.max(Math.abs(x - midX), Math.abs(y - midY));

        int fullyCityRadius = INHABITANT_HALF_SIZE - 4;
        int fadeEndRadius = INHABITANT_HALF_SIZE + 8;

        if (distanceFromCenter <= fullyCityRadius) {
            return 1f;
        }
        if (distanceFromCenter >= fadeEndRadius) {
            return 0f;
        }

        float t = (distanceFromCenter - fullyCityRadius) / (float) (fadeEndRadius - fullyCityRadius);
        return 1f - t;
    }

    // Infertile land tile logic removed; drought tiles are not placed for now.

    private boolean hasTree(int x, int y) {
        if (isInhabitantZone(x, y) || isStoneTile(x, y)) {
            return false;
        }
        int noise = Math.floorMod((x * 73) ^ (y * 41), 1000);
        return noise < 7;
    }

    private boolean isStoneTile(int x, int y) {
        if (isInhabitantZone(x, y)) {
            return false;
        }

        int noise = Math.floorMod((x * 97) ^ (y * 53), 1000);
        return noise < 20;
    }

    private TextureRegion selectStoneRegion(int x, int y) {
        int index = Math.floorMod((x * 31) ^ (y * 79), stoneRegions.length);
        return stoneRegions[index];
    }

    private void drawCarts(float tileSize) {
        float[][] cartTiles = new float[][] {
                {tileMap.width() / 2f - 12f, tileMap.height() / 2f},
                {tileMap.width() / 2f + 18f, tileMap.height() / 2f},
                {tileMap.width() / 3f, tileMap.height() / 2f - 22f},
                {tileMap.width() / 3f, tileMap.height() / 2f + 26f},
                {tileMap.width() * 0.72f, tileMap.height() * 2f / 3f}
        };

        for (float[] cartTile : cartTiles) {
            float drawX = cartTile[0] * tileSize;
            float drawY = cartTile[1] * tileSize;
            game.batch.draw(cartRegion, drawX, drawY, tileSize * 1.2f, tileSize * 0.7f);
        }
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        camera.update();

        ScreenUtils.clear(0.08f, 0.12f, 0.10f, 1f);

        game.batch.setProjectionMatrix(camera.combined);

        float halfWorldWidth = (Gdx.graphics.getWidth() * camera.zoom) / 2f;
        float halfWorldHeight = (Gdx.graphics.getHeight() * camera.zoom) / 2f;

        int startTileX = Math.max(0, (int) ((camera.position.x - halfWorldWidth) / MapConfig.TILE_DRAW_SIZE) - 1);
        int startTileY = Math.max(0, (int) ((camera.position.y - halfWorldHeight) / MapConfig.TILE_DRAW_SIZE) - 1);
        int endTileX = Math.min(tileMap.width() - 1, (int) ((camera.position.x + halfWorldWidth) / MapConfig.TILE_DRAW_SIZE) + 1);
        int endTileY = Math.min(tileMap.height() - 1, (int) ((camera.position.y + halfWorldHeight) / MapConfig.TILE_DRAW_SIZE) + 1);

        game.batch.begin();

        // Layer 1: Ground + center city fade blend
        for (int y = startTileY; y <= endTileY; y++) {
            for (int x = startTileX; x <= endTileX; x++) {
                float drawX = x * MapConfig.TILE_DRAW_SIZE;
                float drawY = y * MapConfig.TILE_DRAW_SIZE;

                game.batch.setColor(1f, 1f, 1f, 1f);
                game.batch.draw(desertSandRegion, drawX, drawY, MapConfig.TILE_DRAW_SIZE, MapConfig.TILE_DRAW_SIZE);

                float blendAlpha = inhabitantBlendAlpha(x, y);
                if (blendAlpha > 0f) {
                    game.batch.setColor(1f, 1f, 1f, blendAlpha);
                    game.batch.draw(inhabitantGroundRegion, drawX, drawY, MapConfig.TILE_DRAW_SIZE, MapConfig.TILE_DRAW_SIZE);
                }
            }
        }
        game.batch.setColor(1f, 1f, 1f, 1f);

        // ...existing code...

        // Layer 3: Stones
        for (int y = startTileY; y <= endTileY; y++) {
            for (int x = startTileX; x <= endTileX; x++) {
                if (!isStoneTile(x, y)) {
                    continue;
                }
                float drawX = x * MapConfig.TILE_DRAW_SIZE;
                float drawY = y * MapConfig.TILE_DRAW_SIZE;
                game.batch.draw(selectStoneRegion(x, y), drawX, drawY, MapConfig.TILE_DRAW_SIZE, MapConfig.TILE_DRAW_SIZE);
            }
        }

        // Layer 4: Trees
        for (int y = startTileY; y <= endTileY; y++) {
            for (int x = startTileX; x <= endTileX; x++) {
                if (!hasTree(x, y)) {
                    continue;
                }
                float drawX = x * MapConfig.TILE_DRAW_SIZE;
                float drawY = y * MapConfig.TILE_DRAW_SIZE;
                game.batch.draw(treeRegion, drawX, drawY, MapConfig.TILE_DRAW_SIZE, MapConfig.TILE_DRAW_SIZE);
            }
        }

        // Layer 5: Carts
        drawCarts(MapConfig.TILE_DRAW_SIZE);
        game.batch.end();

        // Draw HUD for direction
        drawHUD();
    }

    private void drawHUD() {
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

        if (distanceTiles > INHABITANT_HALF_SIZE) {
            String text = "City Zone: " + distanceTiles + " tiles " + direction;
            hudFont.draw(game.batch, text, minimapX, minimapY - 12f);
        } else {
            hudFont.draw(game.batch, "City Zone: inside", minimapX, minimapY - 12f);
        }

        game.batch.end();
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
        minimapTexture.dispose();
        minimapPixmap.dispose();
        hudPixel.dispose();
    }
}
