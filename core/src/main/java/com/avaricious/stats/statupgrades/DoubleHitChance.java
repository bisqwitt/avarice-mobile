package com.avaricious.stats.statupgrades;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
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
