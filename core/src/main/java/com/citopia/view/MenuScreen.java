package com.citopia.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.citopia.CitopiaGame;
import com.citopia.assets.AssetId;

public class MenuScreen extends ScreenAdapter {

    private static final String LOGO_PATH = "images/ui/citopia_logo.png";
    private static final Color DESERT_SKY_TOP = new Color(0.10f, 0.20f, 0.26f, 1f);
    private static final Color DESERT_SKY_BOTTOM = new Color(0.56f, 0.37f, 0.18f, 1f);
    private static final Color GOLD = new Color(0.83f, 0.69f, 0.22f, 1f);
    private static final Color TEAL = new Color(0.00f, 0.50f, 0.50f, 1f);
    private static final Color SAND = new Color(0.80f, 0.52f, 0.28f, 1f);
    private static final Color PAPYRUS = new Color(0.87f, 0.76f, 0.52f, 1f);
    private static final Color PAPYRUS_DARK = new Color(0.34f, 0.22f, 0.12f, 1f);
    private static final Color NIGHT_INK = new Color(0.07f, 0.10f, 0.11f, 1f);
    private static final Color PANEL_SHADOW = new Color(0.04f, 0.03f, 0.02f, 0.72f);
    private static final Color PANEL_FILL = new Color(0.17f, 0.10f, 0.05f, 0.88f);
    private static final Color DISABLED = new Color(0.42f, 0.37f, 0.27f, 1f);

    private static final float BUTTON_WIDTH = 330f;
    private static final float BUTTON_HEIGHT = 54f;
    private static final float BUTTON_GAP = 14f;

    private final CitopiaGame game;
    private final OrthographicCamera camera;
    private final ScreenViewport viewport;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout;
    private final Vector3 pointerPosition;
    private final MenuButton[] buttons;
    private final TextureRegion desertSandRegion;
    private final TextureRegion palmRegion;
    private final TextureRegion logoRegion;
    private Texture logoTexture;
    private String statusText = "Nile trade awaits.";
    private MenuButton hoveredButton;

    public MenuScreen(CitopiaGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new ScreenViewport(camera);
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.glyphLayout = new GlyphLayout();
        this.pointerPosition = new Vector3();
        this.buttons = new MenuButton[] {
            new MenuButton("New Game", true, MenuAction.NEW_GAME),
            new MenuButton("Load Game", true, MenuAction.LOAD_GAME),
            new MenuButton("Quit", true, MenuAction.QUIT)
        };
        this.desertSandRegion = safeRegion(AssetId.TERRAIN_DESERT_SAND, AssetId.TILE_GROUND_01);
        this.palmRegion = safeRegion(AssetId.PROP_DATE_PALMS, AssetId.PROP_TREE_MEDIUM);
        this.logoRegion = loadLogoRegion();
    }

