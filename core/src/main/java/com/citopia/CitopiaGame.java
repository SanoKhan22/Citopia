package com.citopia;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.citopia.assets.AssetRegistry;
import com.citopia.view.GameScreen;

/**
 * Main entry point for the Citopia transport tycoon game.
 * Manages screens and shared resources.
 */
public class CitopiaGame extends Game {

    public SpriteBatch batch;
    public AssetRegistry assets;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new AssetRegistry();
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        if (assets != null) {
            assets.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}
