package com.avaricious.upgrades.rings.triggerable.pointAdditions;

import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.upgrades.rings.triggerable.AbstractScoreAdditionRing;

public abstract class AbstractPointAdditionRing extends AbstractScoreAdditionRing {
    @Override
    protected ScoreDisplay.Type getPatternType() {
        return ScoreDisplay.Type.POINTS;
    }
}
