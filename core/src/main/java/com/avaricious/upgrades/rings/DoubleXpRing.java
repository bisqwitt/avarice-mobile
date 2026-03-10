package com.avaricious.upgrades.rings;

import com.avaricious.utility.RingAssetKeys;

public class DoubleXpRing extends AbstractRing {
    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_77;
    }

    @Override
    public String description() {
        return "Gain Double XP";
    }
}
