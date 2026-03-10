package com.avaricious.upgrades.rings.triggerable.pointAdditions;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.upgrades.rings.triggerable.AbstractTriggerableRing;

public abstract class AbstractPointAdditionRing extends AbstractTriggerableRing {

    public abstract int getPoints();

    @Override
    protected void onTrigger() {
        pulse();
        addToPattern(PatternDisplay.Type.POINTS, getPoints());
    }
}
