package com.avaricious.items.upgrades.rings.triggerable.multAdditions;

import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.items.upgrades.rings.triggerable.AbstractScoreAdditionRing;

public abstract class AbstractMultiAdditionRing extends AbstractScoreAdditionRing {
    @Override
    protected ScoreDisplay.Type getPatternType() {
        return ScoreDisplay.Type.MULTI;
    }
}
