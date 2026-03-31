package com.avaricious.bosses;

import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.rings.OneMoreCardAtStartOfRoundRing;

public class OneLessCardBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Draw one less Card at the start of round";
    }

    @Override
    public AbstractUpgrade loot() {
        return new OneMoreCardAtStartOfRoundRing();
    }
}
