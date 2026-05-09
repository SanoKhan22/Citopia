package com.citopia.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public class AssetRegistry implements Disposable {

    private static final String INDEXED_NAME_PATTERN = "^(.*)_([0-9]+)$";

    private final TextureAtlas atlas;
    private final Map<String, Texture> standaloneTextures = new HashMap<>();
    private final Map<String, Music> musicFiles = new HashMap<>();

    public AssetRegistry() {
        this.atlas = new TextureAtlas("atlas/game-assets.atlas");
    }
    
    /** Load background music or long play audio. */
    public Music music(String filePath) {
        return musicFiles.computeIfAbsent(filePath,
                path -> Gdx.audio.newMusic(Gdx.files.internal(path)));
    }

    /** Load a standalone PNG file (not in the atlas) by its internal path, e.g. "full_Road.png" */
    public TextureRegion texture(String filePath) {
        Texture tex = standaloneTextures.computeIfAbsent(filePath,
                path -> new Texture(Gdx.files.internal(path)));
        return new TextureRegion(tex);
    }

    public TextureRegion region(AssetId assetId) {
        return region(assetId.regionName());
    }

    public TextureRegion region(String regionName) {
        TextureRegion textureRegion = atlas.findRegion(regionName);
        if (textureRegion == null) {
            java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile(INDEXED_NAME_PATTERN)
                .matcher(regionName);
            if (matcher.matches()) {
                String baseName = matcher.group(1);
                int index = Integer.parseInt(matcher.group(2));
                textureRegion = atlas.findRegion(baseName, index);
            }
        }
        if (textureRegion == null) {
            throw new IllegalArgumentException("Missing atlas region: " + regionName);
        }
        return textureRegion;
    }

    @Override
    public void dispose() {
        atlas.dispose();
        standaloneTextures.values().forEach(Texture::dispose);
        standaloneTextures.clear();
        musicFiles.values().forEach(Music::dispose);
        musicFiles.clear();
    }
}
