package com.avaricious.upgrades.multAdditions.pattern;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.multAdditions.MultAdditionRing;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

import java.util.List;

public abstract class PatternMultAdditionRing extends MultAdditionRing {

    private final double patternType;

    protected PatternMultAdditionRing(long patternType) {
        this.patternType = patternType;
    }

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return count == patternType;
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
