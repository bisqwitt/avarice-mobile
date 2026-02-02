package com.avaricious.stats.statupgrades;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CreditSpawnChance extends Stat {
    @Override
    public TextureRegion getTexture() {
        return Assets.I().get(AssetKey.COIN);
    }

    @Override
    public TextureRegion getShadowTexture() {
        return Assets.I().get(AssetKey.COIN_SHADOW);
    }
}
