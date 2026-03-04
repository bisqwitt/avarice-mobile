package com.avaricious.upgrades;

import com.avaricious.utility.RingAssetKeys;

public class RandomUpgrade extends Relic {
    @Override
    public String description() {
        return "Random Relic";
    }

    @Override
    public RingAssetKeys ringKey() {
        return null;
    }
}
