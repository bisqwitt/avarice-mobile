package com.avaricious.components.slot;

import com.avaricious.stats.statupgrades.StatusRelic;

public class SymbolInstance {

    private final Symbol symbol;
    private final StatusRelic statusUpgrade;

    public SymbolInstance(Symbol symbol, StatusRelic statusUpgrade) {
        this.symbol = symbol;
        this.statusUpgrade = statusUpgrade;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public StatusRelic getStatUpgrade() {
        return statusUpgrade;
    }

}
