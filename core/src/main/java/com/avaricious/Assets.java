package com.avaricious;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class Assets {

    private static Assets instance;

    public static Assets I() {
        return instance == null ? instance = new Assets() : instance;
    }

    private Assets() {
    }

    private final AssetManager manager = new AssetManager();

    private TextureAtlas textureAtlas;
    private Map<AssetKey, TextureRegion> cachedTextures = new HashMap<>();

    public void load() {
        manager.load("atlases.atlas", TextureAtlas.class);
    }

}

