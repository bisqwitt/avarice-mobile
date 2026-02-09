package com.avaricious.upgrades.multAdditions.pattern;

import com.avaricious.Assets;
import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;
import com.avaricious.upgrades.multAdditions.MultAdditionUpgrade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PatternMultAdditionUpgrade extends MultAdditionUpgrade {

    private final double patternType;

    protected PatternMultAdditionUpgrade(UpgradeRarity rarity, long patternType) {
        super(rarity);
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
}
