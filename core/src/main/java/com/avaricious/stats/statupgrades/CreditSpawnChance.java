package com.avaricious.stats.statupgrades;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
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
