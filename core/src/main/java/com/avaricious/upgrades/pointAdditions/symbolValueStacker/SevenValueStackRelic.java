package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingKey;

public class SevenValueStackRelic extends SymbolValueStackRelic {

    @Override
    public Symbol getSymbol() {
        return Symbol.SEVEN;
    }

    @Override
    public RingKey ringKey() {
        return RingKey.RING_24;
    }
}
