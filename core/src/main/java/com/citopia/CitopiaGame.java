package com.citopia;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.citopia.assets.AssetRegistry;
import com.citopia.model.PlayerState;
import com.citopia.view.GameScreen;
import com.citopia.view.MenuScreen;

/**
 * Main entry point for the Citopia transport tycoon game.
 * Manages screens and shared resources.
 */
public class CitopiaGame extends Game {

    public SpriteBatch batch;
    public AssetRegistry assets;
    /** Issue #24 – shared player economy state used by all screens. */
    public PlayerState playerState;

    @Override
    public void create() {
        batch       = new SpriteBatch();
        assets      = new AssetRegistry();
        playerState = new PlayerState();
        showMainMenu();
    }

    public void showMainMenu() {
        switchScreen(new MenuScreen(this));
    }

    public void startNewGame() {
        switchScreen(new GameScreen(this));
    }

    private void switchScreen(Screen nextScreen) {
        Screen previousScreen = getScreen();
        setScreen(nextScreen);
        if (previousScreen != null) {
            previousScreen.dispose();
        }
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        if (assets != null) {
            assets.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}
