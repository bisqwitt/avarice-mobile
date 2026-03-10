package com.avaricious.upgrades.rings.triggerable.multAdditions.pattern;

import com.avaricious.components.slot.pattern.PatternHitContext;
import com.avaricious.upgrades.rings.triggerable.ITriggerableOnConditionRing;
import com.avaricious.upgrades.rings.triggerable.ITriggerablePerPatternRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.AbstractMultiAdditionRing;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

import java.util.List;

public abstract class PatternMultiAdditionRing extends AbstractMultiAdditionRing implements ITriggerableOnConditionRing, ITriggerablePerPatternRing {

    private final double patternType;

    protected PatternMultiAdditionRing(long patternType) {
        this.patternType = patternType;
    }

    @Override
    public boolean condition(List<PatternHitContext> matches, PatternHitContext match) {
        return match.getSlots().size() == patternType;
    }

    @Override
    public int getMulti() {
        return 4;
    }

    @Override
    public String description() {
        return "Additional " + Assets.I().redText(getMulti() + "") + " to " + Assets.I().redText("multiplier") + " on " + (int) patternType + "-of-a-kind's";
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_31;
    }
}
