package com.avaricious.upgrades.multAdditions;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.Ring;

import java.util.List;

public abstract class MultAdditionRing extends Ring {

    public abstract boolean condition(List<Symbol> selection, long count);

    public abstract int getMulti();

    @Override
    public void hit() {
        pulse();
        addToPattern(PatternDisplay.Type.MULTI, getMulti());
    }
}