    @Override
    public void show() {
        layoutButtons();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                updateHover(screenX, screenY);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.LEFT) {
                    return false;
                }
                updateHover(screenX, screenY);
                activate(hoveredButton);
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    game.startNewGame();
                    return true;
                }
                if (keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        layoutButtons();
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        camera.update();
        layoutButtons();

        ScreenUtils.clear(DESERT_SKY_TOP);

        shapeRenderer.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(camera.combined);

        drawBackdrop();
        drawPyramidHorizon();
        drawMenuPanel();
        drawText();
    }

    private TextureRegion safeRegion(AssetId preferred, AssetId fallback) {
        try {
            return game.assets.region(preferred);
        } catch (IllegalArgumentException exception) {
            return game.assets.region(fallback);
        }
    }

    private TextureRegion loadLogoRegion() {
        FileHandle logoFile = Gdx.files.internal(LOGO_PATH);
        if (!logoFile.exists()) {
            return null;
        }

        Pixmap pixmap = new Pixmap(logoFile);
        int backgroundPixel = pixmap.getPixel(0, 0);
        int minX = pixmap.getWidth();
        int minY = pixmap.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                if (!isBackgroundPixel(pixmap.getPixel(x, y), backgroundPixel)) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            pixmap.dispose();
            return null;
        }

        int trimPadding = 18;
        minX = Math.max(0, minX - trimPadding);
        minY = Math.max(0, minY - trimPadding);
        maxX = Math.min(pixmap.getWidth() - 1, maxX + trimPadding);
        maxY = Math.min(pixmap.getHeight() - 1, maxY + trimPadding);

        logoTexture = new Texture(pixmap);
        logoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return new TextureRegion(logoTexture, minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private boolean isBackgroundPixel(int pixel, int backgroundPixel) {
        int redDelta = Math.abs(((pixel >>> 24) & 0xFF) - ((backgroundPixel >>> 24) & 0xFF));
        int greenDelta = Math.abs(((pixel >>> 16) & 0xFF) - ((backgroundPixel >>> 16) & 0xFF));
        int blueDelta = Math.abs(((pixel >>> 8) & 0xFF) - ((backgroundPixel >>> 8) & 0xFF));
        int alpha = pixel & 0xFF;
        return alpha < 8 || redDelta + greenDelta + blueDelta < 34;
    }

    private void layoutButtons() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float width = Math.min(BUTTON_WIDTH, Math.max(240f, worldWidth - 56f));
        float startY = Math.max(72f, worldHeight * 0.27f);
        float x = (worldWidth - width) / 2f;

        for (int i = 0; i < buttons.length; i++) {
            float y = startY - i * (BUTTON_HEIGHT + BUTTON_GAP);
            buttons[i].bounds.set(x, y, width, BUTTON_HEIGHT);
        }
    }

    private void drawBackdrop() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float horizonY = worldHeight * 0.38f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0f, 0f, worldWidth, worldHeight, DESERT_SKY_BOTTOM, DESERT_SKY_BOTTOM, DESERT_SKY_TOP, DESERT_SKY_TOP);
        shapeRenderer.setColor(0.95f, 0.70f, 0.24f, 0.55f);
        shapeRenderer.circle(worldWidth * 0.74f, worldHeight * 0.74f, 58f);
        shapeRenderer.setColor(TEAL.r, TEAL.g, TEAL.b, 0.30f);
        shapeRenderer.rect(worldWidth * 0.80f, 0f, worldWidth * 0.09f, horizonY + 30f);
        shapeRenderer.setColor(TEAL.r, TEAL.g, TEAL.b, 0.45f);
        shapeRenderer.rect(worldWidth * 0.83f, 0f, worldWidth * 0.04f, horizonY + 42f);
        shapeRenderer.end();

        game.batch.begin();
        game.batch.setColor(0.94f, 0.70f, 0.36f, 0.82f);
        for (float y = 0f; y < horizonY + 92f; y += 64f) {
            for (float x = -32f; x < worldWidth + 64f; x += 64f) {
                game.batch.draw(desertSandRegion, x, y, 64f, 64f);
            }
        }
        game.batch.setColor(1f, 1f, 1f, 1f);

        float palmBaseY = Math.max(16f, horizonY - 26f);
        game.batch.setColor(0.82f, 0.64f, 0.33f, 0.88f);
        game.batch.draw(palmRegion, worldWidth * 0.10f, palmBaseY, 116f, 122f);
        game.batch.draw(palmRegion, worldWidth * 0.88f - 74f, palmBaseY - 10f, 92f, 108f);
        game.batch.setColor(1f, 1f, 1f, 1f);
        game.batch.end();
    }

    private void drawPyramidHorizon() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float baseY = worldHeight * 0.36f;
        float centerX = worldWidth * 0.50f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(NIGHT_INK.r, NIGHT_INK.g, NIGHT_INK.b, 0.55f);
        shapeRenderer.triangle(centerX - 310f, baseY, centerX - 138f, baseY, centerX - 224f, baseY + 142f);
        shapeRenderer.triangle(centerX - 164f, baseY, centerX + 116f, baseY, centerX - 24f, baseY + 222f);
        shapeRenderer.triangle(centerX + 76f, baseY, centerX + 312f, baseY, centerX + 194f, baseY + 172f);

        shapeRenderer.setColor(GOLD.r, GOLD.g, GOLD.b, 0.14f);
        shapeRenderer.triangle(centerX - 24f, baseY + 222f, centerX + 116f, baseY, centerX + 14f, baseY);

        drawStepPyramid(centerX - 390f, baseY - 6f, 150f, 82f);
        shapeRenderer.end();
    }

    private void drawStepPyramid(float x, float y, float width, float height) {
        int steps = 5;
        float stepHeight = height / steps;
        for (int i = 0; i < steps; i++) {
            float inset = i * width * 0.08f;
            shapeRenderer.rect(x + inset, y + i * stepHeight, width - inset * 2f, stepHeight + 1f);
        }
    }

    private void drawMenuPanel() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float panelWidth = Math.min(470f, Math.max(290f, worldWidth - 42f));
        float panelHeight = Math.min(430f, Math.max(330f, worldHeight - 120f));
        float panelX = (worldWidth - panelWidth) / 2f;
        float panelY = Math.max(34f, worldHeight * 0.10f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(PANEL_SHADOW);
        shapeRenderer.rect(panelX + 8f, panelY - 8f, panelWidth, panelHeight);
        shapeRenderer.setColor(PANEL_FILL);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.setColor(GOLD);
        shapeRenderer.rect(panelX, panelY + panelHeight - 5f, panelWidth, 5f);
        shapeRenderer.rect(panelX, panelY, panelWidth, 5f);
        shapeRenderer.rect(panelX, panelY, 5f, panelHeight);
        shapeRenderer.rect(panelX + panelWidth - 5f, panelY, 5f, panelHeight);

        drawHieroglyphicBand(panelX + 24f, panelY + panelHeight - 54f, panelWidth - 48f);

        for (MenuButton button : buttons) {
            drawButton(button);
        }
        shapeRenderer.end();
    }

    private void drawHieroglyphicBand(float x, float y, float width) {
        shapeRenderer.setColor(GOLD.r, GOLD.g, GOLD.b, 0.88f);
        shapeRenderer.rect(x, y, width, 2f);
        shapeRenderer.rect(x, y - 23f, width, 2f);

        float cursorX = x + 10f;
        while (cursorX < x + width - 18f) {
            shapeRenderer.setColor(PAPYRUS.r, PAPYRUS.g, PAPYRUS.b, 0.64f);
            shapeRenderer.rect(cursorX, y - 17f, 8f, 12f);
            shapeRenderer.setColor(TEAL.r, TEAL.g, TEAL.b, 0.76f);
            shapeRenderer.rect(cursorX + 12f, y - 18f, 4f, 14f);
            shapeRenderer.setColor(SAND.r, SAND.g, SAND.b, 0.82f);
            shapeRenderer.rect(cursorX + 20f, y - 14f, 12f, 6f);
            cursorX += 46f;
        }
    }

    private void drawButton(MenuButton button) {
        boolean hovered = button == hoveredButton;
        Color border = hovered ? GOLD : SAND;

        if (hovered) {
            shapeRenderer.setColor(TEAL.r, TEAL.g, TEAL.b, 0.82f);
        } else {
            shapeRenderer.setColor(PAPYRUS_DARK.r, PAPYRUS_DARK.g, PAPYRUS_DARK.b, 0.92f);
        }
        shapeRenderer.rect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height);
        shapeRenderer.setColor(border);
        shapeRenderer.rect(button.bounds.x, button.bounds.y + button.bounds.height - 2f, button.bounds.width, 2f);
        shapeRenderer.rect(button.bounds.x, button.bounds.y, button.bounds.width, 2f);
        shapeRenderer.rect(button.bounds.x, button.bounds.y, 2f, button.bounds.height);
        shapeRenderer.rect(button.bounds.x + button.bounds.width - 2f, button.bounds.y, 2f, button.bounds.height);

        float iconX = button.bounds.x + 18f;
        float iconY = button.bounds.y + 16f;
        shapeRenderer.setColor(hovered ? GOLD : PAPYRUS);
        shapeRenderer.rect(iconX, iconY, 8f, 22f);
        shapeRenderer.rect(iconX + 14f, iconY + 7f, 18f, 8f);
        shapeRenderer.rect(iconX + 38f, iconY + 2f, 6f, 18f);
    }

    private void drawText() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float panelTop = Math.max(34f, worldHeight * 0.10f) + Math.min(430f, Math.max(330f, worldHeight - 120f));

        game.batch.begin();
        if (logoRegion != null) {
            drawLogo(worldWidth, panelTop);
            drawCentered("Egyptian Transport Empire", worldWidth / 2f, panelTop - 190f, 1.02f, PAPYRUS);
        } else {
            drawCentered("CITOPIA", worldWidth / 2f, panelTop - 86f, 3.0f, GOLD);
            drawCentered("Egyptian Transport Empire", worldWidth / 2f, panelTop - 122f, 1.18f, PAPYRUS);
            drawCentered("Cairo  Alexandria  Thebes  Aswan", worldWidth / 2f, panelTop - 154f, 0.92f, SAND);
        }

        for (MenuButton button : buttons) {
            Color textColor = button.enabled ? PAPYRUS : DISABLED;
            if (button == hoveredButton && button.enabled) {
                textColor = Color.WHITE;
            }
            drawCentered(button.label, button.bounds.x + button.bounds.width / 2f, button.bounds.y + 35f, 1.15f, textColor);
        }

        drawCentered(statusText, worldWidth / 2f, Math.max(28f, buttons[buttons.length - 1].bounds.y - 28f), 0.86f, PAPYRUS);
        game.batch.end();
    }

    private void drawLogo(float worldWidth, float panelTop) {
        float maxLogoWidth = Math.min(worldWidth * 0.48f, 430f);
        float maxLogoHeight = 150f;
        float logoAspect = logoRegion.getRegionWidth() / (float) logoRegion.getRegionHeight();
        float logoWidth = maxLogoWidth;
        float logoHeight = logoWidth / logoAspect;

        if (logoHeight > maxLogoHeight) {
            logoHeight = maxLogoHeight;
            logoWidth = logoHeight * logoAspect;
        }

        float logoX = (worldWidth - logoWidth) / 2f;
        float logoY = panelTop - 170f;
        game.batch.setColor(1f, 1f, 1f, 1f);
        game.batch.draw(logoRegion, logoX, logoY, logoWidth, logoHeight);
    }

    private void drawCentered(String text, float centerX, float baselineY, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        glyphLayout.setText(font, text);
        font.draw(game.batch, glyphLayout, centerX - glyphLayout.width / 2f, baselineY);
    }

    private void updateHover(int screenX, int screenY) {
        pointerPosition.set(screenX, screenY, 0f);
        viewport.unproject(pointerPosition);
        hoveredButton = null;
        for (MenuButton button : buttons) {
            if (button.enabled && button.bounds.contains(pointerPosition.x, pointerPosition.y)) {
                hoveredButton = button;
                return;
            }
        }
    }

    private void activate(MenuButton button) {
        if (button == null || !button.enabled) {
            return;
        }

        if (button.action == MenuAction.NEW_GAME) {
            game.startNewGame();
        } else if (button.action == MenuAction.LOAD_GAME) {
            statusText = "No saved caravan has been found.";
        } else if (button.action == MenuAction.QUIT) {
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        if (logoTexture != null) {
            logoTexture.dispose();
        }
        shapeRenderer.dispose();
        font.dispose();
    }

    private enum MenuAction {
        NEW_GAME,
        LOAD_GAME,
        QUIT
    }

    private static final class MenuButton {
        private final String label;
        private final boolean enabled;
        private final MenuAction action;
        private final Rectangle bounds = new Rectangle();

        private MenuButton(String label, boolean enabled, MenuAction action) {
            this.label = label;
            this.enabled = enabled;
            this.action = action;
        }
    }
}
