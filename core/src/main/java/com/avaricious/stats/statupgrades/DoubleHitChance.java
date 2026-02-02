package com.avaricious.stats.statupgrades;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DoubleHitChance extends Stat {
    @Override
    public TextureRegion getTexture() {
        return Assets.I().get(AssetKey.RETRIGGER);
    }

    @Override
    public TextureRegion getShadowTexture() {
        return Assets.I().get(AssetKey.RETRIGGER_SHADOW);
    }
}
