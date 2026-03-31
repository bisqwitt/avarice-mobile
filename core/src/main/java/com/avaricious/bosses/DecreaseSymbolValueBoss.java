package com.avaricious.bosses;

import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.cards.EitherDoublePointsOrHalveMulti;

public class DecreaseSymbolValueBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Value of Symbols are decreased by 1";
    }

    @Override
    public AbstractUpgrade loot() {
        return new EitherDoublePointsOrHalveMulti();
    }
}
