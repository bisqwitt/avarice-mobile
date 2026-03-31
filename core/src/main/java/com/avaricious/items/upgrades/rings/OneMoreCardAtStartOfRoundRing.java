package com.avaricious.items.upgrades.rings;

import com.avaricious.utility.RingAssetKeys;

public class OneMoreCardAtStartOfRoundRing extends AbstractRing {
    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_93;
    }

    @Override
    public String description() {
        return "Draw one more Card at the start of round";
    }
}
