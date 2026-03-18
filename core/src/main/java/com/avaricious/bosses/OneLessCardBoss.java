package com.avaricious.bosses;

import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.rings.OneMoreCardAtStartOfRoundRing;

public class OneLessCardBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Draw one less Card at the start of round";
    }

    @Override
    public Upgrade loot() {
        return new OneMoreCardAtStartOfRoundRing();
    }
}
