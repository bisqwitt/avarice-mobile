package com.avaricious.components.slot;

import com.badlogic.gdx.math.Vector2;

public class SymbolSlot extends Slot {
    public SymbolSlot(Vector2 pos) {
        super(pos);
    }

    private boolean isEmphasized = false;

    public void setEmphasized(boolean emphasized) {
        isEmphasized = emphasized;
    }

    public boolean isEmphasized() {
        return isEmphasized;
    }
}
