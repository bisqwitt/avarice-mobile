package com.avaricious.stats.statupgrades;

import com.avaricious.components.RingBar;
import com.avaricious.upgrades.rings.CriticalHitDamageRing;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
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
        return RingBar.I().ringOwned(CriticalHitDamageRing.class) ? 4 : 2;
    }

}
