package com.avaricious.upgrades.rings;

import com.avaricious.utility.RingAssetKeys;

public class OnlyFruitsButNoHealingRing extends AbstractRing {
    @Override
    public RingAssetKeys keySet() {
        return null;
    }

    @Override
    public String description() {
        return "";
    }
}
