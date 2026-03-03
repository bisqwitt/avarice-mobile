package com.avaricious.upgrades.multAdditions.pattern;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.multAdditions.MultAdditionRelic;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingKey;

import java.util.List;

public abstract class PatternMultAdditionRelic extends MultAdditionRelic {

    private final double patternType;

    protected PatternMultAdditionRelic(long patternType) {
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
    public RingKey ringKey() {
        return RingKey.RING_31;
    }
}
