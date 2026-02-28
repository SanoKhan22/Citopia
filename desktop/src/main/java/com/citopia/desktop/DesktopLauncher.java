package com.citopia.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.citopia.CitopiaGame;

/**
 * Desktop launcher for Citopia.
 */
public class DesktopLauncher {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Citopia - Transport Tycoon");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new CitopiaGame(), config);
    }
}
