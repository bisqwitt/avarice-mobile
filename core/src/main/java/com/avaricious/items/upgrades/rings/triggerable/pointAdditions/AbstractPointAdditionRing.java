package com.avaricious.items.upgrades.rings.triggerable.pointAdditions;

import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.items.upgrades.rings.triggerable.AbstractScoreAdditionRing;

public abstract class AbstractPointAdditionRing extends AbstractScoreAdditionRing {
    @Override
    protected ScoreDisplay.Type getPatternType() {
        return ScoreDisplay.Type.POINTS;
    }
}
