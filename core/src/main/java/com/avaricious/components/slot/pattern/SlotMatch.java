package com.avaricious.components.slot.pattern;

import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.Symbol;
import com.avaricious.components.slot.SymbolSlot;

import java.util.List;

public class SlotMatch {

    private final Symbol symbol;
    private final List<SymbolSlot> slots;

    public SlotMatch(Symbol symbol, List<SymbolSlot> slots) {
        this.symbol = symbol;
        this.slots = slots;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<SymbolSlot> getSlots() {
        return slots;
    }
}
