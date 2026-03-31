package com.avaricious.items.upgrades.rings;

import com.avaricious.utility.RingAssetKeys;

public class DoubleSymbolValueDisableFruits extends AbstractRing {
    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_95;
    }

    @Override
    public String description() {
        return "Disable all fruits, all non-fruit symbols have their value doubled";
    }
}
