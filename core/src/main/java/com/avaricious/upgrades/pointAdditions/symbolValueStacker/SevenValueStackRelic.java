package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingAssetKeys;

public class SevenValueStackRelic extends SymbolValueStackRelic {

    @Override
    public Symbol getSymbol() {
        return Symbol.SEVEN;
    }

    @Override
    public RingAssetKeys ringKey() {
        return RingAssetKeys.RING_24;
    }
}
