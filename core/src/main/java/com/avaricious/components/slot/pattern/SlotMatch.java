package com.avaricious.components.slot.pattern;

import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.Symbol;

import java.util.List;

public class SlotMatch {

    private final Symbol symbol;
    private final List<Slot> slots;

    public SlotMatch(Symbol symbol, List<Slot> slots) {
        this.symbol = symbol;
        this.slots = slots;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<Slot> getSlots() {
        return slots;
    }
}
