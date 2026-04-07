package com.citopia.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {
    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 8192;
        settings.maxHeight = 8192;
        settings.pot = true; // Power of Two for older generic support, matching 2D games standard
        settings.fast = true;
        settings.combineSubdirectories = true;
        settings.paddingX = 2;
        settings.paddingY = 2;
        settings.edgePadding = true;

        String inputDir = "../assets/citopiaassest/PNG/";
        String outputDir = "../assets/atlas/";
        String packFileName = "game-assets";

        System.out.println("Starting TexturePacker processing...");
        TexturePacker.process(settings, inputDir, outputDir, packFileName);
        System.out.println("TexturePacker processing complete!");
    }
}
