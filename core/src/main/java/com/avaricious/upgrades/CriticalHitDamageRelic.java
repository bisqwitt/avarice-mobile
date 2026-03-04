package com.avaricious.upgrades;

import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

public class CriticalHitDamageRelic extends Relic {
    @Override
    public String description() {
        return "Double " + Assets.I().greenText("Critical Hit") + " Multiplier";
    }

    @Override
    public RingAssetKeys ringKey() {
        return RingAssetKeys.RING_4;
    }
}
