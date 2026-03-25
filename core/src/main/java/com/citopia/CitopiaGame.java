package com.citopia;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.citopia.screen.GameScreen;

/**
 * Main entry point for the Citopia transport tycoon game.
 * Manages screens and shared resources.
 */
public class CitopiaGame extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new GameScreen());
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}
