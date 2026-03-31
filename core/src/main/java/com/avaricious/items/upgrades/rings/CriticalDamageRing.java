package com.avaricious.items.upgrades.rings;

import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

public class CriticalDamageRing extends AbstractRing {
    @Override
    public String description() {
        return "Double " + Assets.I().greenText("Critical Hit") + " Multiplier";
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_4;
    }
}
