package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingAssetKeys;

public class CloverValueStackRelic extends SymbolValueStackRelic {

    @Override
    public Symbol getSymbol() {
        return Symbol.CLOVER;
    }

    @Override
    public RingAssetKeys ringKey() {
        return RingAssetKeys.RING_24;
    }
}
