package com.avaricious.upgrades.rings.triggerable.multAdditions;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.upgrades.rings.triggerable.AbstractTriggerableRing;

public abstract class AbstractMultiAdditionRing extends AbstractTriggerableRing {

    public abstract int getMulti();

    @Override
    public void onTrigger() {
        pulse();
        addToPattern(PatternDisplay.Type.MULTI, getMulti());
    }
}
