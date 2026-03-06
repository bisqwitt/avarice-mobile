package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingAssetKeys;

public class IronValueStackRing extends SymbolValueStackRing {

    @Override
    public Symbol getSymbol() {
        return Symbol.IRON;
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_24;
    }
}
