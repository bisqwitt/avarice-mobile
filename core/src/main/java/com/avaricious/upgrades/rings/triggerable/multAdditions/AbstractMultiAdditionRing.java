package com.avaricious.upgrades.rings.triggerable.multAdditions;

import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.upgrades.rings.triggerable.AbstractScoreAdditionRing;

public abstract class AbstractMultiAdditionRing extends AbstractScoreAdditionRing {
    @Override
    protected ScoreDisplay.Type getPatternType() {
        return ScoreDisplay.Type.MULTI;
    }
}
