package com.citopia.assets;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class AssetRegistry implements Disposable {

    private static final String INDEXED_NAME_PATTERN = "^(.*)_([0-9]+)$";

    private final TextureAtlas atlas;

    public AssetRegistry() {
        this.atlas = new TextureAtlas("atlas/game-assets.atlas");
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
    }
}
