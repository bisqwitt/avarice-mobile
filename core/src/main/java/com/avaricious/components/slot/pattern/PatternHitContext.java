package com.avaricious.components.slot.pattern;

import com.avaricious.components.slot.Body;
import com.avaricious.components.slot.Symbol;

import java.util.List;

public class PatternHitContext {

    private final Symbol symbol;
    private final List<Body> bodies;

    public PatternHitContext(Symbol symbol, List<Body> slots) {
        this.symbol = symbol;
        this.bodies = slots;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<Body> getSlots() {
        return bodies;
    }
}
