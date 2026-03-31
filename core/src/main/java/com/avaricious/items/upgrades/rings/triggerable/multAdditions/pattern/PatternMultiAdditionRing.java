package com.avaricious.items.upgrades.rings.triggerable.multAdditions.pattern;

import com.avaricious.components.slot.pattern.PatternHitContext;
import com.avaricious.items.upgrades.rings.triggerable.ITriggerableOnConditionRing;
import com.avaricious.items.upgrades.rings.triggerable.multAdditions.AbstractMultiAdditionRing;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

import java.util.List;

public abstract class PatternMultiAdditionRing extends AbstractMultiAdditionRing implements ITriggerableOnConditionRing {

    private final double patternType;

    protected PatternMultiAdditionRing(long patternType) {
        this.patternType = patternType;
    }

    @Override
    public boolean condition(List<PatternHitContext> matches, PatternHitContext match) {
        return match.getSlots().size() == patternType;
    }

    @Override
    public int getValue() {
        return 3;
    }

    @Override
    public String description() {
        return Assets.I().redText("+3 Multi") + " on " + (int) patternType + "-of-a-kind's";
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_31;
    }

    @Override
    public TriggerablePer triggerableOn() {
        return TriggerablePer.PATTERN;
    }
}
