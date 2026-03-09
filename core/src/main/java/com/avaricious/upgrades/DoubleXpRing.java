package com.avaricious.upgrades;

import com.avaricious.utility.RingAssetKeys;

public class DoubleXpRing extends Ring {
    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_77;
    }

    @Override
    public void hit() {
    }

    @Override
    public String description() {
        return "Gain Double XP";
    }
}
