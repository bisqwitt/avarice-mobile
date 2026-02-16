package com.avaricious.stats.statupgrades;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.avaricious.upgrades.CriticalHitDamageRelic;
import com.avaricious.upgrades.RelicManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CriticalHitChance extends Stat {

    @Override
    public TextureRegion getTexture() {
        return Assets.I().get(AssetKey.CRITICAL_HIT);
    }

    @Override
    public TextureRegion getShadowTexture() {
        return Assets.I().get(AssetKey.CRITICAL_HIT_SHADOW);
    }

    public int criticalHitMultiplier() {
        return RelicManager.I().relicOwned(CriticalHitDamageRelic.class) ? 4 : 2;
    }

}
