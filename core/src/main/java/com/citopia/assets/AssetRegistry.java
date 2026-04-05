package com.citopia.assets;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class AssetRegistry implements Disposable {

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
            throw new IllegalArgumentException("Missing atlas region: " + regionName);
        }
        return textureRegion;
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
