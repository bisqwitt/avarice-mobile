package com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingAssetKeys;

public class CloverValueStackRing extends SymbolValueStackRing {

    @Override
    public Symbol getSymbol() {
        return Symbol.CLOVER;
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_24;
    }
}
