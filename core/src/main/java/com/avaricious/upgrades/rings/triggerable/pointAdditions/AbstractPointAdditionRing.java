package com.avaricious.upgrades.rings.triggerable.pointAdditions;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.upgrades.rings.triggerable.AbstractPatternAdditionRing;
import com.avaricious.upgrades.rings.triggerable.AbstractTriggerableRing;

public abstract class AbstractPointAdditionRing extends AbstractPatternAdditionRing {
    @Override
    protected PatternDisplay.Type getPatternType() {
        return PatternDisplay.Type.POINTS;
    }
}
