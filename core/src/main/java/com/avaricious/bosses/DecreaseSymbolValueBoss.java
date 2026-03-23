package com.avaricious.bosses;

import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.cards.EitherDoublePointsOrHalveMulti;

public class DecreaseSymbolValueBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Value of Symbols are decreased by 1";
    }

    @Override
    public Upgrade loot() {
        return new EitherDoublePointsOrHalveMulti();
    }
}
