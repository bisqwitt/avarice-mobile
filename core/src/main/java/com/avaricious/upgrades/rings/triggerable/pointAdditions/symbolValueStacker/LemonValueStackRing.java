package com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingAssetKeys;

public class LemonValueStackRing extends SymbolValueStackRing {

    @Override
    public Symbol getSymbol() {
        return Symbol.LEMON;
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_24;
    }
}
