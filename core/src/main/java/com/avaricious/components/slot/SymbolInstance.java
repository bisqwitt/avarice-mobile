package com.avaricious.components.slot;

import com.avaricious.stats.statupgrades.StatusUpgrade;

public class SymbolInstance {

    private final Symbol symbol;
    private final StatusUpgrade statusUpgrade;

    public SymbolInstance(Symbol symbol, StatusUpgrade statusUpgrade) {
        this.symbol = symbol;
        this.statusUpgrade = statusUpgrade;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public StatusUpgrade getStatUpgrade() {
        return statusUpgrade;
    }

}
