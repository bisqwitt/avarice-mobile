package com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingAssetKeys;

public class SevenValueStackRing extends SymbolValueStackRing {

    @Override
    public Symbol getSymbol() {
        return Symbol.SEVEN;
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_24;
    }
}
