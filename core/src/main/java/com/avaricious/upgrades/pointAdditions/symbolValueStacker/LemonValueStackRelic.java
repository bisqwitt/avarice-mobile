package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingKey;

public class LemonValueStackRelic extends SymbolValueStackRelic {

    @Override
    public Symbol getSymbol() {
        return Symbol.LEMON;
    }

    @Override
    public RingKey ringKey() {
        return RingKey.RING_24;
    }
}
