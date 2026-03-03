package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingKey;

public class IronValueStackRelic extends SymbolValueStackRelic {

    @Override
    public Symbol getSymbol() {
        return Symbol.IRON;
    }

    @Override
    public RingKey ringKey() {
        return RingKey.RING_24;
    }
}
