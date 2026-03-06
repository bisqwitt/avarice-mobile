package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingAssetKeys;

public class BellValueStackRing extends SymbolValueStackRing {

    @Override
    public Symbol getSymbol() {
        return Symbol.BELL;
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_24;
    }
}
