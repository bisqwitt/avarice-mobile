package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingAssetKeys;

public class DiamondValueStackRelic extends SymbolValueStackRelic {

    @Override
    public Symbol getSymbol() {
        return Symbol.DIAMOND;
    }

    @Override
    public RingAssetKeys ringKey() {
        return RingAssetKeys.RING_24;
    }
}
