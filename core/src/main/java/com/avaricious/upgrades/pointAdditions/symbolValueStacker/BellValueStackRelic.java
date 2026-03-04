package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingAssetKeys;

public class BellValueStackRelic extends SymbolValueStackRelic {

    @Override
    public Symbol getSymbol() {
        return Symbol.BELL;
    }

    @Override
    public RingAssetKeys ringKey() {
        return RingAssetKeys.RING_24;
    }
}
